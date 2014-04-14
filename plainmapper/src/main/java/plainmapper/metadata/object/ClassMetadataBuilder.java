package plainmapper.metadata.object;

import plainmapper.metadata.AnnotationProvider;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ClassMetadataBuilder {

    static final Map<Class, MdClass> cache = new HashMap<>();

    public static <T> MdClass<T> getClassMetaData(Class<T> classType)
    {
        @SuppressWarnings("unchecked")
        MdClass<T> metaData = cache.get(classType);
        if (metaData == null)
            cache.put(classType, metaData = createClassMetaData(classType));

        return metaData;
    }

    public static <T> MdClass<T> createClassMetaData(Class<T> classType) {
        MdClass<T> MdClass = new MdClass<>();
        MdClass.oType = classType;
        ArrayList<MdProperty> fields = new ArrayList<>();
        for (Field field : classType.getFields()) {
            int modifiers = field.getModifiers();
            if (Modifier.isPublic(modifiers)) {
                List<Annotation> fieldMappingAnnotations = AnnotationProvider.getInstance().getFieldMappingAnnotations(field);
                MdProperty mdProperty = new MdProperty();
                mdProperty.field = field;
                mdProperty.required = false;
                mdProperty.annotations = fieldMappingAnnotations.toArray(new Annotation[fieldMappingAnnotations.size()]);
                fields.add(mdProperty);
            }
        }
        MdClass.fields = fields.toArray(new MdProperty[fields.size()]);
        return MdClass;
    }
}
