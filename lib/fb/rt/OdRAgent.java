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
public interface OdRAgent {

	/**
	 * Initializes the agent.
	 * @param id - identification of the agent
	 * @return true - if the initialization is successful
	 */
//	 public boolean init(String id);

	 /**
	  * Defines a product
	  *
	  * @param prodID - identificator for the product
	  * @param recipe - a comma separated recipe for product manufacturing containing
	  * an agents' order that should be followed by the product.
	  *
	  * If the new definition of the already existent product arrives, the old definition
	  * is overwriten with the new recipe.
	  *
	  * @return true - if the definition was successful
	  */
	 public boolean defineProduct(String prodID, String recipe);

	 /**
	  * Lists the products, which system knows how to build.
	  *
	  * @return a comma separated list of known products the system can manufacture
	  */
	 public String listProducts();


	 /**
	  * Returns a list of the current and past orders.
	  *
	  * Example:
	  * "{(A,1), (B,2)}; {(A,6), (B,7)}"
	  *
	  * Interpretation: Currently system handles 1 order for product A and 2 orders
	  * for product B. In the past, the system accomplished 6 orders for product A
	  * and 7 orders for product B.
	  *
	  */
	 public String listOrders();

	 /**
	  * Introduces an order to the system.
	  *
	  * @param prodID - ID of the product to be manufactured.
	  *
	  */
	 public void addOrder(String prodID);

	 /**
	  * Informs when an order has been added to the system.
	  *
	  * @return true - if the order manufacturing has started, false - otherwise. NB! If the order
	  * manufacturing was not started it is required to repeat the order entry procedure again.
	  */
	 public boolean orderAdded();

	 /**
	  * Informs when a product has been completed.
	  *
	  * @return  prodID - ID of the product that has been manufactured.
	  */
	 public String productFinished();


}