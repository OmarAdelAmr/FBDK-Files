/* Copyright (c)2015 Rockwell Automation. All rights reserved. */
package fb.rt.hmi; import java.awt.*; import java.awt.event.*;
import fb.datatype.*;
import fb.rt.*;
import fb.rt.events.*;
/** FUNCTION_BLOCK UPDATE
  * @author JHC
  * @version 20150602/JHC
  */
public class IN_EVENT extends FBInstance implements ActionListener{
/** VAR QI */
  public BOOL QI = new BOOL();
/** Label */
  public WSTRING LABEL = new WSTRING();
/** VAR QO */
  public BOOL QO = new BOOL();
/** Service Initialization */
 public EventServer INIT = new EventInput(this);
/** Initialization Confirm */
 public EventOutput INITO = new EventOutput();
/** Event Indication */
 public EventOutput IND = new EventOutput();
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
    if("IND".equals(s)) return IND;
    return super.eoNamed(s);}
/** {@inheritDoc}
* @param s {@inheritDoc}
* @return {@inheritDoc}
* @throws FBRManagementException {@inheritDoc}
*/
  public ANY ivNamed(String s) throws FBRManagementException{
    if("QI".equals(s)) return QI;
    if("LABEL".equals(s)) return LABEL;
    return super.ivNamed(s);}
/** {@inheritDoc}
* @param s {@inheritDoc}
* @return {@inheritDoc}
* @throws FBRManagementException {@inheritDoc}
*/
  public ANY ovNamed(String s) throws FBRManagementException{
    if("QO".equals(s)) return QO;
    return super.ovNamed(s);}
/** {@inheritDoc}
* @param ivName {@inheritDoc}
* @param newIV {@inheritDoc}
* @throws FBRManagementException {@inheritDoc} */
  public void connectIV(String ivName, ANY newIV)
    throws FBRManagementException{
    if("QI".equals(ivName)){connect_QI((BOOL)newIV); return;}
    if("LABEL".equals(ivName)){connect_LABEL((WSTRING)newIV); return;}
    super.connectIV(ivName, newIV);
    }
/** Connect the given variable variable to the input variable named QI
  * @param newIV The variable to connect
  * @throws FBRManagementException An internal connection failed. */
  public void connect_QI(BOOL newIV) throws FBRManagementException{
    QI = newIV;
    }
/** Connect the given variable variable to the input variable named LABEL
  * @param newIV The variable to connect
  * @throws FBRManagementException An internal connection failed. */
  public void connect_LABEL(WSTRING newIV) throws FBRManagementException{
    LABEL = newIV;
    }
/** The default constructor. */
public UPDATE(){
    super();
    QI.initializeNoException("true");
  }
/** {@inheritDoc}
* @param e {@inheritDoc} */
  public void serviceEvent(EventServer e){
    if (e == INIT) service_INIT();
  }
  public void service_INIT(){
  }
}
