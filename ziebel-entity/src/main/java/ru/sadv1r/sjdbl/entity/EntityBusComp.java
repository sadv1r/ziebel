package ru.sadv1r.sjdbl.entity;

import com.siebel.data.SiebelException;
import lombok.extern.slf4j.Slf4j;
import ru.sadv1r.sjdbl.core.BusComp;
import ru.sadv1r.sjdbl.core.BusObj;
import ru.sadv1r.sjdbl.core.exception.SjdblException;
import ru.sadv1r.sjdbl.entity.annotation.BusinessComponent;
import ru.sadv1r.sjdbl.entity.annotation.LOV;

import javax.annotation.Nonnull;
import javax.persistence.FetchType;
import java.lang.reflect.Field;
import java.util.List;

/**
 * @author sadv1r
 */
@Slf4j
public class EntityBusComp extends BusComp {
    public EntityBusComp(final BusComp busComp) {
        this(busComp.getBusObj(), busComp);
    }

    public EntityBusComp(final BusObj busObj, final BusComp busComp) {
        super(busObj, busComp);
    }

    /**
     * Prepare component for search:
     * - activate all needed component fields
     * - set viewMode to {@link ru.sadv1r.sjdbl.core.BusComp.ViewMode#MODE_ALL} (if needed)
     * - clear search specs
     * - set search specs with data from given object
     *
     * @param obj         the object that determines the query data
     * @param forUpdate   (for distinguishing b/w plain select {@literal &} select for an update)
     * @param setViewMode true, if view mode {@link ru.sadv1r.sjdbl.core.BusComp.ViewMode#MODE_ALL} needed
     * @throws SjdblException
     */
    public void prepareForQuery(final Object obj, final boolean forUpdate, final boolean setViewMode) throws SjdblException {
        activateFields(obj);

        if (setViewMode)
            setViewMode(3);

        clearToQuery();

        setSearchSpecs(obj, forUpdate);
    }


    void activateFields(final Class obj) throws SjdblException {
        final List<Field> fields = SiebelHelper.getAllDeclaredClassFields(obj);
        for (Field field : fields) {
            if (!SjdblHelper.isTransientField(field) && !SjdblHelper.isMvgField(field) && !SjdblHelper.isChildBusinessCompField(field)) {
                String fieldName = SjdblHelper.getFieldName(field);
                activateField(fieldName);
            }
        }
    }

    void activateFields(final Object obj) throws SjdblException {
        final List<Field> fields = SiebelHelper.getAllDeclaredInstanceFields(obj);
        for (Field field : fields) {
            if (!SjdblHelper.isTransientField(field) && !SjdblHelper.isMvgField(field) && !SjdblHelper.isChildBusinessCompField(field)) {
                String fieldName = SjdblHelper.getFieldName(field);
                activateField(fieldName);
            }
        }
    }

    /**
     * Copy all values from Object to Business Component to prepare for search query
     *
     * @param obj        the object that determines the query data
     * @param keyMatters for distinguishing b/w plain select & select for an update
     * @throws IllegalArgumentException if all nulls are searched for (this would return all rows in all of Siebel and we don't want that!)
     */
    void setSearchSpecs(final Object obj, final boolean keyMatters) throws SjdblException {
        boolean emptyQuery = true;

        List<Field> fields = SiebelHelper.getAllDeclaredInstanceFields(obj);
        for (Field field : fields) {
            field.setAccessible(true);
            if (!SjdblHelper.isTransientField(field) && !SjdblHelper.isMvgField(field) && !SjdblHelper.isChildBusinessCompField(field) && !(keyMatters && !SjdblHelper.isKeyField(field))) {
                try {
                    if (field.get(obj) != null) {
                        Object fieldValueObject = SiebelHelper.getFieldValueFromAccessibleField(obj, field);
                        String fieldName = SjdblHelper.getFieldName(field);
                        @Nonnull String fieldValue = SiebelHelper.convertFieldValueToSiebelValue(field, fieldValueObject);
                        setSearchSpec(fieldName, fieldValue);
                        emptyQuery = false;
                    }
                } catch (IllegalAccessException e) {
                    throw new AssertionError("Should never be thrown! Field " + field + " was forced to be accessible");
                }
            }
        }

        if (emptyQuery && (obj.getClass().getAnnotation(BusinessComponent.class) == null || !obj.getClass().getAnnotation(BusinessComponent.class).blankQueryAllowed())) {
            String errorMsg = "Blank Query (all nulls) found in SiebelDataBean for Object: " + obj.getClass().getName();
            throw new IllegalArgumentException(errorMsg);
        }
    }
}
