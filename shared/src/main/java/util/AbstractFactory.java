package util;

public abstract class AbstractFactory<T> implements Factory<T> {
    private final T t;

    protected AbstractFactory(T t) {
        this.t = t;
    }

    @Override
    public T get() {
        return t;
    }
}
