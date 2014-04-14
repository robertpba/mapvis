package plainmapper.csv.type;

public class EnumHandler implements TypeHandler {
    @Override
    public boolean canGet(Class type) {
        return type == Enum.class;
    }

    @Override
    public Object get(Class type, String string) {
        if (! canGet(type)) return null;

        for(Enum enumValue : getEnumConstants(type)) {
            if (enumValue.name().equalsIgnoreCase(string)) {
                return enumValue;
            }
        }

        return null;
    }

    private <T extends Enum> T[] getEnumConstants(Class<T> type) {
        return type.getEnumConstants();
    }

}

