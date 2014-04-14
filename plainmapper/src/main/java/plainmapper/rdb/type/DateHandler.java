package plainmapper.rdb.type;

import java.io.UnsupportedEncodingException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

public class DateHandler extends BaseTypeHandler implements TypeHandler {
    @Override
    public boolean canGet(Class type) {
        return type == Date.class;
    }

    @Override
    public <T> T get(Class<T> type, ResultSet rs, int index) throws SQLException {
        if (!canGet(type)) return null;

        Object object = rs.getDate(index);

        return (T) object;

        //return null;
    }
}
