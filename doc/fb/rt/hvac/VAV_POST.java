//Copyright (c)2006 Holobloc Inc.
//Licensed under the Academic Free License version 3.0
//See http://www.opensource.org/licenses/afl-3.0.php
package fb.rt.hvac;

import fb.datatype.*;
import fb.rt.*;

/**
 * FUNCTION_BLOCK VAV_POST
 * 
 * @author JHC
 * @version 20060501/JHC - Implemented as REQServer, changed FV to FD.
 * @version 20060411/JHC
 */
public class VAV_POST extends FBInstance implements REQServer {
    public UINT POS = new UINT();

    public REAL FSF = new REAL((float)1000);

    public REAL FD = new REAL();

    public REAL SFS = new REAL((float)1000);

    public UINT ADMAX = new UINT(4096);

    public REAL FLOW = new REAL();

    public UINT AD = new UINT();

    public REQEvent CLK = new REQEvent(this);

    public EventOutput CNF = new EventOutput();

    public EventServer eiNamed(String s) {
        if (s.equals("CLK")) return CLK;
        return super.eiNamed(s);
    }

    public EventOutput eoNamed(String s) {
        if (s.equals("CNF")) return CNF;
        return super.eoNamed(s);
    }

    public ANY ivNamed(String s) throws FBRManagementException {
        if (s.equals("POS")) return POS;
        if (s.equals("FSF")) return FSF;
        if (s.equals("FD")) return FD;
        if (s.equals("SFS")) return SFS;
        if (s.equals("ADMAX")) return ADMAX;
        return super.ivNamed(s);
    }

    public ANY ovNamed(String s) throws FBRManagementException {
        if (s.equals("FLOW")) return FLOW;
        if (s.equals("AD")) return AD;
        return super.ovNamed(s);
    }

    public void connectIV(String ivName, ANY newIV)
            throws FBRManagementException {
        if (ivName.equals("POS")) {
            connect_POS((UINT) newIV);
            return;
        }
        if (ivName.equals("FSF")) {
            connect_FSF((REAL) newIV);
            return;
        }
        if (ivName.equals("FD")) {
            connect_FD((REAL) newIV);
            return;
        }
        if (ivName.equals("SFS")) {
            connect_SFS((REAL) newIV);
            return;
        }
        if (ivName.equals("ADMAX")) {
            connect_ADMAX((UINT) newIV);
            return;
        }
        super.connectIV(ivName, newIV);
    }

    public void connect_POS(UINT newIV) throws FBRManagementException {
        POS = newIV;
    }

    public void connect_FSF(REAL newIV) throws FBRManagementException {
        FSF = newIV;
    }

    public void connect_FD(REAL newIV) throws FBRManagementException {
        FD = newIV;
    }

    public void connect_SFS(REAL newIV) throws FBRManagementException {
        SFS = newIV;
    }

    public void connect_ADMAX(UINT newIV) throws FBRManagementException {
        ADMAX = newIV;
    }

    public VAV_POST() {
        super();
    }

    public void service_REQ() {
        double r = FSF.value*POS.value/100 + FD.value * (2 * Math.random() - 1);
        r = Math.min(FSF.value,Math.max(0.0, r));
        FLOW.value = (float) r;
        r = (r / SFS.value);
        r = r * r * ADMAX.value;
        AD.value = Math.min(ADMAX.value - 1, (int) r);
        CNF.serviceEvent(this);
    }
}
