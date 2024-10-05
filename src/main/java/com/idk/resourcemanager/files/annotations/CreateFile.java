package com.idk.resourcemanager.files.annotations;

import com.idk.resourcemanager.files.utility.Condition;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface CreateFile {

    Condition condition() default Condition.IMMEDIATELY;
    String method() default "";
    long delay() default 0;
    boolean overwrite() default true;
    int retryAttempts() default 3;
    long retryInterval() default 100;

}
