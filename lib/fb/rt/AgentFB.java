/* Copyright (c)2010 Tampere University of Technology. All rights reserved. */
package fb.rt;
import fb.datatype.*;
import fb.rt.*;
import fb.rt.events.*;

import fi.tut.pe.acra.mas.*;

/** FUNCTION_BLOCK Agent
 * A gateway to JADE platform
  * @author JHC, AnLo
  * @version 20100420
  */
public class AgentFB extends FBInstance
{
/** VAR NAME */
  public WSTRING NAME = new WSTRING();
/** VAR NEIGHBOR */
  public WSTRING NEIGHBOR = new WSTRING();
/** VAR PALLET_POS */
  public INT PALLET_POS = new INT();
/** VAR FROM */
  public WSTRING FROM = new WSTRING();
/** VAR TO */
  public WSTRING TO = new WSTRING();
/** VAR PATH */
  public WSTRING PATH = new WSTRING();
/** VAR MOTOR */
  public BOOL MOTOR = new BOOL();
/** Initialization Request */
 public EventServer INIT = new EventInput(this);
/** EVENT AddNeighbor */
 public EventServer AddNeighbor = new EventInput(this);
/** EVENT GetPath */
 public EventServer GetPath = new EventInput(this);
/** EVENT POS_UPD */
 public EventServer POS_UPD = new EventInput(this);
/** Initialization Confirm */
 public EventOutput INITO = new EventOutput();
/** EVENT LOAD_STARTED */
 public EventOutput LOAD_STARTED = new EventOutput();
/** EVENT LOADED */
 public EventOutput LOADED = new EventOutput();
/** EVENT FREE */
 public EventOutput FREE = new EventOutput();
/** EVENT CNF */
 public EventOutput CNF = new EventOutput();
/** EVENT Path */
 public EventOutput Path = new EventOutput();
/** EVENT RemovePallet */
 public EventOutput RemovePallet = new EventOutput();
/** {@inheritDoc}
* @param s {@inheritDoc}
* @return {@inheritDoc}
*/
  public EventServer eiNamed(String s){
    if("INIT".equals(s)) return INIT;
    if("AddNeighbor".equals(s)) return AddNeighbor;
    if("GetPath".equals(s)) return GetPath;
    if("POS_UPD".equals(s)) return POS_UPD;
    return super.eiNamed(s);}
/** {@inheritDoc}
* @param s {@inheritDoc}
* @return {@inheritDoc}
*/
  public EventOutput eoNamed(String s){
    if("INITO".equals(s)) return INITO;
    if("LOAD_STARTED".equals(s)) return LOAD_STARTED;
    if("LOADED".equals(s)) return LOADED;
    if("FREE".equals(s)) return FREE;
    if("CNF".equals(s)) return CNF;
    if("Path".equals(s)) return Path;
    if("RemovePallet".equals(s)) return RemovePallet;
    return super.eoNamed(s);}
/** {@inheritDoc}
* @param s {@inheritDoc}
* @return {@inheritDoc}
* @throws FBRManagementException {@inheritDoc}
*/
  public ANY ivNamed(String s) throws FBRManagementException{
    if("NAME".equals(s)) return NAME;
    if("NEIGHBOR".equals(s)) return NEIGHBOR;
    if("PALLET_POS".equals(s)) return PALLET_POS;
    if("FROM".equals(s)) return FROM;
    if("TO".equals(s)) return TO;
    return super.ivNamed(s);}
/** {@inheritDoc}
* @param s {@inheritDoc}
* @return {@inheritDoc}
* @throws FBRManagementException {@inheritDoc}
*/
  public ANY ovNamed(String s) throws FBRManagementException{
    if("PATH".equals(s)) return PATH;
    if("MOTOR".equals(s)) return MOTOR;
    return super.ovNamed(s);}
/** {@inheritDoc}
* @param ivName {@inheritDoc}
* @param newIV {@inheritDoc}
* @throws FBRManagementException {@inheritDoc} */
  public void connectIV(String ivName, ANY newIV)
    throws FBRManagementException{
    if("NAME".equals(ivName)){connect_NAME((WSTRING)newIV); return;}
    if("NEIGHBOR".equals(ivName)){connect_NEIGHBOR((WSTRING)newIV); return;}
    if("PALLET_POS".equals(ivName)){connect_PALLET_POS((INT)newIV); return;}
    if("FROM".equals(ivName)){connect_FROM((WSTRING)newIV); return;}
    if("TO".equals(ivName)){connect_TO((WSTRING)newIV); return;}
    super.connectIV(ivName, newIV);
    }
/** Connect the given variable variable to the input variable named NAME
  * @param newIV The variable to connect
  * @throws FBRManagementException An internal connection failed. */
  public void connect_NAME(WSTRING newIV) throws FBRManagementException{
    NAME = newIV;
    }
/** Connect the given variable variable to the input variable named NEIGHBOR
  * @param newIV The variable to connect
  * @throws FBRManagementException An internal connection failed. */
  public void connect_NEIGHBOR(WSTRING newIV) throws FBRManagementException{
    NEIGHBOR = newIV;
    }
/** Connect the given variable variable to the input variable named PALLET_POS
  * @param newIV The variable to connect
  * @throws FBRManagementException An internal connection failed. */
  public void connect_PALLET_POS(INT newIV) throws FBRManagementException{
    PALLET_POS = newIV;
    }
/** Connect the given variable variable to the input variable named FROM
  * @param newIV The variable to connect
  * @throws FBRManagementException An internal connection failed. */
  public void connect_FROM(WSTRING newIV) throws FBRManagementException{
    FROM = newIV;
    }
/** Connect the given variable variable to the input variable named TO
  * @param newIV The variable to connect
  * @throws FBRManagementException An internal connection failed. */
  public void connect_TO(WSTRING newIV) throws FBRManagementException{
    TO = newIV;
    }
/** The default constructor. */
public AgentFB(){
    super();
  }
/** {@inheritDoc}
* @param e {@inheritDoc} */
  public void serviceEvent(EventServer e){
    if (e == INIT) service_INIT();
    else if (e == AddNeighbor) service_AddNeighbor();
    else if (e == GetPath) service_GetPath();
    else if (e == POS_UPD) service_POS_UPD();
  }

//------------------ FB METHODS AND AGENT RELATED VARS TO BE UPDATED BELOW ------------------

  private ConveyorAgent ca;


  /**
   * Initializing the agent
   */
  public void service_INIT(){
    System.out.println("Initiailizing the agent: " + NAME);

    if(ca == null){
    	ca = ConveyorAgent.init(NAME.value);
    }

  	INITO.serviceEvent(this); //Send INTO event
  }

  /**
   * Adds the neigbour to the list of agent's neigbours
   */
  public void service_AddNeighbor(){
  	System.out.println("Add neighbor: " + NEIGHBOR);

  	ca.addNeigbor(NEIGHBOR.value);
  	CNF.serviceEvent(this); //Should be generated ONLY if add neighbor request is accepted.
  }


  /**
   * Invokes service for path calculation
   *@return - A comma separated list of nodes in the path or "NOT_FOUND" string.
   */
  public void service_GetPath(){
  	System.out.println("Get path from " + FROM + " to " + TO + ".");

  	//update "Path" variable
  	ca.getPath(FROM.value, TO.value);

  	//Generate associated events.
  	CNF.serviceEvent(this);

  	if(ca.pathFound){
  		PATH.value = ca.getPath();
  		ca.pathFound = false;
  		Path.serviceEvent(this);
  	}
  }


  /**
   * Updates the position of the conveyor. Based on this value, the agent should decide when
   * to stop the loading operation or inform that the unloading is complete.
   */
  public void service_POS_UPD(){
  	//System.out.println(NAME + "Current position: " + PALLET_POS);

  	//Sets the position of the conveyor segment
  	ca.setPalletPosition((int)PALLET_POS.value);

  	//Check motor value
  	MOTOR.value = ca.getMotorValue();
  	//System.out.println("Motor Value: " + MOTOR); // aggiunta

  	CNF.serviceEvent(this);
  	//Check if loaded
  	//if(PALLET_POS.value == 70)
  	if(ca.palletLoaded){
  		LOADED.serviceEvent(this);
  		ca.palletLoaded = false;
  	}
  	//Check if started to load
  	//if(PALLET_POS.value == 10)
  	if(ca.palletLoad){
  		LOAD_STARTED.serviceEvent(this);
  		ca.palletLoad = false;
  	}
  	//Check if free
  	if(PALLET_POS.value == 100)
  		FREE.serviceEvent(this);

  	//Check if pallet must be removed
  	if(ca.removePallet){
  		RemovePallet.serviceEvent(this);
  		ca.removePallet = false;
  	}

  }
}
