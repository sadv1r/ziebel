package ru.sadv1r.sjdbl.core;

import com.siebel.data.SiebelBusObject;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import ru.sadv1r.sjdbl.core.exception.SjdblException;
import ru.sadv1r.sjdbl.core.exception.SjdblRequestException;

import static org.assertj.core.api.Assertions.assertThat;

public class BusObjIT {
    private SiebelDataBeanWrapper siebelDataBeanWrapper;

    private BusObj busObject;

    @BeforeClass
    public static void start() throws Exception {
    }

    @Before
    public void setUp() throws Exception {
        siebelDataBeanWrapper = new SiebelDataBeanWrapper("Siebel://192.168.56.101:2321/SBA_82/FINSObjMgr_rus", "SADMIN", "Rjkjyrb1");
    }

    @After
    public void tearDown() throws Exception {
        if (busObject != null)
            busObject.release();

        if (siebelDataBeanWrapper != null)
            siebelDataBeanWrapper.logoff();
    }

    @Test
    public void name() throws SjdblException {
        busObject = siebelDataBeanWrapper.getBusObject("Account");

        assertThat(busObject.name()).isEqualTo("Account");
    }

    @Test
    public void getBusComp() throws SjdblException {
        busObject = siebelDataBeanWrapper.getBusObject("Account");
        BusComp busComp = busObject.getBusComp("Account");

        assertThat(busComp).isNotNull();
        busComp.release();
    }

    @Test(expected = SjdblRequestException.class)
    public void release() throws SjdblException {
        busObject = siebelDataBeanWrapper.getBusObject("Account");

        busObject.release();
        System.out.println(busObject.getBusComp("Account"));
    }

    @Test
    public void getSiebelBusObject() throws SjdblException {
        busObject = siebelDataBeanWrapper.getBusObject("Account");

        SiebelBusObject siebelBusObject = busObject.getSiebelBusObject();
        
        assertThat(siebelBusObject).isNotNull();
    }
}