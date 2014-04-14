package plainmapper.metadata.rdbs;

import plainmapper.annotation.MappingAnnotation;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SqlMetadataBuilder {

    static final Map<String, MdQuery> cache = new HashMap<>();

    public MdQuery getCachedClassMetaData(String sql) {
        return cache.get(sql);
    }

    public static MdQuery getClassMetaData(String sql, ResultSetMetaData rsmd) throws SQLException {
        @SuppressWarnings("unchecked")
        MdQuery metaData = cache.get(sql);
        if (metaData == null)
            cache.put(sql, metaData = createQueryMetaData(sql, rsmd));

        return metaData;
    }

    public static <T> MdQuery createQueryMetaData(String sql, ResultSetMetaData rsmd) throws SQLException {
        MdQuery mdQuery = new MdQuery();
        mdQuery.result = createRowMetaData(rsmd);
        mdQuery.sql = sql;
        return mdQuery;
    }


    public static <T> MdRow createRowMetaData(ResultSetMetaData rsmd) throws SQLException {

        MdRow mdRow = new MdRow();
        ArrayList<MdColumn> columns = new ArrayList<>();

        int columnCount = rsmd.getColumnCount();
        for (int i = 1; i < columnCount + 1; i++ ) {
            MdColumn columnMetadata = new MdColumn();
            columnMetadata.index = i;
            columnMetadata.type  = rsmd.getColumnType(i);
            columnMetadata.name  = rsmd.getColumnName(i);

            columns.add(columnMetadata);
        }
        mdRow.columns = columns.toArray(new MdColumn[columns.size()]);

        return mdRow;
    }

    public static List<Annotation> getFieldMappingAnnotations(Field field) {
        return Stream.of(field.getAnnotations())
                .filter(a -> isMappingAnnotationType(a.annotationType()))
                .collect(Collectors.toList());
    }

    public static List<Annotation> getClassMappingAnnotations(Class type) {
        return Stream.of(type.getAnnotations())
                .filter(a -> isMappingAnnotationType(a.annotationType()))
                .collect(Collectors.toList());
    }

    public static boolean isMappingAnnotationType(Class type) {
        return Stream.of(type.getAnnotations())
                .anyMatch(a -> a.annotationType() == MappingAnnotation.class);
    }
}
