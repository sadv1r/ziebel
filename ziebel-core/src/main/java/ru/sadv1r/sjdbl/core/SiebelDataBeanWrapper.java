package ru.sadv1r.sjdbl.core;

import com.siebel.data.SiebelDataBean;
import com.siebel.data.SiebelException;
import lombok.Getter;
import lombok.experimental.NonFinal;
import lombok.experimental.PackagePrivate;
import lombok.extern.slf4j.Slf4j;
import ru.sadv1r.sjdbl.core.exception.SjdblException;

/**
 * Siebel Data Bean Wrapper
 *
 * @see <a href="https://docs.oracle.com/cd/E74890_01/books/OIRef/Java_Quick_Reference2.html">Bookshelf: Data Bean Methods for Siebel Java Data Bean</a>
 */
@Slf4j
public class SiebelDataBeanWrapper {
    public static final String DEFAULT_LANGUAGE = "enu";

    @PackagePrivate
    final SiebelDataBean siebelDataBean;
    @NonFinal
    @Getter
    private boolean loggedIn = true;

    public SiebelDataBeanWrapper(final String url, final String login, final String password) throws SjdblException {
        this(url, login, password, DEFAULT_LANGUAGE);
    }

    public SiebelDataBeanWrapper(final String url, final String login, final String password, final String lang) throws SjdblException {
        log.info("Connecting to {} by {}", url, login);
        try {
            siebelDataBean = new SiebelDataBean();
            siebelDataBean.login(url, login, password, lang);
        } catch (SiebelException e) {
            throw SjdblException.create(e);
        }
    }

    public String getLoginId() {
        return siebelDataBean.loginId();
    }

    public String getLoginName() {
        return siebelDataBean.loginName();
    }

    public String getPositionId() {
        return siebelDataBean.positionId();
    }

    public String getPositionName() {
        return siebelDataBean.positionName();
    }

    public BusObj getBusObject(final String busObjName) throws SjdblException {
        try {
            return new BusObj(siebelDataBean.getBusObject(busObjName));
        } catch (SiebelException e) {
            throw SjdblException.create(e);
        }
    }

    public String invokeMethod(final String methodName, final String... args) throws SjdblException {
        log.trace("Invoking method {} with args {}", methodName, args);
        try {
            return siebelDataBean.invokeMethod(methodName, args);
        } catch (SiebelException e) {
            throw SjdblException.create(e);
        }
    }

    public SiebelServiceWrapper getService(final String name) throws SjdblException {
        log.trace("Retrieving service {}", name);
        try {
            return new SiebelServiceWrapper(siebelDataBean.getService(name));
        } catch (SiebelException e) {
            throw SjdblException.create(e);
        }
    }

    public SiebelPropertySetWrapper newPropertySet() {
        log.trace("Creating new Property Set");
        return new SiebelPropertySetWrapper(siebelDataBean.newPropertySet());
    }

    public void logoff() throws SjdblException {
        log.info("Disconnecting");
        if (siebelDataBean != null && loggedIn) {
            try {
                siebelDataBean.logoff();
                loggedIn = false;
            } catch (SiebelException e) {
                throw SjdblException.create(e);
            }
        }
    }

    public SiebelDataBean getSiebelDataBean() {
        log.warn("Working with SiebelDataBean is not recommended!");
        return siebelDataBean;
    }
}