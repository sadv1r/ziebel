package ru.sadv1r.sjdbl.core;

import com.siebel.data.SiebelException;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class BusCompIT {
    private SiebelDataBeanWrapper siebelDataBeanWrapper;

    private BusObj busObject;
    private BusComp busComp;

    @BeforeClass
    public static void start() throws Exception {
    }

    @Before
    public void setUp() throws Exception {
        siebelDataBeanWrapper = new SiebelDataBeanWrapper("Siebel://192.168.56.101:2321/SBA_82/FINSObjMgr_rus", "SADMIN", "Rjkjyrb1");

        busObject = siebelDataBeanWrapper.getBusObject("Account");
    }

    @After
    public void tearDown() throws Exception {
        if (busObject != null)
            busObject.release();

        if (busComp != null)
            busComp.release();

        if (siebelDataBeanWrapper != null)
            siebelDataBeanWrapper.logoff();
    }

    @Test
    public void activateField() throws SiebelException {
        busComp = busObject.getBusComp("Account");

        busComp.activateField("");
    }

    @Test
    public void setSearchExpr() throws SiebelException {
        busComp = busObject.getBusComp("Account");
        busComp.setSearchExpr("Lal");

    }

    @Test
    public void executeQuery() throws SiebelException {
        busComp = busObject.getBusComp("Account");
        busComp.setSearchExpr("Lal");
        //busComp.executeQuery(false);

    }

    @Test
    public void getFieldValue() throws SiebelException {
        busComp = busObject.getBusComp("Account");
        busComp = busObject.getBusComp("Account");
        //busComp.setSearchExpr("*");
        //busComp.executeQuery(false);
        //busComp.getFieldValue("Name");

    }

    @Test
    public void getSiebelBusObject() {
    }
}