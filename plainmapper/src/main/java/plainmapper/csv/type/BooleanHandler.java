package plainmapper.csv.type;

public class BooleanHandler implements TypeHandler {
    @Override
    public boolean canGet(Class type) {
        return type == Boolean.class || type == boolean.class;
    }

    @Override
    public Object get(Class type, String string) {
        if (! canGet(type)) return null;

        return  Boolean.parseBoolean(string);
    }
}

