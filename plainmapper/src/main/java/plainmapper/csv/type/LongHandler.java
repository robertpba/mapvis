package plainmapper.csv.type;

public class LongHandler implements TypeHandler {
    @Override
    public boolean canGet(Class type) {
        return type == Long.class || type == long.class;
    }

    @Override
    public Object get(Class type, String string) {
        if (! canGet(type)) return null;

        return (Long) Long.parseLong(string);
    }
}

