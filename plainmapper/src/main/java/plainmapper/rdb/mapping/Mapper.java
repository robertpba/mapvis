package plainmapper.rdb.mapping;

import plainmapper.annotation.Column;
import plainmapper.metadata.object.MdClass;
import plainmapper.metadata.object.MdProperty;
import plainmapper.metadata.rdbs.MdColumn;
import plainmapper.metadata.rdbs.MdQuery;
import plainmapper.metadata.rdbs.MdRow;
import plainmapper.rdb.type.TypeHandler;
import plainmapper.rdb.type.TypeHandlerRegistry;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class Mapper {
    MdQuery mdQuery;
    MdRow   mdRow;
    MdClass mdClass;

    Map<MdProperty, CacheEntry> map = new HashMap<>();

    class CacheEntry {
        MdProperty  property;
        MdColumn    column;
        TypeHandler handler;
    }

    public Mapper(MdQuery mdQuery, MdClass MdClass)
    {
        this.mdQuery = mdQuery;
        this.mdRow   = mdQuery.result;
        this.mdClass = MdClass;

        for (MdProperty field : MdClass.fields) {
            String specifiedColumnName = null;
            for (Annotation annotation : field.annotations) {
                if (annotation instanceof Column)
                    specifiedColumnName = ((Column) annotation).value();
            }
            MdColumn matched = null;
            for (MdColumn column : mdRow.columns) {
                if (specifiedColumnName != null)
                {
                    if (column.name.compareToIgnoreCase(specifiedColumnName) == 0){
                        matched = column;
                    }
                } else {
                    if (column.name.compareToIgnoreCase(field.field.getName()) == 0){
                        matched = column;
                    }
                }
                if (matched != null)
                    break;
            }

            if (matched != null) {

                CacheEntry entry = new CacheEntry();
                entry.column = matched;
                entry.property = field;

                Class type = field.field.getType();

                entry.handler = TypeHandlerRegistry.handlers.stream()
                        .filter(h -> h.canGet(type))
                        .findFirst().get();

                map.put(field, entry);
            }
        }
    }

    public Object createObject(ResultSet resultSet) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException, SQLException {
        Object obj = mdClass.oType.getConstructor().newInstance();

        for (Map.Entry<MdProperty, CacheEntry> mapEntry : map.entrySet()) {
            CacheEntry cacheEntry = mapEntry.getValue();
            Object data = cacheEntry.handler.get(cacheEntry.property.field.getType(), resultSet, cacheEntry.column.index);
            cacheEntry.property.field.set(obj, data);
        }
        return obj;
    }
}