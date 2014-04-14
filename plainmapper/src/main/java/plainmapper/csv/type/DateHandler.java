package plainmapper.csv.type;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateHandler implements TypeHandler {
    @Override
    public boolean canGet(Class type) {
        return type == Date.class;
    }

    @Override
    public Object get(Class type, String string) {
        if (! canGet(type)) return null;

        try {
            return (Date) new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(string);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }
}

