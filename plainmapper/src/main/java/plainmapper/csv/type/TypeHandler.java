package plainmapper.csv.type;

public interface TypeHandler {
    public boolean canGet(Class type);
    public <T> T   get(Class<T> type, String string);
}
