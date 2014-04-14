package plainmapper.annotation;

import java.lang.annotation.*;
import java.util.Objects;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.ANNOTATION_TYPE})
public @interface ConverterAnnotation {
}
