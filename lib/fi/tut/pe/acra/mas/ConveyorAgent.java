package fi.tut.pe.acra.mas;

/*
 * @author VlVi / AnLo
 * @version 21042010
 */

import java.util.HashMap;
import java.util.Map;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import java.util.Arrays;

import fb.rt.ConvAgent;

import jade.content.ContentManager;
import jade.content.lang.sl.SLCodec;
import jade.content.onto.OntologyException;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.core.behaviours.Behaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.FIPAManagementOntology;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.JADEAgentManagement.JADEManagementOntology;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.wrapper.ContainerController;


import java.util.Timer;
import java.util.TimerTask;
import java.util.Random;


@SuppressWarnings("serial")
public class ConveyorAgent extends Agent implements ConvAgent {

	// Let us use loggers instead of System.out.println
	// Define a logger for this class and setup your own logger levels
	private static Logger logger = Logger.getLogger(ConveyorAgent.class.getName());





	// Static method for creating automatically instances of this agent
	// DO NOT MODIFY!
	public static ConveyorAgent init(String nickname) {
		ContainerController  cc = CCFactory.getCCInstance();
		ConveyorAgent ca = new ConveyorAgent();
		try{
			logger.info("CC " + cc);
			cc.acceptNewAgent(nickname, ca).start();
		} catch(Exception ex){
			logger.log(Level.WARNING, "Exception creating Conveyor Agent: " + nickname);
			ex.printStackTrace();
		}
		return ca;
	}

	private SLCodec codec = new SLCodec();
	// OPTIONAL: Define the instance of your own communication ontologies here (if needed)
	private ContentManager cm = (ContentManager)getContentManager();
	private AID conveyorAgent;

	// The status of the conveyor motor. (Output variable)
	private boolean motorValue;

	// Current position of the pallet. (Input variable)
	private int palletPosition;

	// Path between the conveyor agents. "NOT_FOUND" - if the path is not found
	private String path;

	// Neighbor agent for the this agent.
	private Vector neighbors = new Vector();

	// Origin of the path
	private String from;

	// Destination of the path
	private String to;


	// Status flags

	private boolean Loading = false;

	private boolean Loaded = false;

	private boolean Waiting4Status = false;

	private boolean Waiting4Start = false;

	private boolean Unloading = false;

	private boolean Endpos = false;

	private boolean Startpos = false;

	private boolean busy = false;

	// Senders list


    private Map<AID, String> senderprods = new HashMap<AID, String>();
	private Vector<AID> senderids = new Vector<AID>();


	// product being manufactured

	private String CurrentProdID;


//Coco agent........


	private String RecipeAndPath;

	private Vector RecipeV = new Vector();
	private Vector PathV = new Vector();

	// Destination of the path
	private String NextRecipePoint;

private String RouteControlResult;


 Timer timer;
	  Random generator = new Random();

	 //Vector that holds, Paths founded....
	private Vector PathsFoundV = new Vector();




	private Vector NextPoint;


	/**
	 * A flag that should be set by an agent when pallet must be removed.
	 * This flag is reset by FB instance to once the flag is detected and processed.
	 *
	 * The flag is off by default.
	 */
	public boolean removePallet = false;


	/**
	 * A flag that should be set by an agent when pallet load is started.
	 * This flag is reset by FB instance to once the flag is detected and processed.
	 *
	 * The flag is off by default.
	 */
	public boolean palletLoad = false;

	/**
	 * A flag that should be set by an agent when pallet is loaded (e.g. in position 70).
	 * The motor should be stopped then before trying to unload the pallet.
	 * This flag is reset by FB instance to once the flag is detected and processed.
	 *
	 * The flag is off by default.
	 */
	public boolean palletLoaded = false;

	/**
	 * A flag that should be set by an agent when path computation is completed.
	 * This flag is reset by FB instance to once the flag is detected and processed.
	 *
	 * The flag is off by default.
	 */
	public boolean pathFound = false;



	/*
	 * Agent initializations
	 */

	// Use the setup method for defining the agent's initialization.
	protected void setup() {
		super.setup();

		// TODO: (OPTIONAL) Register the communication ontology & the language to be used by the agents
		cm.registerOntology(FIPAManagementOntology.getInstance());
		cm.registerOntology(JADEManagementOntology.getInstance());

		// TODO: (OPTIONAL) Register your own ontologies

		cm.registerLanguage(codec);

		// Prepare an agent description for the Directory Facilitator (DF)
        DFAgentDescription dfad = new DFAgentDescription();
        dfad.setName(this.getAID());
        dfad.addLanguages(codec.getName());

        // Prepare description of the service(s) offered by this agent
        String serviceName = "JADEConveyorService";
        String typeName = "Transportation";
        String serviceOwnership = "TTE-5306 ACRA Group 8 - " + this.getAID().getName();
        ServiceDescription sd = new ServiceDescription();
        sd.setName(serviceName);
        logger.info(String.format("Service name: %s", serviceName));
        sd.addLanguages(codec.getName());
        sd.setType(typeName);
        logger.info(String.format("Service type: %s", typeName));
        sd.setOwnership(serviceOwnership);
        logger.info(String.format("Service ownership: %s", serviceOwnership));

        // Added services to DFAgentDescription
        dfad.addServices(sd);

        // Register the service in the yellow pages or Directory Facilitator (DF)
         try {
            DFService.register(this, dfad);
        }
        catch(FIPAException fe) {
            fe.printStackTrace();
            doDelete();
        }

		// Print out a welcome message
		logger.info(String.format("Hello! Conveyor-Agent %s is ready.", getAID().getName()));

		// TODO: Add other behaviors here
		addBehaviour(new OrderEntryRequestsServer(this)); // This is just an example!
		addBehaviour(new StartRequestsServer(this));
		addBehaviour(new LoadRequestsServer(this));
		addBehaviour(new UnloadRequestsServer(this));
		addBehaviour(new UnloadConfServer(this));
		addBehaviour(new ReadinessReplyServer(this));


		//CocoAgent


		addBehaviour(new FindPathServer());


		//doDelete();
	}

	/*
	 * Agent termination routine
	 */

	// Use the takeDown method for running clean-up operations before terminating an agent.
	protected void takeDown() {

		// Deregister from the yellow pages
		try {
			DFService.deregister(this);
		}
		catch (FIPAException fe) {
			logger.log(Level.WARNING, "Problem during de-registration from DF", fe);
		}
		catch (Exception e){
			logger.log(Level.SEVERE, "Unknown error ocurred", e);
		}

		// Print out a dismissal message
		logger.info(String.format("Conveyor-Agent %s terminating.", getAID().getName()));
	}


	/*
	 * This inner class implements the behavior that will search for alternative
	 * paths in collaboration with other agents. This behavior will be normally
	 * triggered or added to the agent when the production recipe is not complete
	 * but the start and beginning are correct. A Conveyor Agent (CA) starts
	 * negotiation with other CAs in order to determine the possible neighbors.
	 * Remember: One shot behaviors are executed only once!
	 */











//*******************************************************Find Path

private class FindPath extends OneShotBehaviour {

		// The default constructor of this inner class
		private FindPath(Agent a){
			super(a);
		}

		public void action(){
			/* The idea is that this behavior don't require messages so that other behavior
			 * can trigger it. You can trigger a behavior in many ways: add a behavior from
			 * inside another behavior or the agent can send a message to itself that upon
			 * reception will trigger the behavior.
			 */


				// TIP: Make sure to catch all the thrown exceptions!
				// TODO: Starting from your current position, send messages to your
				// neighbors asking them about the destination. The message has to be
				// propagated to every next neighbor agent until the final destination is reached.
				// The resulting path will consist on a String containing the names of the agents
				// in the path (comma separated). If not path can be found, the answer should be:
				// "NOT_FOUND".



	//	We clear the vector where proposed paths to the recipe will be storage for later evaluation...
				PathsFoundV.clear();
			//Starts to listen for PathFound informs....
			addBehaviour(new ListenForPathFoundServer());



			String PathLogMsg=NextRecipePoint+":"+getLocalName();

			//Missing to validate if the conveyor that starts the FindPath is the same that NextRecipePoint

			//FindPath with Next target in recipe
			sendToAgents_Request(neighbors,"request-to-FindPath",PathLogMsg);
			Say("I started a FindPath talk..");


    Say("Timmer for PathFound proposals starts..");
    //timeout timer
	timer = new Timer();
    timer.schedule(new TimeOutForProposals(), 15 * 100);


		}

	}



class TimeOutForProposals extends TimerTask {
    public void run() {
      Say("Time's up!,lets check path found proposals..");

      timer.cancel(); //Terminates the timer thread

      //Lets check PathFound proposals..

      System.out.println(PathsFoundV);


      //Convert vector to string
String[] st =new String[PathsFoundV.size()];
PathsFoundV.toArray(st);

String SelectedPath="";

/*
 for (int i = 1; i < st.length; ++i) {
 	PathAndRecStr=PathAndRecStr+","+st[i];
      }

*/


if (st.length>0){
//Path found search succesfull......

//Select a random path from those found...
int randomIndex = generator.nextInt(st.length);

SelectedPath=st[randomIndex];

Say("Randomly i chose this path: "+SelectedPath);

refillPathV(SelectedPath);
		RecipeAndPath=FormatRecipeAndPath();
		RouteControlResult=NextPointInPath();
		//Inform that the algorithm has finished...
	addBehaviour(new RouteControlDecisionComplete(ConveyorAgent.this));


}
else
{
//Did not find any path...

RouteControlResult="FindPathTimeOut";
addBehaviour(new RouteControlDecisionComplete(ConveyorAgent.this));
}










    }
  }






		//Cyclic behaviour: Find Path Protocol....

			private class FindPathServer extends CyclicBehaviour {
		public void action() {
			MessageTemplate template = MessageTemplate.and(MessageTemplate.MatchConversationId("request-to-FindPath"),
					MessageTemplate.MatchPerformative(ACLMessage.REQUEST)); //To accept just request-to-FindPath Id conversation
			ACLMessage msg = myAgent.receive(template);
			if (msg != null) {
				// Request,FindPath,  Message received. Process it
				String MessageString = msg.getContent();
				String PathLogMsg="";

				try{


				String AlgorithmResult=FindPathAlgorithm(MessageString);

		Say("Algorithm Result: "+AlgorithmResult);


        if (AlgorithmResult.equals("WrongPath-1")){


        	PathLogMsg=appendToLog(MessageString);
          	sendToAgents_Inform(getInitiator(MessageString),"WrongPath",PathLogMsg);
        	Say("WrongPath1, inform to Initiator");

        }
        else if (AlgorithmResult.equals("PathFound")) {

        	PathLogMsg=appendToLog(MessageString);
        	sendToAgents_Inform(getInitiator(MessageString),"PathFound",PathLogMsg);

        	Say("FoundPath, inform to Initiator");
        }
          else if (AlgorithmResult.equals("PropagateSearch")) {

          	PathLogMsg=appendToLog(MessageString);
          	sendToAgents_Request(neighbors,"request-to-FindPath",PathLogMsg);

        	Say("Propaget serach, Request to neighbors");



        }
          else if (AlgorithmResult.equals("WrongPath-2")) {

          	PathLogMsg=appendToLog(MessageString);
          	sendToAgents_Inform(getInitiator(MessageString),"WrongPath",PathLogMsg);

        	Say("WrongPath2, inform to Initiator");
        }





		//	String pi=getAID();





		} catch(Exception ex){
			//Logger.log(Level.WARNING, "Exception executing functions on Conveyor Agent: " + "nickname");
			Say("Something went wrong");
			ex.printStackTrace();
		}


			}
			else {
				block();
			}
		}
	}




//*******************************************************Find Path ENDS




//*******************************************************Routing Control

//Cyclic behaviour: Waits for inform with FoundPath....
			private class ListenForPathFoundServer extends CyclicBehaviour {
		public void action() {
			MessageTemplate template = MessageTemplate.and(MessageTemplate.MatchConversationId("PathFound"),
					MessageTemplate.MatchPerformative(ACLMessage.INFORM)); //To accept just PathFound Id conversation
			ACLMessage msg = myAgent.receive(template);
			if (msg != null) {
				// PathFound with target in the message....
				String PathFoundMsg = msg.getContent();

	//We storage the proposed paths founded in the Search path process...
	PathsFoundV.add(PathFoundMsg);



			}
			else {
				block();
			}
		}
	}		//Inform that the algorithm has finished...			MessageTemplate template = MessageTemplate.and(MessageTemplate.MatchConversationId("PathFound"),		//Inform that the algorithm has finished...			MessageTemplate template = MessageTemplate.and(MessageTemplate.MatchConversationId("PathFound"),		//Inform that the algorithm has finished...			MessageTemplate template = MessageTemplate.and(MessageTemplate.MatchConversationId("PathFound"),  // End of inner class OfferRequestsServer





	//One shot behaviour: RouteControlBehaviour

		private class RouteControlDecision extends OneShotBehaviour {

		// The default constructor of this inner class
		private RouteControlDecision (Agent a){
			super(a);
		}

		public void action(){

		Say("Route control Behaviour started");




		splitRecipeAndPath(RecipeAndPath);
		//System.out.println(RecipeV);
		//System.out.println(PathV);

		PathV.remove(getLocalName()); //Remove Actual Point from PathToGo....


		boolean PathToGoEmpty = IsVectorEmpty(PathV);


		if (PathToGoEmpty){
		RecipeV.remove(getLocalName()); //Remove Actual Point from Recipe....

		boolean RecipeEmpty = IsVectorEmpty(RecipeV);

			if (RecipeEmpty){
			//REcipe completed...Remove from line
			Say("Here this story ends, ciao pallet...");


			RouteControlResult="LastPoint";

			//Inform that the algorithm has finished...
		addBehaviour(new RouteControlDecisionComplete(ConveyorAgent.this));

			}
				else{

			//FindPath with next target in recipe
			NextRecipePoint=NextPointInRecipe();
			addBehaviour(new FindPath(ConveyorAgent.this));



				}//RecipeEmpty


		}
		else{
			//Next equipment to route



		RecipeAndPath=FormatRecipeAndPath();
		RouteControlResult=NextPointInPath();
		//Inform that the algorithm has finished...
	addBehaviour(new RouteControlDecisionComplete(ConveyorAgent.this));



		}

	}
		}


//Onse shot behaviour: RouteControlDecisionComplete

		private class RouteControlDecisionComplete extends OneShotBehaviour {

		// The default constructor of this inner class
		private RouteControlDecisionComplete (Agent a){
			super(a);
		}

		public void action(){


//When this exectutes, means that the route decision algorithm has finished.....
//	sendToAgents_Request(NextPointInPathV(),"Start",FormatRecipeAndPath());


	//RouteControlResult ("FindPathTimeOut": Timeout reached without PathFound...
						  //"LastPoint": The Pallet is in the last Point of the recipe..
						  //Any other: Agent name to which the pallet has to be transfered....

//RecipeAndPath (Info about pallet recipe, path=...Dominico, u send this in the next loadRequeste....





//**********************NEW part


if (RouteControlResult.equals("LastPoint")) {
	Say("I was the last one!");
    // send completion msg
    addBehaviour(new ProdCompleted(ConveyorAgent.this));


}

else if (RouteControlResult.equals("FindPathTimeOut"))
Say("No one knows how to take this guy...");
else
addBehaviour(new GetConvStatus(ConveyorAgent.this));










			}
		}

//*******************************************************Routing Control ENDS.........


//************************* conveyor control *******************


// queries the  next conveyor status
	private class GetConvStatus extends OneShotBehaviour {

		// The default constructor of this inner class
		private GetConvStatus(Agent a){
			super(a);

		}

		private AID nextn;
		//private String name;

		public void action() {

			nextn = new AID(RouteControlResult, AID.ISLOCALNAME);

			ACLMessage msg = new ACLMessage(ACLMessage.QUERY_IF);
			msg.addReceiver(nextn);
			msg.setConversationId("ConveyorStatus");
			msg.setReplyWith(CurrentProdID);
			send(msg);
			Waiting4Status = true;
		}

	}




// serves conveyor status queries

	private class OrderEntryRequestsServer extends CyclicBehaviour {
		private MessageTemplate template;

		// The default constructor of this inner class
		private OrderEntryRequestsServer(Agent a){
			super(a);
			/* Prepare the template to get the messages from other agents
			 * An example of template is provided below, but you can (and most
			 * likely should!) modify it according the communication needs.
			 * Consult the FIPA specifications, the programming guides, and the
			 * JADE API for more details.
			 */
			// TODO: Modify message template
			template = MessageTemplate.and(MessageTemplate.MatchConversationId("ConveyorStatus"),
					MessageTemplate.MatchPerformative(ACLMessage.QUERY_IF)); //This is just an example!
		}

		private String prodID;
		private AID sender;

		public void action() {
			// Receive the messages sent to this agent
			ACLMessage msg = myAgent.receive(template);
			if(msg != null) {

				// TIP: Make sure to catch all the thrown exceptions!
				// TODO: Parse the message. You can also use the ContentManager and your own ontology (i.e., convert to Java objects)
				// TODO: Determine if the conveyor is available for new orders
				// TODO: If it is available, confirm to the OEA that production can start
				// TODO: If it is not available, inform to the OEA that system is currently busy ("False" for the OE FB)

				prodID = msg.getReplyWith();

				if(!busy) {
					ACLMessage reply = msg.createReply();
					//reply.setConversationId("request-get-ConveyorStatus");
 					reply.setPerformative(ACLMessage.INFORM);
 					reply.setContent("Free");
 					reply.setReplyWith(prodID);
                	myAgent.send(reply);

					}
				else {

					// add senders to senders list

					sender = msg.getSender();
					addSender(sender, prodID);

					ACLMessage reply = msg.createReply();
					//reply.setConversationId("request-get-ConveyorStatus");
 					reply.setPerformative(ACLMessage.INFORM);
 					reply.setContent("Busy");
 					reply.setReplyWith(prodID);
                	myAgent.send(reply);
					}

			}
			else{
				// TIP: Don't forget to block the behavior when is not in use!
				block();
			}

		}

	}


// serves "conveyor free" messages and sends a start-request

	private class ReadinessReplyServer extends CyclicBehaviour {
		private MessageTemplate template;

		// The default constructor of this inner class
		private ReadinessReplyServer(Agent a){ //(agent a, ProdId)
			super(a);
			//this.prodID = prodID;
			/* Prepare the template to get the messages from other agents
			 * An example of template is provided below, but you can (and most
			 * likely should!) modify it according the communication needs.
			 * Consult the FIPA specifications, the programming guides, and the
			 * JADE API for more details.
			 */
			// TODO: Modify message template
			template = MessageTemplate.and(MessageTemplate.MatchConversationId("ConveyorStatus"),
					MessageTemplate.MatchPerformative(ACLMessage.INFORM));
		}

		private String convstatus;
		private String prodID;

		public void action() {
			// Receive the messages sent to this agent
			ACLMessage msg = myAgent.receive(template);
			if(msg != null) {
				convstatus=msg.getContent();
				if(convstatus.equals("Free")) {
					if(Waiting4Status) {
						addBehaviour(new LoadReq(ConveyorAgent.this));
						Waiting4Status = false;
					}

				}

			}
			else{
				// TIP: Don't forget to block the behavior when is not in use!
				block();
			}

		}
	}


	/*
	 * This inner class implements the behavior that will listen and serve
	 * requests for starting production. Originally the requests should come from
	 * an Order Entry Agent (OEA), but they can also come from other type of agent.
	 *
	 * The requesting agent should send a string containing the production recipe
	 * that should be followed. The recipe includes comma separated IDs of the
	 * stations and conveyors that a pallet should visit in order to produce.
	 *
	 * The Conveyor Agent starting the production should acknowledge the OEA with
	 * a message informing that the production order was accepted.
	 */


// serves start-request
	private class StartRequestsServer extends CyclicBehaviour {
		private MessageTemplate template;

		// The default constructor of this inner class
		private StartRequestsServer(Agent a){
			super(a);
			/* Prepare the template to get the messages from other agents
			 * An example of template is provided below, but you can (and most
			 * likely should!) modify it according the communication needs.
			 * Consult the FIPA specifications, the programming guides, and the
			 * JADE API for more details.
			 */
			// TODO: Modify message template
			template = MessageTemplate.and(MessageTemplate.MatchConversationId("start-request"),
					MessageTemplate.MatchPerformative(ACLMessage.REQUEST)); //This is just an example!

		}

		private String prodID;

		public void action() {

			// Receive the messages sent to this agent
			ACLMessage msg = myAgent.receive(template);
			if(msg != null) {
				// TIP: Make sure to catch all the thrown exceptions!
				// TODO: Parse the message. You can also use the ContentManager and your own ontology (i.e., convert to Java objects)
				// TODO: Verify if the production recipe is valid (correct destination? correct path?)
				// TODO: If recipe is valid, proceed with production and order underlying FBs to move pallets
				// TODO: When production is finished, send a message to the OEA and inform "DONE".
				// TODO: If recipe is not valid, trigger a FindPath behavior or reply with an error message to OEA


				if(!busy) {


					// gets Path from message
					RecipeAndPath=msg.getContent();

					// sets current product
					prodID = msg.getReplyWith();
					CurrentProdID = prodID;

					// inputs pallet: (we use the same event we use to remove pallet)

					removePallet = true;

					// sets status, turns motor on
					Loading = true;
					busy = true;
					motorValue = true;  // motor on
					//System.out.println("Motor on!")


					// send reply message
					ACLMessage reply = msg.createReply();
					reply.setConversationId("start-request-reply");
 					reply.setPerformative(ACLMessage.AGREE);
 					reply.setReplyWith(prodID);
 					reply.setContent("Loading");
                	myAgent.send(reply);

				}
				else // if busy refuse
				{

					ACLMessage reply = msg.createReply();
					reply.setConversationId("start-request-reply");
 					reply.setPerformative(ACLMessage.REFUSE);
 					reply.setReplyWith(prodID);
 					reply.setContent("Busy");
                	myAgent.send(reply);
				}


				}
			else{
				// TIP: Don't forget to block the behavior when is not in use!
				block();
			}

		}

	}


// sends a load-request
	private class LoadReq extends OneShotBehaviour {

		// The default constructor of this inner class
		private LoadReq(Agent a){
			super(a);

		}

		private AID nextn;

		public void action() {


			nextn = new AID(RouteControlResult,AID.ISLOCALNAME);

			ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
			msg.addReceiver(nextn);
			msg.setConversationId("load-request");
		    msg.setContent(RecipeAndPath);
		    msg.setReplyWith(CurrentProdID);
			send(msg);
			Waiting4Start = true;


		}

	}

// serves load-request and initiates loading
// IMPORTANTE!: bisogna separare la start-request dal controllo sul conveyor //
	private class LoadRequestsServer extends CyclicBehaviour {
		private MessageTemplate template;

		// The default constructor of this inner class
		private LoadRequestsServer(Agent a){
			super(a);
			/* Prepare the template to get the messages from other agents
			 * An example of template is provided below, but you can (and most
			 * likely should!) modify it according the communication needs.
			 * Consult the FIPA specifications, the programming guides, and the
			 * JADE API for more details.
			 */
			// TODO: Modify message template
			template = MessageTemplate.and(MessageTemplate.MatchConversationId("load-request"),
					MessageTemplate.MatchPerformative(ACLMessage.REQUEST)); //This is just an example!

		}

		private String prodID;

		public void action() {

			// Receive the messages sent to this agent
			ACLMessage msg = myAgent.receive(template);
			if(msg != null) {
				// TIP: Make sure to catch all the thrown exceptions!
				// TODO: Parse the message. You can also use the ContentManager and your own ontology (i.e., convert to Java objects)
				// TODO: Verify if the production recipe is valid (correct destination? correct path?)
				// TODO: If recipe is valid, proceed with production and order underlying FBs to move pallets
				// TODO: When production is finished, send a message to the OEA and inform "DONE".
				// TODO: If recipe is not valid, trigger a FindPath behavior or reply with an error message to OEA


				if(!busy) {

					// gets Path from message
					RecipeAndPath=msg.getContent();

					// sets current product
					prodID = msg.getReplyWith();
					CurrentProdID = prodID;


					// sets status, turns motor on
					Loading = true;
					busy = true;
					motorValue = true;  // motor on
					//System.out.println("Motor on!")


					// send reply message
					ACLMessage reply = msg.createReply();
					reply.setConversationId("load-request-reply");
 					reply.setPerformative(ACLMessage.AGREE);
 					reply.setReplyWith(prodID);
 					reply.setContent("Loading");
                	myAgent.send(reply);

				}
				else // if busy refuse
				{

					ACLMessage reply = msg.createReply();
					reply.setConversationId("load-request-reply");
 					reply.setPerformative(ACLMessage.REFUSE);
 					reply.setReplyWith(prodID);
 					reply.setContent("Busy");
                	myAgent.send(reply);
				}


				}
			else{
				// TIP: Don't forget to block the behavior when is not in use!
				block();
			}

		}

	}


// serves load-request replies, initiates unloading and send load-query
	private class UnloadRequestsServer extends CyclicBehaviour {
		private MessageTemplate template;

		// The default constructor of this inner class
		private UnloadRequestsServer(Agent a){
			super(a);
			/* Prepare the template to get the messages from other agents
			 * An example of template is provided below, but you can (and most
			 * likely should!) modify it according the communication needs.
			 * Consult the FIPA specifications, the programming guides, and the
			 * JADE API for more details.
			 */
			// TODO: Modify message template
			template = MessageTemplate.MatchConversationId("load-request-reply"); //This is just an example!

		}

		private String convstatus;

		public void action() {

			// Receive the messages sent to this agent
			ACLMessage msg = myAgent.receive(template);
			if(msg != null) {
				// TIP: Make sure to catch all the thrown exceptions!
				// TODO: Parse the message. You can also use the ContentManager and your own ontology (i.e., convert to Java objects)
				// TODO: Verify if the production recipe is valid (correct destination? correct path?)
				// TODO: If recipe is valid, proceed with production and order underlying FBs to move pallets
				// TODO: When production is finished, send a message to the OEA and inform "DONE".
				// TODO: If recipe is not valid, trigger a FindPath behavior or reply with an error message to OEA

				convstatus = msg.getContent();

				if(convstatus.equals("Loading")){

					if(Waiting4Start){

						// sets status, turns on motor

						Waiting4Start = false;
						Unloading = true;
						motorValue = true;  // motor on
						System.out.println("Unloading : Motor on!");


						// sends a load-query message

						ACLMessage reply = msg.createReply();
						reply.setConversationId("load-query");
 						reply.setPerformative(ACLMessage.QUERY_IF);
 						reply.setContent(RecipeAndPath);
						reply.setReplyWith(CurrentProdID);
 						//reply.setContent("");
                		myAgent.send(reply);
					}
				}
				else {
					Waiting4Start = false;
					addBehaviour(new GetConvStatus(ConveyorAgent.this));
				}

			}
			else{
				// TIP: Don't forget to block the behavior when is not in use!
				block();
			}

		}

	}


// confirm that pallet has been loaded //
	private class LoadConf extends OneShotBehaviour {

		// The default constructor of this inner class
		private LoadConf(Agent a){
			super(a);

		}

		//private AID nextn;
		//private String name;
		MessageTemplate template = MessageTemplate.and(MessageTemplate.MatchConversationId("load-query"),
			MessageTemplate.MatchPerformative(ACLMessage.QUERY_IF));


		public void action() {

					 ACLMessage msg = myAgent.receive(template);
					 if(msg != null) {
						ACLMessage reply = msg.createReply();
						reply.setConversationId("load-query-reply");
 						reply.setPerformative(ACLMessage.CONFIRM);
 						reply.setContent("Loading Complete");
                		myAgent.send(reply);
                		Startpos = false;


					 }
					 else{
				// TIP: Don't forget to block the behavior when is not in use!
				block();
			}


		}

	}


// serves load confirmations - turns off the motor - resets the status of the conveyor
	private class UnloadConfServer extends CyclicBehaviour {

		// The default constructor of this inner class
		private UnloadConfServer(Agent a){
			super(a);

		}

		//private AID nextn;
		//private String name;
		MessageTemplate template = MessageTemplate.and(MessageTemplate.MatchConversationId("load-query-reply"),
					MessageTemplate.MatchPerformative(ACLMessage.CONFIRM));


		private AID sender;
		private String prodID;

		public void action() {

			ACLMessage msg = myAgent.receive(template);
			if(msg != null) {
				if(Endpos){

					// sets status, turns motor off, resets current product

					Unloading = false;
					busy = false;
					motorValue = false;
					CurrentProdID="";

					// broadcast "I am free now!"

					ACLMessage broadcast = new ACLMessage(ACLMessage.INFORM);

					while(!senderids.isEmpty()) {

						sender = senderids.remove(0);
						prodID = senderprods.remove(sender);

						broadcast.addReceiver(sender);
						broadcast.setConversationId("ConveyorStatus");
 						broadcast.setPerformative(ACLMessage.INFORM);
 						broadcast.setContent("Free");
 						broadcast.setReplyWith(prodID);

                		myAgent.send(broadcast);

					}

				}
			}
			else{
				// TIP: Don't forget to block the behavior when is not in use!
				block();
			}


		}

	}



// sends a message to oea when product is completed
	private class ProdCompleted extends OneShotBehaviour {

		// The default constructor of this inner class
		private ProdCompleted(Agent a){
			super(a);

		}

		private AID oea;

		public void action() {

			removePallet = true;
			busy = false;

			oea = new AID("order_reg",AID.ISLOCALNAME);

			ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
			msg.addReceiver(oea);
			msg.setConversationId("product-completed");
		    msg.setContent(CurrentProdID);
			send(msg);

		}

	}




	/*
	 * These methods correspond to the interface ConvAgent and have to be implemented
	 * by this agent class
	 */

	/**
	 *@see ConvAgent
	 */
	public String getPath(){
		return path;
	}

	/**
	 *@see ConvAgent
	 */
	public void getPath(String from, String to){
		this.from = from;
		this.to = to;

		//Add a FindPath behavior asking agents to compute a path
		addBehaviour(new FindPath(this));
	}

	/**
	 *@see ConvAgent
	 */
	public void setPalletPosition(int pos){
		palletPosition = pos;

		if(palletPosition == 10){
			Loading = false;
			Loaded = false;
			Startpos = true;
			palletLoad = true;
			addBehaviour(new LoadConf(this));
		}
		else
			if(Unloading && palletPosition == 100){
				//addBehaviour(new UnloadConf(this));
				Endpos = true;
				Loaded = false;
			}
			else
		if(!Loaded && palletPosition == 70){
			motorValue = false;
			palletLoaded = true;
			Loaded = true;
			//Compute route control decision...
			addBehaviour(new RouteControlDecision(ConveyorAgent.this));
		}

	}

	/**
	 *@see ConvAgent
	 */
	public boolean getMotorValue(){
		return motorValue;
	}

	/*
	 *
	 * @see ConvAgent
	 */
	public boolean addNeigbor(String id){
		//AID agent = new AID(id, AID.ISLOCALNAME);
		//Find an agent by 'id'

		//If found
		System.out.println(neighbors);
		if(!neighbors.contains(id))
			neighbors.add(id);
		else
			return false;
		System.out.println(neighbors);
		return true;

	}


	public boolean addSender(AID id, String prodID){

		if(!senderids.contains(id)) {

			senderids.add(id);
			senderprods.put(id,prodID);

		}


		else
			return false;

		return true;

	}

//Find path algortihm functions
public String FindPathAlgorithm(String s){

       String TargetID=getTarget(s);
       String ActualPoint=getLocalName();
       String AlgorithmResult="NA";


       boolean inPathLogBool=IsInPathLog(s);

                       if (inPathLogBool){
                       //
                       AlgorithmResult="WrongPath-1";
                       }
                       else{
                       //Actual point not in pathLog

                               if (TargetID.equals(ActualPoint)){

                               //Append
                               AlgorithmResult="PathFound";
                               }
                               else{
                               //Actual point is not target

                                       if (HasNeighbors()){
                                       //Append
                                       AlgorithmResult="PropagateSearch";
                                       }
                                       else{

                                       //Append
                                       AlgorithmResult="WrongPath-2";
                                       }//Has neighbors

                               }       //TargetID==ActualPoint


                       }//inPathLog


       return AlgorithmResult;
}


public Vector getInitiator(String messStr){
         String [] temp1 = null;
     String [] temp2 = null;

     temp1 = messStr.split(":");
     temp2 = temp1[1].split(",");

     String [] temp3=new String[1];
         temp3[0]=temp2[0];

  Vector  v = new Vector(Arrays.asList(temp3));


     return v;
}






public void sendToAgents_Inform(Vector agentsList,String ConvId,String
messageContent){


       ACLMessage MsgToSend = new ACLMessage(ACLMessage.INFORM);




//Convert vector to string

String[] st =new String[agentsList.size()];
agentsList.toArray(st);

 for (int i = 0; i < st.length; ++i) {
               AID agent=new AID(st[i],AID.ISLOCALNAME);
       MsgToSend.addReceiver(agent);
     }

 MsgToSend.setContent(messageContent);
 MsgToSend.setConversationId(ConvId);

 send(MsgToSend);

}


public void sendToAgents_Request(Vector agentsList,String
ConvId,String messageContent){


       ACLMessage MsgToSend = new ACLMessage(ACLMessage.REQUEST);




//Convert vector to string

String[] st =new String[agentsList.size()];
agentsList.toArray(st);

 for (int i = 0; i < st.length; ++i) {
               AID agent=new AID(st[i],AID.ISLOCALNAME);
       MsgToSend.addReceiver(agent);
     }

 MsgToSend.setContent(messageContent);
 MsgToSend.setConversationId(ConvId);

 send(MsgToSend);

}



public String appendToLog(String m){
       return m+","+getLocalName();
}

public boolean HasNeighbors(){
       if (neighbors.size()>0)
               return true;
       else
               return false;

}

public String  getTarget(String messStr){
     String [] temp = null;
     temp = messStr.split(":");
return temp[0];
       }

public boolean IsInPathLog(String messStr){
     String [] temp1 = null;
     String [] temp2 = null;

     temp1 = messStr.split(":");
     temp2 = temp1[1].split(",");

   Vector  v = new Vector(Arrays.asList(temp2));


    // System.out.println(v);

     //If found
               if(v.contains(getLocalName()))
               return true;
               else
               return false;

       }


//Find Path algorithm search....




//Functions used in RoutingControl algorithm....
public void splitRecipeAndPath(String messStr){
  	  String [] temp1 = null;
      String [] RecArr = null;
      String [] PathArr = null;

      temp1 = messStr.split(":");
      RecArr = temp1[0].split(",");
      PathArr = temp1[1].split(",");


  RecipeV= new Vector(Arrays.asList(RecArr));
  PathV= new Vector(Arrays.asList(PathArr));


}

public String FormatRecipeAndPath()
{
String PathAndRecStr="";

//Convert vector to string
String[] st =new String[RecipeV.size()];
RecipeV.toArray(st);

PathAndRecStr=st[0];
 for (int i = 1; i < st.length; ++i) {
 	PathAndRecStr=PathAndRecStr+","+st[i];
      }
PathAndRecStr=PathAndRecStr+":";

 st =new String[PathV.size()];
PathV.toArray(st);

PathAndRecStr=PathAndRecStr+st[0];
 for (int i = 1; i < st.length; ++i) {
 	PathAndRecStr=PathAndRecStr+","+st[i];
      }

return PathAndRecStr;
}



public boolean IsVectorEmpty(Vector vect){

	if (vect.size()==0)
			return true;
		else
			return false;

}

public String NextPointInRecipe()
{

//Convert vector to string
String[] st =new String[RecipeV.size()];
RecipeV.toArray(st);

return st[0];

}

public String NextPointInPath()
{

//Convert vector to string
String[] st =new String[PathV.size()];
PathV.toArray(st);

return st[0];

}

public Vector NextPointInPathV()
{

//Convert vector to string
String[] st =new String[PathV.size()];
PathV.toArray(st);
 String [] temp3=new String[1];

 temp3[0]=st[0];


   Vector  v = new Vector(Arrays.asList(temp3));


      return v;

}

public void refillPathV(String messStr)
{
String [] temp1 = null;
      String [] PathArr = null;

      temp1 = messStr.split(":");
      PathArr = temp1[1].split(",");

      PathV= new Vector(Arrays.asList(PathArr));
      PathV.remove(0);

}



//Functions used in RoutingControl algorithm....






public void Say (String s){

   System.out.println(getLocalName()+ " says:");
       System.out.println(s);
}






}