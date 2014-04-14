package plainmapper.csv.type;

public class ShortHandler implements TypeHandler {
    @Override
    public boolean canGet(Class type) {
        return type == Short.class || type == short.class;
    }

    @Override
    public Object get(Class type, String string) {
        if (! canGet(type)) return null;

        return (Short) Short.parseShort(string);
    }
}

