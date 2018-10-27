package archer.container.context.lifecycle;

public interface LifeCycleBean {
    void registerListener(EventListener listener);

    void start();

    void init();

    void destroy();

    void publishEvent(Event event);

}
