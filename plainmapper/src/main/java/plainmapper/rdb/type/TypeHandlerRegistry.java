package plainmapper.rdb.type;

import java.util.ArrayList;
import java.util.List;

public class TypeHandlerRegistry {
    public static final List<TypeHandler> handlers = new ArrayList<>();

    static {
        handlers.add(new IntegerHandler());
        handlers.add(new ShortHandler());
        handlers.add(new LongHandler());
        handlers.add(new BooleanHandler());
        handlers.add(new StringHandler());
        handlers.add(new EnumHandler());
        handlers.add(new DateHandler());
    }


}
