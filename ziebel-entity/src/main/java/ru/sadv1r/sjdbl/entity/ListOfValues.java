package ru.sadv1r.sjdbl.entity;

import lombok.*;
import lombok.experimental.NonFinal;

import javax.annotation.Nullable;

/**
 * @author sadv1r
 */
@Value
@Setter(AccessLevel.PACKAGE)
@AllArgsConstructor(access = AccessLevel.PACKAGE)
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ListOfValues implements IListOfValues {
    @NonFinal
    @Nullable
    String type = null;
    @NonFinal
    @Nullable
    String name = null;
    @NonFinal
    @Nullable
    String value = null;
    @NonFinal
    boolean active = false;

    public static ListOfValues byName(final String name) {
        final ListOfValues listOfValues = new ListOfValues();
        listOfValues.name = name;
        return listOfValues;
    }

    public static ListOfValues byValue(final String value) {
        final ListOfValues listOfValues = new ListOfValues();
        listOfValues.value = value;
        return listOfValues;
    }
}