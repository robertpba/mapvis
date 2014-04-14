package plainmapper.metadata;

import plainmapper.annotation.MappingAnnotation;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class AnnotationProvider {

    public static AnnotationProvider getInstance() {
        return instance;
    }

    public static AnnotationProvider instance = new AnnotationProvider();


    public List<Annotation> getFieldMappingAnnotations(Field field) {
        return Stream.of(field.getAnnotations())
                .filter(a -> isMappingAnnotationType(a.annotationType()))
                .collect(Collectors.toList());
    }

    public List<Annotation> getClassMappingAnnotations(Class type) {
        return Stream.of(type.getAnnotations())
                .filter(a -> isMappingAnnotationType(a.annotationType()))
                .collect(Collectors.toList());
    }

    public boolean isMappingAnnotationType(Class type) {
        return Stream.of(type.getAnnotations())
                .anyMatch(a -> a.annotationType() == MappingAnnotation.class);
    }
}
