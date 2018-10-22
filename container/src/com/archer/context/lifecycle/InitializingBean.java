package archer.context.lifecycle;

public interface InitializingBean {
    void init() throws RuntimeException;
}
