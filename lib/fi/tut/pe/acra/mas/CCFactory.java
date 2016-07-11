package fi.tut.pe.acra.mas;

import jade.core.Runtime;
import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.wrapper.*;
import jade.domain.*;


import fb.rt.*;

/**
 *
 * @author Andrei Lobov
 * @version 20100422
 */
public class CCFactory {
	
	/**
	 * A container instance for the 
	 */
	private static ContainerController cc = null;
	
	public static ContainerController getCCInstance(){
		if(cc == null){
			
			//Start the platform.
			String ar[] = new String[1]; 
			ar[0] = "-gui";
							
			jade.Boot.main(ar);

			//***** JADE programmers guide p. 52. *****
			// Get a hold on JADE runtime
			Runtime rt = Runtime.instance();
			// Create a default profile
			Profile p = new ProfileImpl();
			// Create a new non-main container, connecting to the default
			// main container (i.e. on this host, port 1099)
			cc = rt.createAgentContainer(p);
		}
		
		return cc;
	}
	
	/**
	 *Destroy cc
	 */
	public static void destroyJADE(){
		try{
			cc.kill();
		} catch(Exception ex){
		 	System.out.println("Exception destroying CC: " + ex.getMessage());
		}
		cc = null;
	}
	
	/**
	 * For testing
	 */
	public static void main(String[] args){
		
		ConvAgent ca = ConveyorAgent.init("DemoAgent");
		
		if(true)
			return;
		
		ContainerController CC = CCFactory.getCCInstance();
		

		// Create a new agent, a DummyAgent
		// and pass it a reference to an Object
		Object reference = new Object();
		Object argz[] = new Object[1];
		argz[0]=reference;
		try{
			AgentController dummy = CC.createNewAgent("inProcess",
			"jade.tools.DummyAgent.DummyAgent", argz);
			// Fire up the agent
			dummy.start();		
		} catch(Exception e){
			System.out.println("Exception while trying to create an agent: ");
			e.printStackTrace();
		}

		try{
			System.out.println("Container created:" + CC.getContainerName());
		} catch(Exception ex){
			System.out.println("Exception in CC: ");
			ex.printStackTrace();
		}
	}
	
}
