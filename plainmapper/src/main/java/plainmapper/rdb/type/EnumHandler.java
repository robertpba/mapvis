package plainmapper.rdb.type;

import java.sql.ResultSet;
import java.sql.SQLException;

public class EnumHandler extends BaseTypeHandler implements TypeHandler {
    @Override
    public boolean canGet(Class type) {
        return Enum.class.isAssignableFrom(type);
    }

    @Override
    public Object get(Class type, ResultSet rs, int index) throws SQLException {
        if (!canGet(type)) return null;
        Object object = rs.getObject(index);

        for(Enum enumValue : getEnumConstants(type)) {
            if (enumValue.name().equalsIgnoreCase((String)object)) {
                return enumValue;
            }
        }

        return null;
    }

    private <T extends Enum> T[] getEnumConstants(Class<T> type) {
        return type.getEnumConstants();
    }

}

