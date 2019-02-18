package ru.sadv1r.sjdbl.core;

import com.siebel.data.SiebelBusObject;
import com.siebel.data.SiebelException;
import lombok.extern.slf4j.Slf4j;
import ru.sadv1r.sjdbl.core.exception.SjdblException;

/**
 * Siebel Business Object
 *
 * @see <a href="https://docs.oracle.com/cd/E74890_01/books/ConfigApps/BusObj2.html">About Business Objects</a>
 */
@Slf4j
public class BusObj {
    private final SiebelBusObject siebelBusObject;

    public BusObj(SiebelBusObject siebelBusObject) {
        this.siebelBusObject = siebelBusObject;
    }

    /**
     * Returns a business component instance.
     *
     * @param busCompName the name of a business component
     * @return business component instance
     * @throws SjdblException
     */
    public BusComp getBusComp(final String busCompName) throws SjdblException {
        log.trace("Getting Business Component '{}'...", busCompName);
        try {
            return new BusComp(this, siebelBusObject.getBusComp(busCompName));
        } catch (SiebelException e) {
            throw SjdblException.create(e);
        }
    }

    /**
     * Returns the name of a business object.
     *
     * @return the name of a business object
     */
    public String name() {
        log.trace("Getting Business Object Name...");
        final String name = siebelBusObject.name();
        log.debug("Business Object name found: '{}'", name);
        return name;
    }

    /**
     * Releases a business object and the resources for this business object on the Siebel Server.
     */
    public void release() {
        log.debug("Releasing Business Object '{}'", name());
        siebelBusObject.release();
    }

    public SiebelBusObject getSiebelBusObject() {
        log.warn("Working with SiebelBusObject is not recommended!");
        return siebelBusObject;
    }
}