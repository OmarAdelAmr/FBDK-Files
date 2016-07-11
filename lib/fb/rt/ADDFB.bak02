/* Copyright (c)2015 FraunhoferIGD. All rights reserved. */
package fb.rt;
import fb.datatype.*;
import fb.rt.*;
import fb.rt.events.*;
/** FUNCTION_BLOCK ADDFB
  * @author Sadik
  * @version 20150515/Sadik
  */
public class ADDFB extends FBInstance
{
/** VAR N1 */
  public BOOL N1 = new BOOL();
/** VAR N2 */
  public BOOL N2 = new BOOL();
/** VAR N3 */
  public BOOL N3 = new BOOL();
/** Normal Execution Request */
 public EventServer CALC = new EventInput(this);
/** Execution Confirmation */
 public EventOutput CNF = new EventOutput();
/** {@inheritDoc}
* @param s {@inheritDoc}
* @return {@inheritDoc}
*/
  public EventServer eiNamed(String s){
    if("CALC".equals(s)) return CALC;
    return super.eiNamed(s);}
/** {@inheritDoc}
* @param s {@inheritDoc}
* @return {@inheritDoc}
*/
  public EventOutput eoNamed(String s){
    if("CNF".equals(s)) return CNF;
    return super.eoNamed(s);}
/** {@inheritDoc}
* @param s {@inheritDoc}
* @return {@inheritDoc}
* @throws FBRManagementException {@inheritDoc}
*/
  public ANY ivNamed(String s) throws FBRManagementException{
    if("N1".equals(s)) return N1;
    if("N2".equals(s)) return N2;
    return super.ivNamed(s);}
/** {@inheritDoc}
* @param s {@inheritDoc}
* @return {@inheritDoc}
* @throws FBRManagementException {@inheritDoc}
*/
  public ANY ovNamed(String s) throws FBRManagementException{
    if("N3".equals(s)) return N3;
    return super.ovNamed(s);}
/** {@inheritDoc}
* @param ivName {@inheritDoc}
* @param newIV {@inheritDoc}
* @throws FBRManagementException {@inheritDoc} */
  public void connectIV(String ivName, ANY newIV)
    throws FBRManagementException{
    if("N1".equals(ivName)){connect_N1((BOOL)newIV); return;}
    if("N2".equals(ivName)){connect_N2((BOOL)newIV); return;}
    super.connectIV(ivName, newIV);
    }
/** Connect the given variable variable to the input variable named N1
  * @param newIV The variable to connect
  * @throws FBRManagementException An internal connection failed. */
  public void connect_N1(BOOL newIV) throws FBRManagementException{
    N1 = newIV;
    }
/** Connect the given variable variable to the input variable named N2
  * @param newIV The variable to connect
  * @throws FBRManagementException An internal connection failed. */
  public void connect_N2(BOOL newIV) throws FBRManagementException{
    N2 = newIV;
    }
/** The default constructor. */
public ADDFB(){
    super();
  }
/** {@inheritDoc}
* @param e {@inheritDoc} */
  public void serviceEvent(EventServer e){
    if (e == CALC) service_CALC();
  }
  public void service_CALC(){
  }
}
