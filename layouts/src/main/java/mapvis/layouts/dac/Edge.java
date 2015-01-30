package mapvis.layouts.dac;

class Edge<T>{
    T src;
    T dst;

    public T getSrc() {
        return src;
    }
    public T getDst() {
        return dst;
    }

    public Edge(T src, T dst) {
        this.src = src;
        this.dst = dst;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Edge edge = (Edge) o;

        if (!dst.equals(edge.dst)) return false;
        if (!src.equals(edge.src)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = src.hashCode();
        result = 31 * result + dst.hashCode();
        return result;
    }
}
