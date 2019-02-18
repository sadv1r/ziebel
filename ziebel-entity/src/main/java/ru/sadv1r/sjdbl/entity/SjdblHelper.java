package ru.sadv1r.sjdbl.entity;

import ru.sadv1r.sjdbl.entity.annotation.*;

import java.beans.Transient;
import java.lang.reflect.Field;

/**
 * @author sadv1r
 */
public class SjdblHelper {
    private SjdblHelper() {
    }

    static boolean isTransientField(final Field field) {
        return field.getAnnotation(Transient.class) != null;
    }

    static boolean isMvgField(final Field field) {
        return field.getAnnotation(MvgField.class) != null;
    }

    static boolean isChildBusinessCompField(final Field field) {
        return field.getAnnotation(ChildBusCompField.class) != null;
    }

    static boolean isKeyField(final Field field) {
        return field.getAnnotation(Key.class) != null;
    }

    static boolean isReadOnlyField(final Field field) {
        return field.getAnnotation(ReadOnly.class) != null;
    }

    public static String getFieldName(final Field field) {
        field.setAccessible(true);

        final String siebelFieldName;
        final SiebelField siebelFieldAnnotation = field.getAnnotation(SiebelField.class);

        if (siebelFieldAnnotation == null) {
            siebelFieldName = convertCamelCaseToSpaces(field.getName());
        } else {
            siebelFieldName = siebelFieldAnnotation.value();
        }

        return siebelFieldName;
    }

    static String convertCamelCaseToSpaces(final String camelCase) {
        if (camelCase.isEmpty()) return camelCase;
        final StringBuffer spaces = new StringBuffer();

        for (int i = 0; i < camelCase.length(); i++) {
            char c = camelCase.charAt(i);
            if (Character.isUpperCase(c) || Character.isDigit(c)) spaces.append(" ");
            spaces.append(c);
        }
        spaces.setCharAt(0, Character.toUpperCase(spaces.charAt(0))); //need to make first letter capital
        return spaces.toString();
    }
}