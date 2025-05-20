package com.inertiaclient.base.event;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface EventTarget {

    int priority() default 1;

}
