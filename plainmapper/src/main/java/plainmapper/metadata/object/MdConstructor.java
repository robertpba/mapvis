package plainmapper.metadata.object;

import java.lang.reflect.Constructor;
import java.util.List;

public class MdConstructor extends MdMember {
    public List<MdFunctionParameter> parameters;
    public Constructor constructor;
}
