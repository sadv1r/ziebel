package ru.sadv1r.sjdbl.core;

import com.siebel.data.SiebelDataBean;
import com.siebel.data.SiebelException;
import com.siebel.data.SiebelPropertySet;
import com.siebel.data.SiebelService;
import lombok.extern.slf4j.Slf4j;
import ru.sadv1r.sjdbl.core.exception.SjdblException;

/**
 * Siebel service wrapper
 *
 * @author sadv1r
 */
@Slf4j
public class SiebelServiceWrapper {
    private final SiebelService siebelService;

    public SiebelServiceWrapper(final SiebelService siebelService) {
        this.siebelService = siebelService;
    }

    public boolean invokeMethod(final String methodName,
                                final ZiebelPropertySet propertySetIn,
                                final ZiebelPropertySet propertySetOut) throws SjdblException {
        final SiebelPropertySet siebelPropertySetIn = propertySetIn.getSiebelPropertySet();
        final SiebelPropertySet siebelPropertySetOut = propertySetOut.getSiebelPropertySet();
        log.trace("Invoking method {} with property sets: {}, {}", methodName,
                siebelPropertySetIn.encodeAsString(), siebelPropertySetOut.encodeAsString());
        try {
            return siebelService.invokeMethod(methodName, siebelPropertySetIn, siebelPropertySetOut);
        } catch (SiebelException e) {
            throw SjdblException.create(e);
        }
    }

    public String getName() {
        return siebelService.getName();
    }

    public void release() {
        log.debug("Releasing Business Service '{}'", getName());
        siebelService.release();
    }

    public SiebelService getSiebelService() {
        log.warn("Working with SiebelService is not recommended!");
        return siebelService;
    }
}
