package plainmapper.csv;

import plainmapper.csv.type.TypeHandler;
import plainmapper.csv.type.TypeHandlerRegistry;
import plainmapper.metadata.object.ClassMetadataBuilder;
import plainmapper.metadata.object.MdClass;

import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;

public class CsvContext {
    InputStream  input;
    BufferedReader reader;
    String    separator = "\t";

    public CsvContext(InputStream input)
    {
        this.input = input;
    }
    public CsvContext(InputStream input, String separator)
    {
        this.input = input;
        this.separator = separator;
    }

    public <T> T Read(Class<T> type) throws IOException, NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        if (reader == null) {
            reader = new BufferedReader(new InputStreamReader(input));
        }

        MdClass<T> classMeta = ClassMetadataBuilder.getClassMetaData(type);

        String line = reader.readLine();
        if (line == null)
            return null;
        String[] columns = line.split(separator);

        T obj;
        obj = type.getConstructor().newInstance();

        for (int i = 0; i < classMeta.fields.length; i++) {
            if ( i == columns.length)
                break;

            Object data = null;
            Field field = classMeta.fields[i].field;

            for (TypeHandler handler : TypeHandlerRegistry.handlers) {
                if (handler.canGet(field.getType())){
                    data = handler.get(field.getType(), columns[i]);
                    break;
                }
            }
            field.set(obj, data);
        }

        return obj;
    }
}
