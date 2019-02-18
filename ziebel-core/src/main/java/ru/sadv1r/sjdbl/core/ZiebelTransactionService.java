package ru.sadv1r.sjdbl.core;

import com.siebel.data.SiebelService;
import ru.sadv1r.sjdbl.core.exception.SjdblException;

/**
 * @author sadv1r
 */
class ZiebelTransactionService extends SiebelServiceWrapper {
    ZiebelTransactionService(SiebelService siebelService) {
        super(siebelService);
    }

    public void beginTransaction() throws SjdblException {
        final ZiebelPropertySet propertySetIn = new ZiebelPropertySet();
        final ZiebelPropertySet propertySetOut = new ZiebelPropertySet();
        invokeMethod("BeginTransaction", propertySetIn, propertySetOut);
    }

    public void endTransaction(final boolean isAbort) throws SjdblException {
        final ZiebelPropertySet propertySetIn = new ZiebelPropertySet();
        final ZiebelPropertySet propertySetOut = new ZiebelPropertySet();
        if (isAbort) propertySetIn.setProperty("Is Abort", "True");
        invokeMethod("EndTransaction", propertySetIn, propertySetOut);
    }
}
