/* Copyright (c)2015 Fraunhofer IGD. All rights reserved. */
package fb.rt;
import fb.datatype.*;
import fb.rt.*;
import fb.rt.events.*;
/** FUNCTION_BLOCK FirstAgentFB
  * @author Ahmed
  * @version 20150416/Ahmed
  */
public class FirstAgentFB extends FBInstance
{
/** VAR NAME */
  public WSTRING NAME = new WSTRING();
/** Normal Execution Request */
 public EventServer INIT = new EventInput(this);
/** Execution Confirmation */
 public EventOutput INITO = new EventOutput();
/** {@inheritDoc}
* @param s {@inheritDoc}
* @return {@inheritDoc}
*/
  public EventServer eiNamed(String s){
    if("INIT".equals(s)) return INIT;
    return super.eiNamed(s);}
/** {@inheritDoc}
* @param s {@inheritDoc}
* @return {@inheritDoc}
*/
  public EventOutput eoNamed(String s){
    if("INITO".equals(s)) return INITO;
    return super.eoNamed(s);}
/** {@inheritDoc}
* @param s {@inheritDoc}
* @return {@inheritDoc}
* @throws FBRManagementException {@inheritDoc}
*/
  public ANY ivNamed(String s) throws FBRManagementException{
    if("NAME".equals(s)) return NAME;
    return super.ivNamed(s);}
/** {@inheritDoc}
* @param ivName {@inheritDoc}
* @param newIV {@inheritDoc}
* @throws FBRManagementException {@inheritDoc} */
  public void connectIV(String ivName, ANY newIV)
    throws FBRManagementException{
    if("NAME".equals(ivName)){connect_NAME((WSTRING)newIV); return;}
    super.connectIV(ivName, newIV);
    }
/** Connect the given variable variable to the input variable named NAME
  * @param newIV The variable to connect
  * @throws FBRManagementException An internal connection failed. */
  public void connect_NAME(WSTRING newIV) throws FBRManagementException{
    NAME = newIV;
    }
/** The default constructor. */
public FirstAgentFB(){
    super();
  }
/** {@inheritDoc}
* @param e {@inheritDoc} */
  public void serviceEvent(EventServer e){
    if (e == INIT) service_INIT();
  }
  public void service_INIT(){
  }
}
