package plainmapper.rdb.type;

import java.sql.ResultSet;
import java.sql.SQLException;

public interface TypeHandler {
    public boolean canGet(Class type);
    public <T> T   get(Class<T> type, ResultSet rs, int index) throws SQLException;
    public <T> T   get(Class<T> type, ResultSet rs) throws SQLException;
}
