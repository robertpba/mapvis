package plainmapper.rdb.type;

import java.sql.ResultSet;
import java.sql.SQLException;

public abstract class BaseTypeHandler implements TypeHandler {
    protected int index;

    @Override
    public <T> T get(Class<T> type, ResultSet rs) throws SQLException {
        return get(type, rs, index);
    }
}
