package archer.container.context.lifecycle;

public interface InitializingBean {
    void init() throws RuntimeException;
}
