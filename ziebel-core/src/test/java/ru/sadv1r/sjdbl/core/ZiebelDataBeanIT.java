package ru.sadv1r.sjdbl.core;

import com.siebel.data.SiebelDataBean;
import com.siebel.data.SiebelException;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import ru.sadv1r.sjdbl.core.exception.SjdblAuthException;
import ru.sadv1r.sjdbl.core.exception.SjdblException;
import ru.sadv1r.sjdbl.core.exception.SjdblObjectNotFoundException;
import ru.sadv1r.sjdbl.core.exception.SjdblRequestException;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import static org.assertj.core.api.Assertions.assertThat;

public class ZiebelDataBeanIT {
    private static ZiebelDataBean ziebelDataBean;

    private static Properties properties;

    private static String link;
    private static String login;
    private static String pwd;

    private static String testLovType;
    private static String testLovName;
    private static String testLovValue;

    @BeforeClass
    public static void start() throws IOException, SjdblException {
        properties = new Properties();
        properties.load(new FileInputStream("ziebel.properties"));

        link = properties.getProperty("link");
        login = properties.getProperty("login");
        pwd = properties.getProperty("pwd");

        testLovType = properties.getProperty("test.lov.type");
        testLovName = properties.getProperty("test.lov.name");
        testLovValue = properties.getProperty("test.lov.value");

        ziebelDataBean = new ZiebelDataBean(link, login, pwd);
    }

    @AfterClass
    public static void tearDown() throws SjdblException {
        if (ziebelDataBean != null) {
            ziebelDataBean.logoff();
        }
    }

    @Test
    public void getBusObject() throws SiebelException {
        BusObj busObject = ziebelDataBean.getBusObject("Account");

        assertThat(busObject).isNotNull();
        busObject.release();
    }

    @Test(expected = SjdblObjectNotFoundException.class)
    public void getBusObjectWrong() throws SiebelException {
        ziebelDataBean.getBusObject("WrongBusObjName");
    }

    @Test
    public void logoff() throws SiebelException {
        final ZiebelDataBean ziebelDataBeanLocal = new ZiebelDataBean(link, login, pwd);

        ziebelDataBeanLocal.logoff();

        assertThat(ziebelDataBeanLocal.isLoggedIn()).isFalse();
    }

    @Test(expected = SjdblAuthException.class)
    public void loginWrong() throws SiebelException {
        new ZiebelDataBean(link, "WrongLoginName", pwd);
    }

    @Test
    public void invokeMethod() throws SjdblException {
        assertThat(ziebelDataBean.invokeMethod("LookupValue", testLovType, testLovName)).isEqualTo(testLovValue);
    }

    @Test(expected = SjdblRequestException.class)
    public void invokeMethodWrong() throws SjdblException {
        ziebelDataBean.invokeMethod("WrongMethodName", testLovType, testLovName);
    }

    @Test
    public void lookupValue() throws SjdblException {
        assertThat(ziebelDataBean.lookupValue(testLovType, testLovName)).isEqualTo(testLovValue);
    }

    @Test
    public void lookupName() throws SjdblException {
        assertThat(ziebelDataBean.lookupName(testLovType, testLovValue)).isEqualTo(testLovName);
    }

    @Test
    public void getLoginId() {
        assertThat(ziebelDataBean.getLoginId()).isEqualTo(properties.getProperty("test.user.id"));
    }

    @Test
    public void getLoginName() {
        assertThat(ziebelDataBean.getLoginName()).isEqualTo(properties.getProperty("test.user.login"));
    }

    @Test
    public void getPositionId() {
        assertThat(ziebelDataBean.getPositionId()).isEqualTo(properties.getProperty("test.user.position.id"));
    }

    @Test
    public void getPositionName() {
        assertThat(ziebelDataBean.getPositionName()).isEqualTo(properties.getProperty("test.user.position.name"));
    }

    @Test
    public void getService() throws SjdblException {
        final SiebelServiceWrapper siebelServiceWrapper = ziebelDataBean.getService("EAI Transaction Service");
        assertThat(siebelServiceWrapper).isNotNull();

        siebelServiceWrapper.release();
    }

    @Test(expected = SjdblRequestException.class)
    public void getServiceWrong() throws SjdblException {
        ziebelDataBean.getService("WrongServiceName");
    }

    @Test
    public void newPropertySet() {
        final SiebelPropertySetWrapper siebelPropertySetWrapper = ziebelDataBean.newPropertySet();
        assertThat(siebelPropertySetWrapper).isNotNull();

        siebelPropertySetWrapper.reset();
    }

    @Test
    public void getSiebelDataBean() {
        final SiebelDataBean siebelDataBean = ziebelDataBean.getSiebelDataBean();
        assertThat(siebelDataBean).isNotNull();
    }

    @Test
    public void isLoggedIn() {
        assertThat(ziebelDataBean.isLoggedIn()).isTrue();
    }
}