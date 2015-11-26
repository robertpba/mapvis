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

    public boolean hasSameTupleElements(Tuple2<T1, T2> nodeTupleB){
        if(this.first == null && nodeTupleB.first == null && this.second == null && nodeTupleB.second == null){
            return true;
        }

        if(this.first != null && this.second == null){
            if(this.first.equals(nodeTupleB.first) && nodeTupleB.second == null)
                return true;

            if(this.first.equals(nodeTupleB.second) && nodeTupleB.first == null)
                return true;
            return false;
        }

        if(this.second != null && this.first == null){
            if(this.second.equals(nodeTupleB.second) && nodeTupleB.first == null)
                return true;

            if(this.second.equals(nodeTupleB.first) && nodeTupleB.second == null)
                return true;

            return false;
        }

        if(this.first.equals(nodeTupleB.first) && this.second.equals(nodeTupleB.second))
            return true;

        if(this.second.equals(nodeTupleB.first) && this.first.equals(nodeTupleB.second))
            return true;

        return false;
    }

    @Override
    public int hashCode() {
        int result = first != null ? first.hashCode() : 0;
        result = 31 * result + (second != null ? second.hashCode() : 0);
        return result;
    }
}
