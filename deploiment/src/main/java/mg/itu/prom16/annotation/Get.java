package mg.itu.prom16.annotation;

import java.lang.annotation.*;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME) 
public @interface Get {
    String url() default "";
}

