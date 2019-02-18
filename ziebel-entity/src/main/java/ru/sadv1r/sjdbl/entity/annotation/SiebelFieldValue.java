package ru.sadv1r.sjdbl.entity.annotation;

import java.lang.annotation.*;

/**
 * Indicates that the annotated field should be used to map Siebel field values to the corresponding java enum.
 * This annotation must only be present on enum fields that are not static. The annotated field must be of type
 * {@link String}.
 *
 * For example:
 * <code>
 * enum Gender {
 *   Male("M"),
 *   Female("F");
 *
 *   {@literal @}SiebelFieldValue
 *   String code;
 *
 *   Gender(String code) {
 *     this.code = code;
 *   }
 * }
 * </code>
 * In this case, Siebel will store "M" and "F", but java code can always
 * use the typesafe Gender.Male and Gender.Female.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@Documented
public @interface SiebelFieldValue {
}
