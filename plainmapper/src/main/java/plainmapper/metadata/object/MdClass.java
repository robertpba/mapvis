package plainmapper.metadata.object;

public class MdClass<T> {
    public Class<T> oType;
    public MdConstructor[]        constructors;
    public MdProperty[]           fields;
}
