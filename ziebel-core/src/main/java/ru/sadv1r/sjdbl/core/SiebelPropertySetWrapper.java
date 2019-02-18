package ru.sadv1r.sjdbl.core;

import com.siebel.data.SiebelPropertySet;

/**
 * @author sadv1r
 */
public class SiebelPropertySetWrapper {
    private final SiebelPropertySet siebelPropertySet;

    public SiebelPropertySetWrapper() {
        this(new SiebelPropertySet());
    }

    public SiebelPropertySetWrapper(final SiebelPropertySet siebelPropertySet) {
        this.siebelPropertySet = siebelPropertySet;
    }

    public void setProperty(final String name, final String value) {
        siebelPropertySet.setProperty(name, value);
    }

    public boolean reset() {
        return siebelPropertySet.reset();
    }

    SiebelPropertySet getSiebelPropertySet() {
        return siebelPropertySet;
    }
}
