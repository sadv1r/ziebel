package ru.sadv1r.sjdbl.entity;

import com.siebel.data.SiebelException;
import lombok.extern.slf4j.Slf4j;
import ru.sadv1r.sjdbl.core.BusComp;
import ru.sadv1r.sjdbl.core.BusObj;
import ru.sadv1r.sjdbl.core.SiebelDataBeanWrapper;
import ru.sadv1r.sjdbl.core.ZiebelDataBean;
import ru.sadv1r.sjdbl.core.exception.MoreThanOneRecordFoundExceptions;
import ru.sadv1r.sjdbl.core.exception.RecordNotFoundException;
import ru.sadv1r.sjdbl.core.exception.SjdblException;
import ru.sadv1r.sjdbl.entity.annotation.*;

import javax.persistence.FetchType;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.*;

@Slf4j
public class Session implements ISession {
    private final SessionFactory sessionFactory;
    private final ZiebelDataBean ziebelDataBean;
    private final UUID sessionIdentifier;

    Session(final SessionFactory sessionFactory, final ZiebelDataBean connection) {
        this.sessionFactory = sessionFactory;
        this.ziebelDataBean = connection;

        sessionIdentifier = UUID.randomUUID();

        log.info( "Opened session with  by.. [{}]", getSessionIdentifier());
    }

    public SiebelDataBeanWrapper connection() {
        return this.ziebelDataBean;
    }

    @Override
    public SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    @Override
    public UUID getSessionIdentifier() {
        return sessionIdentifier;
    }

    @Override
    public String getLoginId() {
        return ziebelDataBean.getLoginId();
    }

    @Override
    public String getLoginName() {
        return ziebelDataBean.getLoginName();
    }

    @Override
    public String getPositionId() {
        return ziebelDataBean.getPositionId();
    }

    @Override
    public String getPositionName() {
        return ziebelDataBean.getPositionName();
    }

    @Override
    public boolean isOpen() {
        return ziebelDataBean.isLoggedIn();
    } //TODO Or create property in Session.class?

    @Override
    public void close() throws SjdblException {
        log.info( "Closing session [{}]", getSessionIdentifier() );

        ziebelDataBean.logoff();
        sessionFactory.markClosed(getSessionIdentifier());

        //TODO Clean up!
    }

    @Override
    public String toString() {
        return "Session{" +
                "sessionFactory=" + sessionFactory +
                ", sessionIdentifier=" + sessionIdentifier +
                ", loginName=" + getLoginName() +
                '}';
    }

    @Override
    public <T> Optional<T> get(final Class<T> clazz, final String id) throws SjdblException {
        EntityBusComp entityBusComp = null;
        try {
            entityBusComp = loadCompAndObj(clazz);

            entityBusComp.activateFields(clazz);
            entityBusComp.setViewMode(BusComp.ViewMode.MODE_ALL);
            entityBusComp.clearToQuery();
            entityBusComp.setSearchExpr(String.format("[Id] = '%s'", id));
            entityBusComp.executeQuery(false);

            if (entityBusComp.firstRecord()) {
                T tempObj = instantiate(clazz);

                copySearchResultsToEntityObject(entityBusComp, tempObj);

                cascadeLoadRelationships(entityBusComp, Collections.singletonList(tempObj));

                return Optional.of(tempObj);
            } else
                return Optional.empty();
        } finally {
            if (entityBusComp != null)
                entityBusComp.release();
        }
    }

    @Override
    public <T> T load(final Class<T> clazz, final String id) throws SjdblException {
        EntityBusComp entityBusComp = null;
        try {
            entityBusComp = loadCompAndObj(clazz);

            entityBusComp.activateFields(clazz);
            entityBusComp.setViewMode(BusComp.ViewMode.MODE_ALL);
            entityBusComp.clearToQuery();
            entityBusComp.setSearchExpr(String.format("[Id] = '%s'", id));
            entityBusComp.executeQuery(false);

            if (entityBusComp.firstRecord()) {
                T tempObj = instantiate(clazz);

                copySearchResultsToEntityObject(entityBusComp, tempObj);

                cascadeLoadRelationships(entityBusComp, Collections.singletonList(tempObj));

                return tempObj;
            } else
                throw new RecordNotFoundException();
        } finally {
            if (entityBusComp != null)
                entityBusComp.release();
        }
    }

    @Override
    public boolean get(final Object obj) throws SjdblException {
        EntityBusComp entityBusComp = null;
        try {
            entityBusComp = loadCompAndObj(obj);

            entityBusComp.prepareForQuery(obj, false, true);
            entityBusComp.executeQuery(false);

            if (entityBusComp.firstRecord()) {
                if (entityBusComp.nextRecord())
                    throw new MoreThanOneRecordFoundExceptions();
                entityBusComp.firstRecord();
                copySearchResultsToEntityObject(entityBusComp, obj);

                cascadeLoadRelationships(entityBusComp, Collections.singletonList(obj));

                return true;
            } else {
                log.debug("Object not found with search {}", obj);
                return false;
            }
        } finally {
            if (entityBusComp != null)
                entityBusComp.release();
        }
    }

    @Override
    public <T> List<T> list(final T obj) throws SjdblException {
        return new ArrayList<>(siebelCollectionSelect(obj));
    }

    private <T> Collection<T> siebelCollectionSelect(final T obj) throws SjdblException {
        EntityBusComp mainBusComp = null;
        try {
            mainBusComp = loadCompAndObj(obj);

            return siebelCollectionSelect(mainBusComp, obj);
        } catch (SjdblException e) {
            throw new SjdblException("Unable to perform list select on " + obj, e);
        } finally {
            if (mainBusComp != null)
                mainBusComp.release();
        }
    }

    private <T> Collection<T> siebelCollectionSelect(final EntityBusComp busComp, final T object) throws SjdblException {
        try {
            final Collection<T> collection = new ArrayList<>();

            busComp.prepareForQuery(object, false, true);
            busComp.executeQuery(false);

            if (busComp.firstRecord()) {
                @SuppressWarnings("unchecked")
                Class<T> objType = (Class<T>) object.getClass();
                do {
                    T tempObj = instantiate(objType);

                    copySearchResultsToEntityObject(busComp, tempObj);

                    cascadeLoadRelationships(busComp, Collections.singletonList(tempObj));

                    collection.add(tempObj);
                } while (busComp.nextRecord());
            }

            return collection;
        } catch (SiebelException e) {
            throw new SjdblException("Unable to perform select on " + object, e);
        }
    }

    private <T> void cascadeLoadRelationships(final EntityBusComp parentBusComp, final List<T> parentObjects) throws SjdblException {
        try {
            List<Field> fs = SiebelHelper.getAllDeclaredInstanceFields(parentObjects.get(0));
            for (Field f : fs) {
                f.setAccessible(true);

                ChildBusCompField childBusCompField = f.getAnnotation(ChildBusCompField.class);
                if (childBusCompField != null && childBusCompField.fetch().equals(FetchType.EAGER)) {
                    EntityBusComp childBusComp = new EntityBusComp(parentBusComp.getBusObj(), parentBusComp.getBusObj().getBusComp(SiebelHelper.determineSiebelFieldNameForChildBusinessCompField(parentObjects.get(0), f.getName())));
                    Collection<?> siebelSelection = siebelCollectionSelect(childBusComp, childBusCompField.clazz().newInstance());
                    for (Object parentObject : parentObjects)
                        f.set(parentObject, siebelSelection);
                    childBusComp.release();
                }

                MvgField fieldMetadata = f.getAnnotation(MvgField.class);
                if (fieldMetadata != null && fieldMetadata.fetch().equals(FetchType.EAGER)) {
                    log.trace("MVG field find and cascade load needed");
                    String siebelName = SiebelHelper.determineSiebelFieldNameForMvgField(parentObjects.get(0), f.getName());
                    Collection<Object> siebelSelection = siebelSelectMvg(parentBusComp, siebelName, fieldMetadata.clazz().newInstance());
                    for (Object parentObject : parentObjects) {
                        log.trace("Setting {} to {} on {}", f.getName(), siebelSelection, parentObject);
                        f.set(parentObject, siebelSelection);
                    }
                }
            }
        } catch (Exception e) {
            throw new SjdblException("Unable to perform select on " + parentObjects.get(0), e);
        }
    }

    /**
     * Iterate through the instances of mvgObject in the context of the
     * specified business component
     *
     * @param <T>
     * @param busComp
     * @param exampleChildObject
     * @return
     */
    private <T> Collection<T> siebelSelectMvg(BusComp busComp, String fieldName, T exampleChildObject) throws SjdblException {
        EntityBusComp tempBusComp = null;

        try {
            Collection<T> collection = new ArrayList<>();

            tempBusComp = new EntityBusComp(busComp.getMvgBusComp(fieldName));

            tempBusComp.prepareForQuery(exampleChildObject, false, true);

            tempBusComp.executeQuery2(true, true);

            if (tempBusComp.firstRecord()) {
                log.trace("tempBusComp found");
                @SuppressWarnings("unchecked")
                Class<T> objType = (Class<T>) exampleChildObject.getClass();
                do {
                    T tempObj = instantiate(objType);
                    copySearchResultsToEntityObject(tempBusComp, tempObj);
                    collection.add(tempObj);
                }
                while (tempBusComp.nextRecord());
            }

            return collection;

        } catch (SiebelException e) {
            throw new SjdblException("Unable to perform select on " + exampleChildObject, e);
        } finally {
            if (tempBusComp != null)
                tempBusComp.release();
        }
    }

    private <T> T instantiate(final Class<T> objType) {
        try {
            return objType.newInstance();
        } catch (InstantiationException e) {
            throw new IllegalArgumentException(String.format("Can't instantiate object of type %s; make sure it's not abstract or an interface", objType.getName()), e);
        } catch (IllegalAccessException e) {
            throw new IllegalArgumentException(String.format("Can't instantiate object of type %s; make sure it has a public no-argument constructor", objType.getName()), e);
        }
    }

    /**
     * Method takes data from a Siebel Business Component after query and moves
     * data back into the entity object.
     *
     * @param busComp
     * @param obj
     * @return
     */
    private void copySearchResultsToEntityObject(BusComp busComp, Object obj) throws SjdblException {
        List<Field> fields = SiebelHelper.getAllDeclaredInstanceFields(obj);
        for (Field field : fields) {
            field.setAccessible(true);
            if (!SjdblHelper.isTransientField(field) && !SjdblHelper.isMvgField(field) && !SjdblHelper.isChildBusinessCompField(field)) {
                String fieldName = SjdblHelper.getFieldName(field);
                Class<?> fieldType = field.getType();
                String fieldValue = busComp.getFieldValue(fieldName);

                Object convertedValue = null;
                LOV lovAnnotation = field.getAnnotation(LOV.class);
                if (lovAnnotation != null && lovAnnotation.fetch().equals(FetchType.EAGER)) {
                    log.trace("LOV field found and cascade load needed");

                    String lovType = lovAnnotation.value();
                    if (fieldValue.isEmpty()) {
                        convertedValue = new ListOfValues(lovType, "", "", false);
                    } else {
                        BusObj listOfValuesBusObj = ziebelDataBean.getBusObject("List Of Values");
                        BusComp listOfValuesBusComp = listOfValuesBusObj.getBusComp("List Of Values");
                        listOfValuesBusComp.activateField("Value");
                        listOfValuesBusComp.activateField("Type");
                        listOfValuesBusComp.activateField("Name");
//                        listOfValuesBusComp.setSearchSpec("Type", lovType);
//                        listOfValuesBusComp.setSearchSpec("Value", fieldValue);
                        listOfValuesBusComp.setSearchExpr(String.format("[Type] = \"%s\" AND [Value] = \"%s\"", lovType, fieldValue));
                        listOfValuesBusComp.executeQuery(true);

                        if (listOfValuesBusComp.firstRecord()) {
                            convertedValue = new ListOfValues(lovType, listOfValuesBusComp.getFieldValue("Name"), fieldValue, true);
                        }

                        listOfValuesBusComp.release();
                        listOfValuesBusObj.release();
                    }
                } else
                    convertedValue = SiebelHelper.convertSiebelValueToFieldValue(fieldType, fieldValue);

                SiebelHelper.setFieldValueToAccessibleField(obj, field, convertedValue);
            }
        }
        return;
    }

    private EntityBusComp loadCompAndObj(final Object obj) throws SjdblException {
        return loadCompAndObj(obj.getClass());
    }

    private EntityBusComp loadCompAndObj(final Class clazz) throws SjdblException {
        BusObj busObj = loadBusinessObject(clazz);

        return loadBusinessComponent(clazz, busObj);
    }

    private BusObj loadBusinessObject(final Class<?> type) throws SjdblException {
        return ziebelDataBean.getBusObject(getBusObjName(type));
    }

    private EntityBusComp loadBusinessComponent(final Class<?> type, final BusObj busObj) throws SjdblException {
        return new EntityBusComp(busObj.getBusComp(getBusCompName(type)));
    }

    private String getBusObjName(final Class<?> type) {
        log.trace("Getting Business Object name for class {}", type);
        final Optional<BusinessObject> businessObjectAnnotation = getBusinessObjectAnnotation(type);

        String busObjName;
        if (businessObjectAnnotation.isPresent()) {
            log.trace("BusinessObject annotation found");
            busObjName = businessObjectAnnotation.get().value();

            if (busObjName.isEmpty()) {
                log.trace("Business Object name is not specified in annotation, using class name");
                busObjName = type.getSimpleName();
            }
        } else {
            log.trace("BusinessObject annotation is not found, using class name");
            busObjName = type.getSimpleName();
        }

        log.debug("Business Object name for class {} is '{}'", type, busObjName);
        return busObjName;
    }

    private String getBusCompName(final Class<?> type) {
        log.trace("Getting Business Component name for class {}", type);
        final Optional<BusinessComponent> businessComponentAnnotation = getBusinessComponentAnnotation(type);

        String busCompName;
        if (businessComponentAnnotation.isPresent()) {
            log.trace("BusinessComponent annotation found");
            busCompName = businessComponentAnnotation.get().value();

            if (busCompName.isEmpty()) {
                log.trace("Business Component name is not specified in annotation, using class name");
                busCompName = type.getSimpleName();
            }
        } else {
            log.trace("BusinessComponent annotation is not found, using class name");
            busCompName = type.getSimpleName();
        }

        log.debug("Business Component name for class {} is {}", type, busCompName);
        return busCompName;
    }

    private Optional<BusinessObject> getBusinessObjectAnnotation(final Class<?> type) {
        return getAnnotationIncludingInterfaces(type, BusinessObject.class);
    }

    private Optional<BusinessComponent> getBusinessComponentAnnotation(final Class<?> type) {
        return getAnnotationIncludingInterfaces(type, BusinessComponent.class);
    }

    /**
     * Get {@link Annotation} instance from {@link Class} or it's interfaces
     *
     * @param type           {@link Class} to perform search on
     * @param annotationType annotation type class
     * @param <A>            annotation type
     * @return annotation
     */
    private <A extends Annotation> Optional<A> getAnnotationIncludingInterfaces(final Class<?> type, final Class<A> annotationType) {
        log.trace("Getting annotation {} on {}", annotationType.getSimpleName(), type);
        A annotation = type.getAnnotation(annotationType);

        if (annotation == null) {
            log.trace("Annotation {} is not found on {}. Searching on it's interfaces", annotationType.getSimpleName(), type);
            Class<?>[] classes = type.getInterfaces();
            for (int i = 0; (annotation == null && i < classes.length); i++)
                annotation = classes[i].getAnnotation(annotationType);
        }

        return Optional.ofNullable(annotation);
    }

    /**
     * Inserts a row into the corresponding Siebel business component, built from the given object.
     *
     * @param obj object the row to be inserted
     * @return the id assigned by Siebel for the newly inserted row
     */
    @Override
    public String save(final Object obj) throws SjdblException {
        EntityBusComp mainBusComp = null;
        try {
            mainBusComp = loadCompAndObj(obj);

            return siebelInsert(mainBusComp, obj);
        } catch (SjdblException e) {
            throw new SjdblException("Unable to perform insert on " + obj, e);
        } finally {
            if (mainBusComp != null)
                mainBusComp.release();
        }
    }

    private String siebelInsert(final EntityBusComp busComp, final Object obj) throws SjdblException {
        try {
            busComp.newRecord(false);
            setFieldsForInsert(busComp, obj);
            if (!busComp.writeRecord())
                throw new SjdblException("insert did not succeed");

            return busComp.getFieldValue("Id");
        } catch (SiebelException e) {
            throw new SjdblException("Unable to perform insert on " + obj, e);
        }
    }

    private void setFieldsForInsert(final EntityBusComp busComp, final Object obj) throws SjdblException {
        List<Field> fields = SiebelHelper.getAllDeclaredInstanceFields(obj);
        for (Field field : fields) {
            field.setAccessible(true);
            if (!SjdblHelper.isTransientField(field) && !SjdblHelper.isMvgField(field) && !SjdblHelper.isChildBusinessCompField(field) && !SjdblHelper.isReadOnlyField(field)) {
                String fieldName = SjdblHelper.getFieldName(field);
                Object fieldValueObject = SiebelHelper.getFieldValueFromAccessibleField(obj, field);

                if (fieldValueObject != null && !field.getType().equals(List.class)) {
                    String convertedValue;
                    LOV lovAnnotation = field.getAnnotation(LOV.class);
                    if (lovAnnotation != null) {
                        log.trace("LOV field found");

                        if (!(fieldValueObject instanceof ListOfValues))
                            throw new SjdblException(String.format("Field %s#%s annotated with @LOV but type is not ListOfValue", obj.getClass().getSimpleName(), fieldName));
                        final ListOfValues fieldValueObjectLov = (ListOfValues) fieldValueObject;

                        String lovType = lovAnnotation.value();
                        if (lovAnnotation.lookupOnSet()) {
                            if (fieldValueObjectLov.getName() != null)
                                fieldValueObjectLov.setValue(ziebelDataBean.lookupValue(lovType, fieldValueObjectLov.getName()));
                            else if (fieldValueObjectLov.getValue() != null)
                                fieldValueObjectLov.setName(ziebelDataBean.lookupName(lovType, fieldValueObjectLov.getValue()));
                            else throw new SjdblException("LOV %s#%s is empty");
                            fieldValueObjectLov.setActive(true);
                        }
                        convertedValue = fieldValueObjectLov.getValue();
                    } else
                        convertedValue = SiebelHelper.convertFieldValueToSiebelValue(field, fieldValueObject);
                    busComp.setFieldValue(fieldName, convertedValue);
                }
            }
        }
        return;
    }

    @Override
    public String siebelUpsert(Object object) {
        return null;
    }

    @Override
    public boolean siebelDelete(Object object) {
        return false;
    }
}