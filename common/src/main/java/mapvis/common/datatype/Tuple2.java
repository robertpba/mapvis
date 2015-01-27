package mapvis.common.datatype;

public class Tuple2<T1, T2> {
    public T1 first;
    public T2 second;

    public Tuple2(T1 first, T2 second) {
        this.first = first;
        this.second = second;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Tuple2 tuple2 = (Tuple2) o;

        if (first != null ? !first.equals(tuple2.first) : tuple2.first != null) return false;
        if (second != null ? !second.equals(tuple2.second) : tuple2.second != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = first != null ? first.hashCode() : 0;
        result = 31 * result + (second != null ? second.hashCode() : 0);
        return result;
    }
}
