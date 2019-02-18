package ru.sadv1r.sjdbl.entity.annotation;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Inherited
public @interface BusinessComponent {
    String value() default "";
    boolean blankQueryAllowed() default false;
}
