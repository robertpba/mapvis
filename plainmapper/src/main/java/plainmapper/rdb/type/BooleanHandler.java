package plainmapper.rdb.type;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;

public class BooleanHandler extends BaseTypeHandler implements TypeHandler {
    @Override
    public boolean canGet(Class type) {
        return type == Boolean.class || type == boolean.class;
    }

    @Override
    public Object get(Class type, ResultSet rs, int index) throws SQLException {
        if (!canGet(type)) return null;
        Object object = rs.getObject(index);

        if (object instanceof Integer)
            return (Boolean) object;
        else if (object instanceof Short)
            return (Short) object;
        else if (object instanceof Long)
            return (Boolean) object;
        else if (object instanceof Float)
            return (Boolean) object;
        else if (object instanceof Double)
            return (Boolean) object;
        else if (object instanceof Boolean)
            return (Boolean) object;
        else if (object instanceof BigDecimal)
            return (Boolean) object;

        try {
            if (object instanceof String)
                return (Boolean) Boolean.parseBoolean((String) object);

            else if (object instanceof byte[])
                return (Boolean) Boolean.parseBoolean(new String( (byte[])object, "utf-8"));
        } catch (UnsupportedEncodingException e) {
        }
        return null;
    }
}

