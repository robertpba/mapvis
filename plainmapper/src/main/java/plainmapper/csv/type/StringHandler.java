package plainmapper.csv.type;

public class StringHandler implements TypeHandler {
    @Override
    public boolean canGet(Class type) {
        return type == String.class;
    }

    @Override
    public Object get(Class type, String string){

        return string;

        //return null;
    }
}
