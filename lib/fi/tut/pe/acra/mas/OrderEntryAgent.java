package fi.tut.pe.acra.mas;

/*
 * @author VlVi / AnLo
 * @version 21042010
 */

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import fb.rt.OdRAgent;
import jade.content.ContentManager;
import jade.content.lang.sl.SLCodec;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.FIPAManagementOntology;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.JADEAgentManagement.JADEManagementOntology;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.wrapper.ContainerController;

@SuppressWarnings("serial")
public class OrderEntryAgent extends Agent implements OdRAgent {

	// Let us use loggers instead of System.out.println
	// Define a logger for this class and setup your own logger levels
	private static Logger logger = Logger.getLogger(OrderEntryAgent.class.getName());

	// Static method for creating automatically instances of this agent
	// DO NOT MODIFY!
	public static OdRAgent init(String nickname) {
		ContainerController  cc = CCFactory.getCCInstance();
		OrderEntryAgent oea = new OrderEntryAgent();
		try{
			logger.info("CC " + cc);
			cc.acceptNewAgent(nickname, oea).start();
		} catch(Exception ex){
			logger.log(Level.WARNING, "Exception creating Order Entry Agent: " + nickname);
			ex.printStackTrace();
		}
		return oea;
	}

	private SLCodec codec = new SLCodec();
	// OPTIONAL: Define the instance of your own communication ontologies here
	private ContentManager cm = (ContentManager)getContentManager();
	private AID orderEntryAgent;


	// This HashMap is meant to hold a list of products and their recipes.
	// It uses the prodID string as a unique key and the recipe string as the value
	// of the map.
	private Map<String, String> products = new HashMap<String, String>();

	// This HashMap is meant to hold a list of orders that were produced.
	// It uses the productID string as a unique key and the quantity string as the value.
	private Map<String, String> previousOrders = new HashMap<String, String>();

	// This HashMap is meant to hold a list orders that are currently being produced.
	// It uses the productID string as a unique key and the quantity string as the value.
	private Map<String, String> currentOrder = new HashMap<String, String>();

	// Flag used to indicate when an order has been added and it has started production
	private Boolean orderAdded = false;

	// Used to inform the system what product has been finished
	private String productCompleted;

	//
	private Map<String, Integer> Waiting4Status = new HashMap<String, Integer>();

	private Map<String, Integer> Waiting4Start = new HashMap<String, Integer>();


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
        String serviceName = "JADEOrderEntryService";
        String typeName = "HMI";
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
		logger.info(String.format("Hello! OrderEntry-Agent %s is ready.", getAID().getName()));

		// TODO: Add other behaviors here

		addBehaviour(new ReadinessReplyServer(this));
		addBehaviour(new ConfirmStartServer(this));
		addBehaviour(new ProdCompletedServer(this));


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
		logger.info(String.format("OrderEntry-Agent %s terminating.", getAID().getName()));
	}


	/*
	 * This inner class implements the behavior that will ask to the Conveyor
	 * Agent (CA) which represents the entry point of the system, if it is possible
	 * to start producing. CA agent should reply if its free or busy with a
	 * pallet. A "free" response from the CA should trigger the start of the
	 * production.
	 * This behavior is complementary to ReadinessReplyServer behavior
	 */
	private class OrderEntryFBRequests extends OneShotBehaviour {
		private String prodID;

		// The default constructor of this inner class
		private OrderEntryFBRequests(Agent a, String prodID){
			super(a);
			this.prodID = prodID;

		}

		private AID entrypoint;
		private String[] recipe;
		private int quantity;

		public void action() {

			System.out.println("Order!");
			if(products.containsKey(this.prodID)) {

				System.out.println("Recipe in list!");
				System.out.println(products.get(this.prodID));

				// string with recipe information to be sent//
				recipe = products.get(this.prodID).split(",");
				// to the entrypoint //
				entrypoint = new AID(recipe[0], AID.ISLOCALNAME);
				System.out.println(entrypoint);

				// send a ConveyorStatus query//
				ACLMessage msg = new ACLMessage(ACLMessage.QUERY_IF);
				msg.addReceiver(entrypoint);
				msg.setConversationId("ConveyorStatus");
				msg.setReplyWith(prodID);
				send(msg);

				// set the product waiting for the status of the conveyor//
				addWaiting4Status(this.prodID);


			}


			else
				System.out.println("Error! Product not listed!");


		}

	}



	/*
	 * This inner class implements the behavior that will receive answers from
	 * the Conveyor Agent (CA) which represents the entry point of the system.
	 * If CA is free, this behavior should trigger the start of the production.
	 * This behavior is complementary to OrderEntryFBRequests behavior
	 */
	private class ReadinessReplyServer extends CyclicBehaviour {
		private MessageTemplate template;

		// The default constructor of this inner class
		private ReadinessReplyServer(Agent a){
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
					MessageTemplate.MatchPerformative(ACLMessage.INFORM)); //This is just an example!
		}

		private AID entrypoint;
		private String prodID;
		private String[] path;
		private String RecipeAndPath;
		private String recipe;
		private int quantity;
		private String convstatus;

		public void action() {
			// Receive the messages sent to this agent
			ACLMessage msg = myAgent.receive(template);
			if(msg != null) {
				// TIP: Make sure to catch all the thrown exceptions!
				// TODO: Parse the message. You can also use the ContentManager and your own ontology (i.e., convert to Java objects)
				// TODO: If the Conveyor Agent (CA) says it's free, trigger the RequestStart behavior


				convstatus = msg.getContent();

				System.out.println("OEA was answered:" + convstatus);

				// if it's free...
				if(convstatus.equals("Free")) {

					// check if the product is still waiting for conveyor status
					prodID = msg.getReplyWith();
					quantity = Waiting4Status.get(prodID);

					System.out.println(quantity);

					// if it's waiting...
					if(quantity > 0) {

						// build path and recipe information
						recipe = products.get(prodID);
						path = recipe.split(",");
						RecipeAndPath = recipe + ":" + path[0];

						// send a start-request
						ACLMessage reply = msg.createReply();
						reply.setConversationId("start-request");
 						reply.setPerformative(ACLMessage.REQUEST);
 						reply.setReplyWith(prodID);
 						reply.setContent(RecipeAndPath);

                		myAgent.send(reply);

						// set the product waiting for start
						delWaiting4Status(prodID);
						addWaiting4Start(prodID);

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
	 * This inner class implements a behavior that will trigger the start of production.
	 * The behavior waits for a confirmation from the Conveyor Agent informing that
	 * a certain product has been finished. The confirmation must be forwaded to the
	 * underlying FB system.
	 */



// serves start-request-replies
	private class ConfirmStartServer extends CyclicBehaviour {
		private MessageTemplate template;
		private String prodID;

		// The default constructor of this inner class
		private ConfirmStartServer(Agent a){
			super(a);
			//this.prodID = prodID;
			/* Prepare the template to get the messages from other agents
			 * An example of template is provided below, but you can (and most
			 * likely should!) modify it according the communication needs.
			 * Consult the FIPA specifications, the programming guides, and the
			 * JADE API for more details.
			 */
			// TODO: Modify message template
			template = MessageTemplate.MatchConversationId("start-request-reply"); //This is just an example!
		}
		private int quantity;
		private String convstatus;

		public void action() {

			// Receive the messages sent to this agent
			ACLMessage msg = myAgent.receive(template);
			if(msg != null) {

				convstatus = msg.getContent();

			    // if agree...
				if(convstatus.equals("Loading")){

					prodID = msg.getReplyWith();
					quantity = Waiting4Start.get(prodID);

					// ...and if the product is waiting for start...
					if(quantity > 0){

						// add the order to the orders being processed
						addCurrentOrder(prodID);
						delWaiting4Start(prodID);
					}

				}
				// if refuse, add a new OrderEntryFBRequests behaviour
				else {
					delWaiting4Start(prodID);
					addBehaviour(new OrderEntryFBRequests(OrderEntryAgent.this, prodID));
				}


			}
			else {
				// TIP: Don't forget to block the behavior when is not in use!
				block();
			}

		}
	}


// serves product-completed messages
	private class ProdCompletedServer extends CyclicBehaviour {
		private MessageTemplate template;
		private String prodID;

		// The default constructor of this inner class
		private ProdCompletedServer(Agent a){
			super(a);

			template = MessageTemplate.and(MessageTemplate.MatchConversationId("product-completed"),
					MessageTemplate.MatchPerformative(ACLMessage.INFORM)); //This is just an example!
		}

		public void action() {
			// Receive the messages sent to this agent
			ACLMessage msg = myAgent.receive(template);
			if(msg != null) {


				prodID = msg.getContent();

				System.out.println("Product " + prodID + " completed!");

				// update production figures
				delCurrentOrder(prodID);
				addPreviousOrder(prodID);

			}
			else{
				// TIP: Don't forget to block the behavior when is not in use!
				block();
			}

		}
	}




	/*
	 * These methods correspond to the interface OdRAgent and have to be implemented
	 * by this agent class
	 */

	/**
	 *@see OdRAgent
	 */
	public boolean defineProduct(String prodID, String recipe){
		try{
			products.put(prodID, recipe);
			return true;
		}
		catch (Exception ex){
			logger.log(Level.WARNING, "Exception definining the product: " + prodID);
			ex.printStackTrace();
			return false;
		}
	}



	/**
	 *@see OdRAgent
	 */
	public String listProducts() {
		String listOfProducts = "";
		if (products.isEmpty()) {
			listOfProducts = "_NO_PRODUCTS_AVAILABLE";
		}
		else {
			for (Map.Entry<String, String> e : products.entrySet()) {
				listOfProducts = listOfProducts + "," + e.getKey();
			}
		}
		return listOfProducts.substring(1);
	}

	/**
	 *@see OdRAgent
	 */
	public String listOrders() {
		String listOfOrders = "";
		// Both HashMaps are empty... no orders in the system!
		if (currentOrder.isEmpty() && previousOrders.isEmpty()) {
			listOfOrders = "{};{}";
		}
		// The HashMap of currentOrder is NOT empty, but the HashMap of previousOrders is empty
		else if (!currentOrder.isEmpty() && previousOrders.isEmpty()) {
			listOfOrders = "{";
			for (Map.Entry<String, String> e : currentOrder.entrySet()) {
				listOfOrders = listOfOrders + "(" + e.getKey() + "," + e.getValue() + "),";
			}
			listOfOrders = listOfOrders + "};{}";
		}
		// The HashMap of currentOrder is empty, but the HashMap of previousOrdes is NOT empty
		else if (!previousOrders.isEmpty() && currentOrder.isEmpty()){
			listOfOrders = "{};{";
			for (Map.Entry<String, String> e : previousOrders.entrySet()) {
				listOfOrders = listOfOrders + "(" + e.getKey() + "," + e.getValue() + "),";
			}
			listOfOrders = listOfOrders + "}";
		}
		// Both HashMaps contain something
		else if (!currentOrder.isEmpty() && !previousOrders.isEmpty()) {
			listOfOrders = "{";
			for (Map.Entry<String, String> e : currentOrder.entrySet()) {
				listOfOrders = listOfOrders + "(" + e.getKey() + "," + e.getValue() + "),";
			}
			listOfOrders = listOfOrders + "};{";
			for (Map.Entry<String, String> e : previousOrders.entrySet()) {
				listOfOrders = listOfOrders + "(" + e.getKey() + "," + e.getValue() + "),";
			}
			listOfOrders = listOfOrders + "}";
		}
		return listOfOrders;
	}

	/**
	 *@see OdRAgent
	 */
	public void addOrder(String prodID) {
		addBehaviour(new OrderEntryFBRequests(this, prodID));
	}

	/**
	 *@see OdRAgent
	 */
	public boolean orderAdded() {
		return orderAdded;
	}

	/**
	 *@see OdRAgent
	 */
	public String productFinished() {
		return productCompleted;
	}

	private boolean addCurrentOrder(String prodID){
		int quantity;

		try{
			System.out.println("adding order...");
			if(currentOrder.containsKey(prodID)) {
				quantity = Integer.parseInt(currentOrder.get(prodID));
				currentOrder.put(prodID, Integer.toString(++quantity));
				}
			else {
				currentOrder.put(prodID, "1");
				}
			// orderAdded = true;
			return true;
		}
		catch (Exception ex){
			logger.log(Level.WARNING, "Exception adding the order of: " + prodID);
			ex.printStackTrace();
			return false;
		}
	}

	private boolean delCurrentOrder(String prodID){
		int quantity;

		try{
			System.out.println("deleting order...");
			if(currentOrder.containsKey(prodID)) {
				quantity = Integer.parseInt(currentOrder.get(prodID));
				currentOrder.put(prodID, Integer.toString(--quantity));
				}
			else {
				currentOrder.put(prodID, "0");
				}

			return true;
		}
		catch (Exception ex){
			logger.log(Level.WARNING, "Exception deleting the order of: " + prodID);
			ex.printStackTrace();
			return false;
		}
	}

	private boolean addPreviousOrder(String prodID){
		int quantity;

		try{
			System.out.println("recording order...");
			if(previousOrders.containsKey(prodID)) {
				quantity = Integer.parseInt(previousOrders.get(prodID));
				previousOrders.put(prodID, Integer.toString(++quantity));
				}
			else {
				previousOrders.put(prodID, "1");
				}

			return true;
		}
		catch (Exception ex){
			logger.log(Level.WARNING, "Exception recording the order of: " + prodID);
			ex.printStackTrace();
			return false;
		}
	}


private boolean addWaiting4Status(String prodID) {
	int quantity;


	if(!Waiting4Status.containsKey(prodID)) {
		Waiting4Status.put(prodID, 1);
	}
	else {
		quantity = Waiting4Status.get(prodID);
		Waiting4Status.put(prodID, ++quantity);

	}
	return true;
}

private boolean delWaiting4Status(String prodID) {
	int quantity;


	if(!Waiting4Status.containsKey(prodID)) {
		Waiting4Status.put(prodID, 0);
	}
	else {
		quantity = Waiting4Status.get(prodID);
		Waiting4Status.put(prodID, --quantity);

	}
	return true;
}

private boolean addWaiting4Start(String prodID) {
	int quantity;


	if(!Waiting4Start.containsKey(prodID)) {
		Waiting4Start.put(prodID, 1);
	}
	else {
		quantity = Waiting4Start.get(prodID);
		Waiting4Start.put(prodID, ++quantity);

	}
	return true;
}

private boolean delWaiting4Start(String prodID) {
	int quantity;


	if(!Waiting4Start.containsKey(prodID)) {
		Waiting4Start.put(prodID, 0);
	}
	else {
		quantity = Waiting4Start.get(prodID);
		Waiting4Start.put(prodID, --quantity);

	}
	return true;
}



}

