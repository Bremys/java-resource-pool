
public interface Resource<T> extends AutoCloseable {
    T get();

    void close();
}