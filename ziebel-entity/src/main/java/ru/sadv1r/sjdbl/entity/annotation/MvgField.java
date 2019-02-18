package ru.sadv1r.sjdbl.entity.annotation;

import javax.persistence.CascadeType;
import javax.persistence.FetchType;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface MvgField {
    String value();

    Class clazz();

    /**
     * (Optional) Whether the association should be lazily
     * loaded or must be eagerly fetched. The {@link FetchType#EAGER EAGER}
     * strategy is a requirement on the persistence provider runtime that
     * the associated entity must be eagerly fetched. The {@link FetchType#LAZY
     * LAZY} strategy is a hint to the persistence provider runtime.
     */
    FetchType fetch() default FetchType.LAZY;

    /**
     * (Optional) The operations that must be cascaded to
     * the target of the association.
     * <p>
     * <p> By default no operations are cascaded.
     */
    CascadeType[] cascade() default {};
}
