/* Copyright (c)2015 Holobloc Inc. All rights reserved. */
package fb.rt.template;
import fb.datatype.*;
import fb.rt.*;
import fb.rt.events.*;
/** FUNCTION_BLOCK Simple1
  * @author JHC
  * @version 20150602/JHC
  */
public class Simple1 extends SimpleFB {
/** Input event qualifier */
  public BOOL QI = new BOOL();
/** Output event qualifier */
  public BOOL QO = new BOOL();
/** {@inheritDoc}
* @param s {@inheritDoc}
* @return {@inheritDoc}
* @throws FBRManagementException {@inheritDoc}
*/
  public ANY ivNamed(String s) throws FBRManagementException{
    if("QI".equals(s)) return QI;
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
    super.connectIV(ivName, newIV);
    }
/** Connect the given variable variable to the input variable named QI
  * @param newIV The variable to connect
  * @throws FBRManagementException An internal connection failed. */
  public void connect_QI(BOOL newIV) throws FBRManagementException{
    QI = newIV;
    }
/** The default constructor. */
public Simple1(){
    super();
  }
/** {@inheritDoc}
* @param e {@inheritDoc} */
public void serviceEvent(EventServer e){
QO.value=QI.value;
      CNF.serviceEvent(this);
}
}
