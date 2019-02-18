package ru.sadv1r.sjdbl.core;

import com.siebel.data.SiebelException;
import lombok.extern.slf4j.Slf4j;
import ru.sadv1r.sjdbl.core.exception.SjdblException;

@Slf4j
public class ZiebelDataBean extends SiebelDataBeanWrapper {
    public ZiebelDataBean(String url, String login, String password) throws SjdblException {
        this(url, login, password, SiebelDataBeanWrapper.DEFAULT_LANGUAGE);
    }

    public ZiebelDataBean(String url, String login, String password, String lang) throws SjdblException {
        super(url, login, password, lang);
    }

    public String lookupValue(final String type, final String name) throws SjdblException {
        log.trace("Lookup value of {} in {}", name, type);
        try {
            return siebelDataBean.invokeMethod("LookupValue", new String[]{type, name});
        } catch (SiebelException e) {
            throw SjdblException.create(e);
        }
    }

    public String lookupName(final String type, final String value) throws SjdblException {
        log.trace("Lookup name of {} in {}", value, type);
        try {
            return siebelDataBean.invokeMethod("LookupValue", new String[]{type, value});
        } catch (SiebelException e) {
            throw SjdblException.create(e);
        }
    }

    public ZiebelTransactionService getEaiTransactionService() throws SjdblException {
        log.trace("Retrieving service EAI Transaction Service");
        try {
            return new ZiebelTransactionService(siebelDataBean.getService("EAI Transaction Service"));
        } catch (SiebelException e) {
            throw SjdblException.create(e);
        }
    }

    public Transaction getTransaction() throws SjdblException {
        return new Transaction(getEaiTransactionService());
    }

    public Transaction beginTransaction() throws SjdblException {
        final Transaction transaction = getTransaction();
        transaction.begin();
        return transaction;
    }
}