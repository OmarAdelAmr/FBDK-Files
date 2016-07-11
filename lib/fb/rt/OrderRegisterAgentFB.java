/* Copyright (c)2010 TUT. All rights reserved. */
package fb.rt;
import fb.datatype.*;
import fb.rt.*;
import fb.rt.events.*;

import fi.tut.pe.acra.mas.OrderEntryAgent;

/** FUNCTION_BLOCK OrderRegisterAgent
  * @author AnLo
  * @version 20100420/AnLo
  */
public class OrderRegisterAgentFB extends FBInstance
{
/** Input event qualifier */
  public WSTRING NAME = new WSTRING();
/** VAR PRODUCT */
  public WSTRING PRODUCT = new WSTRING();
/** VAR RECIPE */
  public WSTRING RECIPE = new WSTRING();
/** VAR STATUS */
  public WSTRING STATUS = new WSTRING();
/** VAR KNOWN_PRODUCTS */
  public WSTRING KNOWN_PRODUCTS = new WSTRING();
/** Lists # of orders: "{(A, 1), (B,2)}; {(A, 6), (B,7)}" = "Current Orders; Finalized Orders" */
  public WSTRING ORDERS = new WSTRING();
/** Initialization */
 public EventServer INIT = new EventInput(this);
/** Define product */
 public EventServer DEF_PRODUCT = new EventInput(this);
/** EVENT ORDER */
 public EventServer ORDER = new EventInput(this);
/** EVENT LIST_PRODUCTS */
 public EventServer LIST_PRODUCTS = new EventInput(this);
/** List current orders */
 public EventServer LIST_ORDERS = new EventInput(this);
/** Block initialized */
 public EventOutput INITO = new EventOutput();
/** Confirmation event */
 public EventOutput CNF = new EventOutput();
/** EVENT ORDER_STARTED */
 public EventOutput ORDER_STARTED = new EventOutput();
/** Order cannot be intput now */
 public EventOutput WAIT = new EventOutput();
/** Error while executing the request (E.g. unknown Product ordered: "PRODUCT_UNKNOWN. */
 public EventOutput ERR = new EventOutput();
/** {@inheritDoc}
* @param s {@inheritDoc}
* @return {@inheritDoc}
*/
  public EventServer eiNamed(String s){
    if("INIT".equals(s)) return INIT;
    if("DEF_PRODUCT".equals(s)) return DEF_PRODUCT;
    if("ORDER".equals(s)) return ORDER;
    if("LIST_PRODUCTS".equals(s)) return LIST_PRODUCTS;
    if("LIST_ORDERS".equals(s)) return LIST_ORDERS;
    return super.eiNamed(s);}
/** {@inheritDoc}
* @param s {@inheritDoc}
* @return {@inheritDoc}
*/
  public EventOutput eoNamed(String s){
    if("INITO".equals(s)) return INITO;
    if("CNF".equals(s)) return CNF;
    if("ORDER_STARTED".equals(s)) return ORDER_STARTED;
    if("WAIT".equals(s)) return WAIT;
    if("ERR".equals(s)) return ERR;
    return super.eoNamed(s);}
/** {@inheritDoc}
* @param s {@inheritDoc}
* @return {@inheritDoc}
* @throws FBRManagementException {@inheritDoc}
*/
  public ANY ivNamed(String s) throws FBRManagementException{
    if("NAME".equals(s)) return NAME;
    if("PRODUCT".equals(s)) return PRODUCT;
    if("RECIPE".equals(s)) return RECIPE;
    return super.ivNamed(s);}
/** {@inheritDoc}
* @param s {@inheritDoc}
* @return {@inheritDoc}
* @throws FBRManagementException {@inheritDoc}
*/
  public ANY ovNamed(String s) throws FBRManagementException{
    if("STATUS".equals(s)) return STATUS;
    if("KNOWN_PRODUCTS".equals(s)) return KNOWN_PRODUCTS;
    if("ORDERS".equals(s)) return ORDERS;
    return super.ovNamed(s);}
/** {@inheritDoc}
* @param ivName {@inheritDoc}
* @param newIV {@inheritDoc}
* @throws FBRManagementException {@inheritDoc} */
  public void connectIV(String ivName, ANY newIV)
    throws FBRManagementException{
    if("NAME".equals(ivName)){connect_NAME((WSTRING)newIV); return;}
    if("PRODUCT".equals(ivName)){connect_PRODUCT((WSTRING)newIV); return;}
    if("RECIPE".equals(ivName)){connect_RECIPE((WSTRING)newIV); return;}
    super.connectIV(ivName, newIV);
    }
/** Connect the given variable variable to the input variable named NAME
  * @param newIV The variable to connect
  * @throws FBRManagementException An internal connection failed. */
  public void connect_NAME(WSTRING newIV) throws FBRManagementException{
    NAME = newIV;
    }
/** Connect the given variable variable to the input variable named PRODUCT
  * @param newIV The variable to connect
  * @throws FBRManagementException An internal connection failed. */
  public void connect_PRODUCT(WSTRING newIV) throws FBRManagementException{
    PRODUCT = newIV;
    }
/** Connect the given variable variable to the input variable named RECIPE
  * @param newIV The variable to connect
  * @throws FBRManagementException An internal connection failed. */
  public void connect_RECIPE(WSTRING newIV) throws FBRManagementException{
    RECIPE = newIV;
    }
/** The default constructor. */
public OrderRegisterAgentFB(){
    super();
  }
/** {@inheritDoc}
* @param e {@inheritDoc} */
  public void serviceEvent(EventServer e){
    if (e == INIT) service_INIT();
    else if (e == DEF_PRODUCT) service_DEF_PRODUCT();
    else if (e == ORDER) service_ORDER();
    else if (e == LIST_PRODUCTS) service_LIST_PRODUCTS();
    else if (e == LIST_ORDERS) service_LIST_ORDERS();
  }
  
//------------------ FB METHODS TO BE UPDATED BELOW ------------------

	private OdRAgent oa;
	
	
  /**
   * Initialize the agent
   */
  public void service_INIT(){
  	System.out.println("Initiailizing the agent: " + NAME);	
    	
    if(oa == null){
    	oa = OrderEntryAgent.init(NAME.value);
    }
      	
  	INITO.serviceEvent(this); //Send INTO event
  }
  
  /**
   * Define the product depending on PRODUCT and Recipe inputs
   */
  public void service_DEF_PRODUCT(){
  	oa.defineProduct(PRODUCT.value, RECIPE.value);
  	CNF.serviceEvent(this);
  }
  
  /**
   * Make an order (Check for PRODUCT parameter value)
   */
  public void service_ORDER(){
  	oa.addOrder(PRODUCT.value);
  	if(oa.orderAdded())
  		ORDER_STARTED.serviceEvent(this);
  	else
  		WAIT.serviceEvent(this);
  }
  
  /**
   * Lists the known products for which the agent has recipes
   */
  public void service_LIST_PRODUCTS(){
  	KNOWN_PRODUCTS.value = oa.listProducts();
  	CNF.serviceEvent(this);
  }
  
  /**
   * Lists orders
   * Format: "{(A, 1), (B,2)}; {(A, 6), (B,7)}" = "{Current Orders}; {Finalized Orders}"
   */
  public void service_LIST_ORDERS(){
  	ORDERS.value = oa.listOrders();
  	CNF.serviceEvent(this);
  }
}
