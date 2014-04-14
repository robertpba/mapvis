package plainmapper.rdb.type;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;

public class IntegerHandler extends BaseTypeHandler implements TypeHandler {
    @Override
    public boolean canGet(Class type) {
        return type == Integer.class || type == int.class;
    }

    @Override
    public Object get(Class type, ResultSet rs, int index) throws SQLException {
        if (!canGet(type)) return null;
        Object object = rs.getObject(index);

        if (object instanceof Integer)
            return (Integer) object;
        else if (object instanceof Short)
            return (Integer) object;
        else if (object instanceof Long)
            return (Integer) object;
        else if (object instanceof Float)
            return (Integer) object;
        else if (object instanceof Double)
            return (Integer) object;
        else if (object instanceof Boolean)
            return (Integer) object;
        else if (object instanceof BigDecimal)
            return (Integer) object;

        try {
            if (object instanceof String)
                return (Integer) Integer.parseInt((String) object);

            else if (object instanceof byte[])
                return (Integer) Integer.parseInt(new String((byte[]) object, "utf-8"));
        } catch (UnsupportedEncodingException e) {
        }
        return null;
    }
}

