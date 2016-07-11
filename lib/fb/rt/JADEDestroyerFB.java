/* Copyright (c)2010 TUT. All rights reserved. */
package fb.rt;

import fb.datatype.*;
import fb.rt.*;
import fb.rt.events.*;

import fi.tut.pe.acra.mas.CCFactory;

/** FUNCTION_BLOCK JADEDestroyerFB
  * @author AnLo
  * @version 20100422/AnLo
  */
public class JADEDestroyerFB extends FBInstance
{
/** Normal Execution Request */
 public EventServer REQ = new EventInput(this);
/** Execution Confirmation */
 public EventOutput CNF = new EventOutput();
/** {@inheritDoc}
* @param s {@inheritDoc}
* @return {@inheritDoc}
*/
  public EventServer eiNamed(String s){
    if("REQ".equals(s)) return REQ;
    return super.eiNamed(s);}
/** {@inheritDoc}
* @param s {@inheritDoc}
* @return {@inheritDoc}
*/
  public EventOutput eoNamed(String s){
    if("CNF".equals(s)) return CNF;
    return super.eoNamed(s);}
/** The default constructor. */
public JADEDestroyerFB(){
    super();
  }
/** {@inheritDoc}
* @param e {@inheritDoc} */
  public void serviceEvent(EventServer e){
    if (e == REQ) service_REQ();
  }
  public void service_REQ(){
  	CCFactory.destroyJADE();
  }
}
