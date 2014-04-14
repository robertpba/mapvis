package plainmapper.metadata.object;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.List;

public class MdProperty extends MdMember {
    public enum AccessType {
        Field, Property, Constructor
    }
    public AccessType accessType;
    public Field field;
    public boolean required;
    public Annotation[] annotations;
}
