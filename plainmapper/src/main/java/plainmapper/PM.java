package plainmapper;

import plainmapper.csv.CsvContext;
import plainmapper.rdb.RdbContext;

import java.io.InputStream;
import java.sql.*;

public class PM {
    private final Connection conn;

    public PM(Connection conn) {
        this.conn = conn;
    }

    public static RdbContext Rdb(String url, String username, String password) throws ClassNotFoundException {
        return new RdbContext(url, username, password);
    }
    public static CsvContext Csv(InputStream stream)
    {
        return new CsvContext(stream);
    }


}