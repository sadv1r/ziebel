package ru.sadv1r.sjdbl.entity;

import lombok.extern.slf4j.Slf4j;
import org.assertj.core.util.Strings;
import ru.sadv1r.sjdbl.entity.annotation.SiebelField;
import ru.sadv1r.sjdbl.core.exception.SjdblException;
import ru.sadv1r.sjdbl.entity.annotation.ChildBusCompField;
import ru.sadv1r.sjdbl.entity.annotation.Key;
import ru.sadv1r.sjdbl.entity.annotation.MvgField;
import ru.sadv1r.sjdbl.entity.annotation.SiebelFieldValue;

import javax.annotation.Nullable;
import java.beans.Transient;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.EnumSet;
import java.util.List;

import static javax.xml.bind.DatatypeConverter.parseDateTime;

/**
 * @author sadv1r
 */
@Slf4j
public class SiebelHelper {
    private static final String SIEBEL_DATE_PATTERN = "MM/dd/yyyy";
    private static final String SIEBEL_DATE_TIME_PATTERN = "MM/dd/yyyy HH:mm:ss";

    /**
     * prints and parses dates using the format that Siebel uses for dates without a time component: {@code MM/dd/yyyy}
     */
    public static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern(SIEBEL_DATE_PATTERN);

    /**
     * prints and parses dates using the format that Siebel uses for dates with a time component: {@code MM/dd/yyyy HH:mm:ss}
     */
    public static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern(SIEBEL_DATE_TIME_PATTERN);

    private SiebelHelper() {
    }

    /**
     * Get all declared class and all super classes fields
     *
     * @param clazz1
     * @return {@link List} of all declared class and all super classes fields
     */
    static List<Field> getAllDeclaredClassFields(final Class clazz1) {
        log.debug("Getting all declared fields for Class {}", clazz1.getSimpleName());
        final ArrayList<Field> fieldList = new ArrayList<>();
        Field[] fields;

        Class<?> clazz = clazz1;

        while (!clazz.equals(Object.class)) {
            fields = clazz.getDeclaredFields();
            for (Field field : fields) {
                if (!Modifier.isStatic(field.getModifiers())) {
                    fieldList.add(field);
                }
            }
            clazz = clazz.getSuperclass();
        }

        return fieldList;
    }

    /**
     * Get all declared instance class and super class fields
     *
     * @param obj
     * @return {@link List} of all declared instance class and super class fields
     */
    static List<Field> getAllDeclaredInstanceFields(final Object obj) {
        final ArrayList<Field> fieldList = new ArrayList<>();
        Field[] fields;
        Class<?> clazz = obj.getClass();

        while (!clazz.equals(Object.class)) {
            fields = clazz.getDeclaredFields();
            for (Field field : fields) {
                if (!Modifier.isStatic(field.getModifiers())) {
                    fieldList.add(field);
                }
            }
            clazz = clazz.getSuperclass();
        }

        return fieldList;
    }

    @Nullable
    static Object getFieldValueFromAccessibleField(final Object obj, final Field field) {
        Object fieldValueObject;
        try {
            fieldValueObject = field.get(obj);
        } catch (IllegalAccessException e) {
            throw new AssertionError("Should never be thrown! Field " + field + " was forced to be accessible");
        }
        return fieldValueObject;
    }

    static String convertFieldValueToSiebelValue(final Field field, final Object fieldValueObject) {
        final Class<?> fieldType = field.getType();

        if (fieldType.equals(Date.class)) {
            Date fieldValueAsDate = (Date) fieldValueObject;
            return DATE_TIME_FORMATTER.format(LocalDateTime.ofInstant(fieldValueAsDate.toInstant(), ZoneId.systemDefault()));
        } else if (fieldType.equals(LocalDateTime.class)) {
            LocalDateTime fieldValueAsDateTime = (LocalDateTime) fieldValueObject;
            return DATE_TIME_FORMATTER.format(fieldValueAsDateTime);
        } else if (fieldType.equals(LocalDate.class)) {
            LocalDate fieldValueAsLocalDate = (LocalDate) fieldValueObject;
            return DATE_FORMATTER.format(fieldValueAsLocalDate);
        } else if (Enum.class.isAssignableFrom(fieldType)) {
            return convertEnumToSiebelCode((Enum<?>) fieldValueObject, field);
        } else if (fieldType.equals(Boolean.class)) {
            Boolean fieldValueAsBoolean = (Boolean) fieldValueObject;
            return fieldValueAsBoolean ? "Y" : "N";
        } else {
            return fieldValueObject.toString();
        }
    }

    static String convertEnumToSiebelCode(final Enum<?> enumValue, final Field columnField) {
        Field[] fields = enumValue.getClass().getDeclaredFields();
        for (Field field : fields) {

            if (field.isAnnotationPresent(SiebelFieldValue.class)) {
                if (field.getType() != String.class) {
                    throw new IllegalArgumentException(String.format(
                            "enum %s's @%s field %s is not of type String, but of type %s",
                            enumValue,
                            SiebelFieldValue.class.getSimpleName(),
                            field,
                            field.getType()
                    ));
                }

                field.setAccessible(true);
                return (String) getFieldValueFromAccessibleField(enumValue, field);
            }
        }
        throw new IllegalArgumentException(String.format(
                "enum %s does not have a field annotated @%s, as required if this enum field (%s) is to be mapped to a Siebel column",
                enumValue,
                SiebelFieldValue.class.getSimpleName(),
                columnField
        ));
    }

    @Nullable
    static Object convertSiebelValueToFieldValue(final Class<?> fieldType, final String fieldValue) throws SjdblException {
        log.trace("Converting '{}' of {}...", fieldValue, fieldType);
        if (fieldType.equals(String.class)) {
            return fieldValue; //some existing code is depending on blank strings instead of nulls
        } else {
            if (fieldValue == null) {
                return null;
            } else if (fieldType.equals(Boolean.class)) {
                if (fieldValue.isEmpty()) return null;
                if ("Y".equals(fieldValue)) return Boolean.TRUE;
                if ("N".equals(fieldValue)) return Boolean.FALSE;
                throw new IllegalArgumentException("can't convert to Boolean: " + fieldValue);
            } else if (fieldType.equals(Integer.class)) {
                return new Integer(fieldValue);
            } else if (fieldType.equals(Long.class)) {
                return new Long(fieldValue);
            } else if (fieldType.equals(Float.class)) {
                return new Float(fieldValue);
            } else if (fieldType.equals(Date.class)) {
                return parseDateTime(fieldValue).getTime();
            } else if (fieldType.equals(LocalDateTime.class)) {
                return convertToLocalDateTime(fieldValue);
            } else if (fieldType.equals(LocalDate.class)) {
                return convertToLocalDate(fieldValue);
            } else if (Enum.class.isAssignableFrom(fieldType)/* && !Strings.isNullOrEmpty(fieldValue)*/) {
                @SuppressWarnings("rawtypes")  //sadly, I think using asSubclass() requires us to use a raw type
                        Class<? extends Enum> enumType = fieldType.asSubclass(Enum.class);
                @SuppressWarnings("unchecked") //I can't find a way to do this w/out compiler warnings
                        Enum<?> enumValue = convertSiebelCodeToEnum(enumType, fieldValue);
                return enumValue;
            } else {
                throw new IllegalArgumentException("Can't handle field of type: " + fieldType);
            }
        }
    }

    @Nullable
    private static LocalDate convertToLocalDate(final String fieldValue) {
        if (fieldValue.isEmpty()) return null;
        return LocalDate.parse(fieldValue, DATE_FORMATTER);
    }

    @Nullable
    private static LocalDateTime convertToLocalDateTime(final String fieldValue) {
        if (fieldValue.isEmpty()) return null;
        return LocalDateTime.parse(fieldValue, DATE_TIME_FORMATTER);
    }

    private static <T extends Enum<T>> T convertSiebelCodeToEnum(Class<T> enumType, String siebelFieldValue) throws SjdblException {
        EnumSet<T> possibleEnumValues = EnumSet.allOf(enumType);

        Field mappingField = getSiebelValueField(enumType);
        mappingField.setAccessible(true);

        for (T possibleEnum : possibleEnumValues) {
            String code = (String) getFieldValueFromAccessibleField(possibleEnum, mappingField);
            if (code == null)
                throw new IllegalArgumentException(String.format("siebel value field %s contains null value", mappingField));
            if (code.equals(siebelFieldValue))
                return possibleEnum;
        }
        throw new SjdblException(String.format(
                "The value returned by Siebel (%s) does not correspond to any enum of type %s",
                siebelFieldValue,
                enumType
        ));
    }

    static void setFieldValueToAccessibleField(final Object obj, final Field field, final Object convertedValue) {
        try {
            field.set(obj, convertedValue);
        } catch (IllegalAccessException e) {
            throw new AssertionError("Should never be thrown! Field " + field + " was forced to be accessible");
        }
    }

    static String determineSiebelFieldNameForChildBusinessCompField(Object parentObj, String fieldName) {
        String siebelFieldName = fieldName;
        final Field field = SiebelHelper.getField(parentObj.getClass(), fieldName);
        if (field == null) return fieldName;
        final ChildBusCompField fieldMetadata = field.getAnnotation(ChildBusCompField.class);
        if (fieldMetadata != null && !Strings.isNullOrEmpty(fieldMetadata.value()))
            siebelFieldName = fieldMetadata.value();
        return siebelFieldName;
    }

    static String determineSiebelFieldNameForMvgField(Object parentObj, String fieldName) {
        String siebelFieldName = fieldName;
        Field field = SiebelHelper.getField(parentObj.getClass(), fieldName);
        if (field == null) return fieldName;
        MvgField fieldMetadata = field.getAnnotation(MvgField.class);
        if (fieldMetadata != null && !Strings.isNullOrEmpty(fieldMetadata.value()))
            siebelFieldName = fieldMetadata.value();

        log.debug("Siebel MVG field name for {}#{} found: '{}'", parentObj.getClass().getSimpleName(), fieldName, siebelFieldName);
        return siebelFieldName;
    }

    public static Field getField(Class clazz, final String fieldName) {
        while (!clazz.getName().equals("java.lang.Object")) {
            for (Field f : clazz.getDeclaredFields()) {
                if (f.getName().equals(fieldName)) return f;
            }
            clazz = clazz.getSuperclass();
        }
        return null;
    }

    private static Field getSiebelValueField(Class<?> enumType) {
        Field[] fields = enumType.getDeclaredFields();
        for (Field field : fields) {
            if (field.isAnnotationPresent(SiebelFieldValue.class))
                return field;
        }
        throw new IllegalArgumentException(String.format("enum type %s does not have a field", enumType));
    }
}
