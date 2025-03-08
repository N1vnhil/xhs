package org.n1vnhil.framework.biz.operationlog.aspect;


import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface ApiOperationLog {

    String description() default "";

}
