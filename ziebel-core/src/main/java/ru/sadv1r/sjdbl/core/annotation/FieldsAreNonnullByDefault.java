package ru.sadv1r.sjdbl.core.annotation;

import javax.annotation.Nonnull;
import javax.annotation.meta.TypeQualifierDefault;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Documented
@Nonnull
@TypeQualifierDefault(ElementType.FIELD)  // <-- use METHOD for return values
@Retention(RetentionPolicy.RUNTIME)
public @interface FieldsAreNonnullByDefault {
}