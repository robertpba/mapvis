package plainmapper.csv.mapping;

import plainmapper.csv.type.TypeHandler;
import plainmapper.csv.type.TypeHandlerRegistry;
import plainmapper.metadata.object.MdClass;
import plainmapper.metadata.object.MdProperty;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

public class Mapper {
    MdClass mdClass;

    public Mapper(MdClass mdClass, String[] columns){
        this.mdClass = mdClass;

        for (int i = 0; i < mdClass.fields.length; i++) {
            if ( i == columns.length)
                break;

            Object data = null;
            Field field = mdClass.fields[i].field;
            Class type = field.getType();

            CacheEntry entry = new CacheEntry();

            entry.property = mdClass.fields[i];
            entry.index    = i;
            entry.handler = TypeHandlerRegistry.handlers.stream()
                    .filter(h -> h.canGet(type))
                    .findFirst().get();
            map.put(mdClass.fields[i], entry);
        }
    }

    Map<MdProperty, CacheEntry> map = new HashMap<>();

    class CacheEntry {
        MdProperty  property;
        TypeHandler handler;
        int         index;
    }

    public Object createObject(String[] columns) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        Object obj = mdClass.oType.getConstructor().newInstance();

        for (Map.Entry<MdProperty, CacheEntry> entryEntry : map.entrySet()) {
            MdProperty field = entryEntry.getKey();
            TypeHandler handler = entryEntry.getValue().handler;
            Object data = handler.get(field.field.getType(), columns[entryEntry.getValue().index]);
            field.field.set(obj, data);
        }
        return obj;
    }
}
