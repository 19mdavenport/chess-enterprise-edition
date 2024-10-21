package util;

@SuppressWarnings("AbstractClassWithoutAbstractMethods")
public abstract class BaseFactory<T> implements Factory<T> {
    private final T t;

    protected BaseFactory(T t) {
        this.t = t;
    }

    @Override
    public T get() {
        return t;
    }
}
