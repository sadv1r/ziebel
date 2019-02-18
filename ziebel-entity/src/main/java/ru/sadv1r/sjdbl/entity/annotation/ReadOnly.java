package ru.sadv1r.sjdbl.entity.annotation;

import java.lang.annotation.*;


@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@Inherited
public @interface ReadOnly {
	
}
