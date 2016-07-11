package fb.rt;


import fb.datatype.*;
import fb.rt.*;
import fb.rt.events.*;

/**
 *An agent interface function block
 *
 *@version 20100420
 *@author Andrei Lobov
 */
public interface ConvAgent {
	
		
	/**
	 * Initializes the agent.
	 * @param id - identification of the agent
	 * @return true - if the initialization is successful
	 */
//	 public ConvAgent init(String id);
	 
	 /**
	  * Adds the neigbor to the given agent
	  * @param id - identificator of the neigbor agent
	  * @return true if the addition was successful
	  */
	 public boolean addNeigbor(String id);
	 
	 /**
	  * Initiates a request for finding a path between "two agents" 
	  * based on neigborhood relation.
	  * @param fromID - start agent ID for the search path 
	  * @param toID - end agent ID for the search path
	  */
	 public void getPath(String fromID, String toID);
	 
	 /**
	  * Retrieves a path between "two agents" based on neigborhood relation
	  * once it has been computed (pathFound is true).
	  * @return a comma separated path between the agents
	  */
	 public String getPath();
	 
	 /**
	  * Sets a position of the pallet.
	  * If the agent is loading athepallet, it should load the pallet till position is 70 and then stop the motor.
	  * If the agent is unloading the pallet, it should run the motor until the position is less than 110.
	  *
	  * @param position - position of the pallet.
	  */
	 public void setPalletPosition(int position);
	 
	 /**
	  * Returns the current status of the motor.
	  * @return true - if motor is running, false - otherwise.
	  */
	 public boolean getMotorValue();
	 
	 
	 
	
}
