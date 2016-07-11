//Copyright (c)2006 Holobloc Inc.
//Licensed under the Academic Free License version 3.0
//See http://www.opensource.org/licenses/afl-3.0.php
package fb.rt.math;

import fb.datatype.*;
import fb.rt.*;

/**
 * FUNCTION_BLOCK REGRESS
 * 
 * @author JHC
 * @version 20060504/JHC - Modified <TT>service_REQ()</TT> to show
 *          predicted <TT>OUT</TT> at end of next time increment.
 * @version 20060428/JHC - Extended from FBClient.
 */
public class REGRESS extends FBClient {
    /** The circular buffer of sampled data, default size = 16 */
    private float[] y = new float[16];

    /** The sum of sample data */
    private double sumy = 0.0;

    /** The circular buffer of sample squared values */
    private double[] y2 = new double[16];

    /** The sum of squared sample values */
    private double sumy2 = 0.0;

    /** The sum of time values */
    private int sumx = 0;

    /** Differences of time-squared values */
    private int sxx = 0;

    /** The sum of t*y values */
    private double sumxy = 0;

    /** The pointer to the current data insertion point. */
    private int ptr = 0;

    /** The number of sample points (minimum = 3) */
    private int n = 3;

    public REAL IN = new REAL();

    public UINT N = new UINT(16);

    public REAL OUT = new REAL();

    public REAL SLOPE = new REAL();

    public REAL DEV = new REAL();

    public ANY ivNamed(String s) throws FBRManagementException {
        if (s.equals("IN")) return IN;
        if (s.equals("N")) return N;
        return super.ivNamed(s);
    }

    public ANY ovNamed(String s) throws FBRManagementException {
        if (s.equals("OUT")) return OUT;
        if (s.equals("SLOPE")) return SLOPE;
        if (s.equals("DEV")) return DEV;
        return super.ovNamed(s);
    }

    public void connectIV(String ivName, ANY newIV)
            throws FBRManagementException {
        if (ivName.equals("IN")) {
            connect_IN((REAL) newIV);
            return;
        }
        if (ivName.equals("N")) {
            connect_N((UINT) newIV);
            return;
        }
        super.connectIV(ivName, newIV);
    }

    public void connect_IN(REAL newIV) throws FBRManagementException {
        IN = newIV;
    }

    public void connect_N(UINT newIV) throws FBRManagementException {
        N = newIV;
    }

    public REGRESS() {
        super();
    }

    public void service_INIT() {
        n = Math.max(N.value, 3);
        sumx = n * (n - 1) / 2;
        int sumx2 = n * (n - 1) * (2 * n - 1) / 6;
        sxx = sumx2 - sumx * sumx / n;
        ptr = 0;
        y = new float[n];
        y2 = new double[n];
        for (int i = 0; i < n; i++) {
            y[i] = 0;
            y2[i] = 0.0;
        }
        sumy = 0.0;
        sumy2 = 0.0;
        sumxy = 0.0;
        QO.value = false;
        INITO.serviceEvent(this);
    }

    public void service_REQ() {
        float yval = IN.value;
        double y2val = yval * yval;
        sumy += yval - y[ptr];
        y[ptr] = yval;
        sumy2 += y2val - y2[ptr];
        y2[ptr] = y2val;
        sumxy += yval * n;
        for (int i = 0; i < n; i++)
            sumxy -= y[i];
        ptr++;
        if (ptr == n) {
            ptr = 0;
            QO.value = true;
        }
        if (QO.value) { // Compute output values
            double yave = sumy / n;
            double sxy = sumxy - sumx * yave;
            double slope = sxy / sxx;
            SLOPE.value = (float) slope;
            OUT.value = (float) (yave + slope * (1 + sumx / n));
            double syy = sumy2 - sumy * yave;
            double dev = (syy - slope * sxy) / (n - 2);
            DEV.value = (float) Math.sqrt(Math.max(dev, 0.0));
        }
        CNF.serviceEvent(this);
    }
}
