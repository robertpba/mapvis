package plainmapper.rdb.type;

import java.io.UnsupportedEncodingException;
import java.sql.SQLException;

import java.sql.ResultSet;

public class StringHandler extends BaseTypeHandler implements TypeHandler {
    @Override
    public boolean canGet(Class type) {
        return type == String.class;
    }

    @Override
    public <T> T get(Class<T> type, ResultSet rs, int index) throws SQLException {
        if (type != String.class)
            return null;
        Object object = rs.getObject(index);
        if (object instanceof String)
            return (T) object;

        else if (object instanceof byte[])
            try {
                return (T) new String( (byte[])object, "utf-8");
            } catch (UnsupportedEncodingException e) {
            }

        return (T) object.toString();

        //return null;
    }
}
