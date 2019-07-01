package org.zalando.baigan.spring;

import org.springframework.context.annotation.Import;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(ConfigurationBeanRegistrar.class)
public @interface EnableBaigan {

    boolean enabled() default true;

    Class<?>[] basePackageClasses() default {};

}
