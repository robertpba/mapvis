package plainmapper.rdb.type;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;

public class LongHandler extends BaseTypeHandler implements TypeHandler {
    @Override
    public boolean canGet(Class type) {
        return type == Long.class || type == long.class;
    }

    @Override
    public Object get(Class type, ResultSet rs, int index) throws SQLException {
        if (!canGet(type)) return null;
        Object object = rs.getObject(index);

        if (object instanceof Integer)
            return (Long) object;
        else if (object instanceof Short)
            return (Long) object;
        else if (object instanceof Long)
            return (Long) object;
        else if (object instanceof Float)
            return (Long) object;
        else if (object instanceof Double)
            return object;
        else if (object instanceof Boolean)
            return (Long) object;
        else if (object instanceof BigDecimal)
            return (Long) object;
        try {
            if (object instanceof String)
                return (Long) Long.parseLong((String) object);

            else if (object instanceof byte[])
                return (Long) Long.parseLong(new String( (byte[])object, "utf-8"));
        } catch (UnsupportedEncodingException e) {
        }
        return null;
    }
}

