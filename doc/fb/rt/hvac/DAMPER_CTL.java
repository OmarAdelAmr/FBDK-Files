// Copyright (c)2006 Holobloc Inc.
//Published under the Academic Free License
//See http://www.opensource.org/licenses/afl-3.0.php.
package fb.rt.hvac;

import fb.datatype.*;
import fb.rt.*;

/**
 * FUNCTION_BLOCK DAMPER_CTL
 * 
 * @author JHC
 * @version 20060501/JHC - Extended from FBClient
 */
public class DAMPER_CTL extends FBClient {
    /** <B>true</B> when damper is fully open. */
    private boolean isOpen = false;

    public REAL SP = new REAL();

    public REAL PV = new REAL();

    public REAL SLOPE = new REAL();

    public REAL DEV = new REAL();

    public BOOL OPEN = new BOOL();

    public BOOL CLOSE = new BOOL();

    public ANY ivNamed(String s) throws FBRManagementException {
        if (s.equals("SP")) return SP;
        if (s.equals("PV")) return PV;
        if (s.equals("SLOPE")) return SLOPE;
        if (s.equals("DEV")) return DEV;
        return super.ivNamed(s);
    }

    public ANY ovNamed(String s) throws FBRManagementException {
        if (s.equals("OPEN")) return OPEN;
        if (s.equals("CLOSE")) return CLOSE;
        return super.ovNamed(s);
    }

    public void connectIV(String ivName, ANY newIV)
            throws FBRManagementException {
        if (ivName.equals("SP")) {
            connect_SP((REAL) newIV);
            return;
        }
        if (ivName.equals("PV")) {
            connect_PV((REAL) newIV);
            return;
        }
        if (ivName.equals("SLOPE")) {
            connect_SLOPE((REAL) newIV);
            return;
        }
        if (ivName.equals("DEV")) {
            connect_DEV((REAL) newIV);
            return;
        }
        super.connectIV(ivName, newIV);
    }

    public void connect_SP(REAL newIV) throws FBRManagementException {
        SP = newIV;
    }

    public void connect_PV(REAL newIV) throws FBRManagementException {
        PV = newIV;
    }

    public void connect_SLOPE(REAL newIV) throws FBRManagementException {
        SLOPE = newIV;
    }

    public void connect_DEV(REAL newIV) throws FBRManagementException {
        DEV = newIV;
    }

    public DAMPER_CTL() {
        super();
        SP.initializeNoException("50");
    }

    public void service_INIT() {
        isOpen = false;
        OPEN.value = false;
        CLOSE.value = false;
        INITO.serviceEvent(this);
    }

    public void service_REQ() {
        float err = PV.value - SP.value;
        if (OPEN.value) {
            if (err >= 0.0) // Stop opening if past SP
            OPEN.value = false;
            else if (SLOPE.value <= (DEV.value * 0.025)) {
                // Stop opening if damper stop is detected
                isOpen = true;
                OPEN.value = false;
            }
        } else if (CLOSE.value) {
            if (err <= 0.0) CLOSE.value = false;
        } else {// Move when outside of 5% or 2.5 sigma
            double dev = Math.max(0.05, 2.5 * DEV.value);
            if (!isOpen && (err < -dev)) OPEN.value = true;
            else if (err > dev) {
                CLOSE.value = true;
                isOpen = false;
            }
        }
        CNF.serviceEvent(this);
    }
}
