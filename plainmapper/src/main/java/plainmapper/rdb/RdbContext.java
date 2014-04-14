package plainmapper.rdb;

import plainmapper.metadata.object.ClassMetadataBuilder;
import plainmapper.metadata.object.MdClass;
import plainmapper.metadata.rdbs.MdQuery;
import plainmapper.metadata.rdbs.SqlMetadataBuilder;
import plainmapper.rdb.mapping.Mapper;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RdbContext implements AutoCloseable {
    private final Connection conn;

    public RdbContext(Connection conn) {
        this.conn = conn;
    }

    public RdbContext(String url, String username, String password) {
        url = url == null ? "jdbc:mysql://localhost:3307/plainmapper.metadata.test" : url;
        //String driver = "com.mysql.jdbc.Driver";

        try {
            //Class.forName(driver); // not needed after 1.6
            conn = DriverManager.getConnection(url, username, password);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    Map<String, Map<Class, Mapper>> cacheMapping = new HashMap<>();

    public <T> List<T> query(Class<T> type, String sql, Object... params) throws Exception {
        List<T> results = new ArrayList<>();

        PreparedStatement statement = conn.prepareStatement(sql);

        for (int i = 0; i < params.length; i++) {
            statement.setObject(i + 1, params[i]);
        }

        ResultSet rs = statement.executeQuery();

        Mapper mapper = getCachedMapping(type, sql, rs);

        while (rs.next()) {
            T obj = (T) mapper.createObject(rs);
            results.add(obj);
        }

        return results;
    }

    private <T> Mapper getCachedMapping(Class<T> type, String sql, ResultSet rs) throws SQLException {
        Map<Class, Mapper> classMappings = cacheMapping.get(sql);
        if (classMappings==null)
            classMappings = new HashMap<>();

        Mapper mapper = classMappings.get(type);
        if (mapper ==null) {
            MdClass<T> mdClass = ClassMetadataBuilder.getClassMetaData(type);
            MdQuery mdQuery = SqlMetadataBuilder.getClassMetaData(sql, rs.getMetaData());
            mapper = new Mapper(mdQuery , mdClass);
            classMappings.put(type, mapper);
        }
        return mapper;
    }


    @Override
    public void close() throws Exception {
        if (!conn.isClosed())
            conn.close();
    }
}
