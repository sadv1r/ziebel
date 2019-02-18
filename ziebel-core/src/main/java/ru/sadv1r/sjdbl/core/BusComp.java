package ru.sadv1r.sjdbl.core;

import com.siebel.data.SiebelBusComp;
import com.siebel.data.SiebelException;
import com.siebel.data.SiebelPropertySet;
import lombok.extern.slf4j.Slf4j;
import ru.sadv1r.sjdbl.core.exception.SjdblException;

/**
 * Siebel Business Component
 *
 * @see <a href="https://docs.oracle.com/cd/E74890_01/books/ConfigApps/BusComp3.html">Overview of Business Components</a>
 * @see <a href="https://docs.oracle.com/cd/E74890_01/books/OIRef/Java_Quick_Reference3.html">Business Component Methods for Siebel Java Data Bean</a>
 */
@Slf4j
public class BusComp {
    private final BusObj busObj;
    private final SiebelBusComp siebelBusComp;

    private StringBuilder query = new StringBuilder();

    public BusComp(final BusObj busObj, final SiebelBusComp siebelBusComp) {
        this.busObj = busObj;
        this.siebelBusComp = siebelBusComp;
    }

    //TODO Think about the needs of this
    public BusComp(final BusObj busObj, final BusComp busComp) {
        this.busObj = busObj;
        this.siebelBusComp = busComp.siebelBusComp;
    }

    /**
     * Returns the business object that the business component references.
     *
     * @return business object that the business component references
     */
    public BusObj getBusObj() {
        return busObj;
    }

    /**
     * Activates a field.
     * You must use the ActivateField method to activate a field before you can perform a query for the business component.
     * <p>
     * By default, a field is inactive except in the following situations:
     * <ul><li>The field is a system field, such as Id, Created, Created By, Updated, or Updated By.
     * <li>The Force Active property of the field is TRUE.
     * <li>The Link Specification property of the field is TRUE
     * <li>The field is included in an applet, and this applet references a business component that is active.
     * For a field in a list applet, the Show In List list column property is TRUE.
     * <li>Siebel CRM calls the ActivateField method on the field, and then runs the ExecuteQuery method.</ul>
     * <p>
     * Note the following:
     * <ul><li>If Siebel CRM activates a field after it queries a business component,
     * then it must requery the business component before the user can access the value in that field.
     * If Siebel CRM does not requery the business component, then it returns a value of 0.
     * <li>If Siebel CRM calls the ActivateField method after it calls the ExecuteQuery method,
     * then the ActivateField method deletes the query context.
     * <li>The ActivateField method causes Siebel CRM to include the field in the SQL statement that the ExecuteQuery method starts.
     * If Siebel CRM activates a field, and then if a statement in the GetFieldValue method or the SetFieldValue
     * method references the file before Siebel CRM performs a statement from the ExecuteQuery method,
     * then the activation has no effect. The query contains an empty value because
     * Siebel CRM does not return the activated field through this query.
     * <li>Siebel CRM does not restrict the maximum number of fields that the ActivateField method can activate.
     * This number depends on the SQL query limitations of the database that your deployment uses.</ul>
     *
     * @param fieldName variable or literal that contains the name of the field. Must match exactly the field name that displays in Siebel Tools, including the same case
     * @return {@code true} if the activation is successful, {@code false} if the activation is not successful
     * @throws SjdblException
     */
    public boolean activateField(final String fieldName) throws SjdblException {
        log.trace("Activating field '{}'...", fieldName);
        try {
            return siebelBusComp.activateField(fieldName);
        } catch (SiebelException e) {
            throw SjdblException.create(e);
        }
    }

    /**
     * Activate multiple fields.
     *
     * @param propertySet Property set that identifies a collection of properties.
     *                    These properties identify the fields that Siebel CRM must activate.
     * @return {@code true} if the activation is successful, {@code false} if the activation is not successful
     * @throws SjdblException
     */
    public boolean activateFields(final SiebelPropertySet propertySet) throws SjdblException {
        log.trace("Activating propertySet '{}'...", propertySet);
        try {
            return siebelBusComp.activateMultipleFields(propertySet);
        } catch (SiebelException e) {
            throw SjdblException.create(e);
        }
    }

    public void setViewMode(final ViewMode viewMode) throws SjdblException {
        setViewMode(viewMode.code);
    }

    public void setViewMode(final int viewMode) throws SjdblException {
        log.trace("Setting ViewMode to {}", viewMode);
        try {
            siebelBusComp.setViewMode(viewMode);
        } catch (SiebelException e) {
            throw SjdblException.create(e);
        }
    }


    public void clearToQuery() throws SjdblException {
        log.trace("Clearing query");
        try {
            siebelBusComp.clearToQuery();
        } catch (SiebelException e) {
            throw SjdblException.create(e);
        }
    }

    public void setSearchExpr(String searchExpr) throws SjdblException {
        log.trace("Setting search expression: {}", searchExpr);

        try {
            siebelBusComp.setSearchExpr(searchExpr);

            if (query.length() != 0)
                query.append(" AND ");
            query.append(searchExpr);
        } catch (SiebelException e) {
            throw SjdblException.create(e);
        }
    }

    public void setSearchSpec(String field, String value) throws SjdblException {
        log.trace("Setting search field [{}] to [{}]", field, value);

        if (query.length() != 0) {
            query.append(" AND ");
        }
        query.append("[").append(field).append("] = '").append(value).append("'"); //FIXME IS NULL, LIKE Problems

        try {
            siebelBusComp.setSearchSpec(field, value);
        } catch (SiebelException e) {
            throw SjdblException.create(e);
        }
    }

    public void executeQuery(boolean b) throws SjdblException {
        log.debug("Executing query: {}", query);

        try {
            siebelBusComp.executeQuery(b);
        } catch (SiebelException e) {
            throw SjdblException.create(e);
        }
    }

    public void executeQuery2(boolean b, boolean c) throws SjdblException {
        log.debug("Executing query: {}", query);

        try {
            siebelBusComp.executeQuery2(b, c);
        } catch (SiebelException e) {
            throw SjdblException.create(e);
        }
    }

    public boolean firstRecord() throws SjdblException {
        try {
            return siebelBusComp.firstRecord();
        } catch (SiebelException e) {
            throw SjdblException.create(e);
        }
    }

    public boolean nextRecord() throws SjdblException {
        try {
            return siebelBusComp.nextRecord();
        } catch (SiebelException e) {
            throw SjdblException.create(e);
        }
    }

    public String getFieldValue(String fieldName) throws SjdblException {
        try {
            return siebelBusComp.getFieldValue(fieldName);
        } catch (SiebelException e) {
            throw SjdblException.create(e);
        }
    }

    public BusComp getMvgBusComp(String fieldName) throws SjdblException {
        log.trace("Getting MVG Business Component '{}'", fieldName);
        try {
            return new BusComp(busObj, siebelBusComp.getMVGBusComp(fieldName));
        } catch (SiebelException e) {
            throw SjdblException.create(e);
        }
    }

    public void newRecord(final boolean b) throws SjdblException {
        log.trace("Creating new record"); //TODO Add Component name
        try {
            siebelBusComp.newRecord(b);
        } catch (SiebelException e) {
            throw SjdblException.create(e);
        }
    }

    public void setFieldValue(final String field, final String value) throws SjdblException {
        log.trace("Setting field [{}] to [{}]", field, value);
        try {
            siebelBusComp.setFieldValue(field, value);
        } catch (SiebelException e) {
            throw SjdblException.create(e);
        }
    }

    public boolean writeRecord() throws SjdblException {
        log.trace("Writing record");
        try {
            return siebelBusComp.writeRecord();
        } catch (SiebelException e) {
            throw SjdblException.create(e);
        }
    }

    public void release() {
        log.trace("Releasing Business Component {}", siebelBusComp.name());
        siebelBusComp.release();
    }

    public SiebelBusComp getSiebelBusComp() {
        log.warn("Working with SiebelBusComp is not recommended!");
        return siebelBusComp;
    }

    public enum ViewMode {
        MODE_SALES_REP(0),
        MODE_MANAGER(1),
        MODE_PERSONAL(2),
        MODE_ALL(3),
        MODE_NONE(4);

        int code;

        ViewMode(int code) {
            this.code = code;
        }
    }
}