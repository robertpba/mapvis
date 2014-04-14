package plainmapper.rdb.type;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ShortHandler extends BaseTypeHandler implements TypeHandler {
    @Override
    public boolean canGet(Class type) {
        return type == Short.class || type == short.class;
    }

    @Override
    public Object get(Class type, ResultSet rs, int index) throws SQLException {
        if (!canGet(type)) return null;
        Object object = rs.getObject(index);

        if (object instanceof Integer)
            return (Short) object;
        else if (object instanceof Short)
            return (Short) object;
        else if (object instanceof Long)
            return (Short) object;
        else if (object instanceof Float)
            return (Short) object;
        else if (object instanceof Double)
            return (Short)object;
        else if (object instanceof Boolean)
            return (Short) object;
        else if (object instanceof BigDecimal)
            return (Short) object;
        try {
            if (object instanceof String)
                return (Short) Short.parseShort((String) object);

            else if (object instanceof byte[])
                return (Short) Short.parseShort(new String((byte[]) object, "utf-8"));
        } catch (UnsupportedEncodingException e) {
        }
        return null;
    }
}

