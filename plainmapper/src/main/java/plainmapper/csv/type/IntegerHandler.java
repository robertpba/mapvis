package plainmapper.csv.type;

public class IntegerHandler implements TypeHandler {
    @Override
    public boolean canGet(Class type) {
        return type == Integer.class  || type == int.class;
    }

    @Override
    public Object get(Class type, String string) {
        if (! canGet(type)) return null;

        return (Integer) Integer.parseInt(string);
    }
}