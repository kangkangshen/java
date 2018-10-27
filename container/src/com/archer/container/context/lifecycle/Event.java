package archer.container.context.lifecycle;

public interface Event {
    int START_EVENT=0;
    int INIT_EVENT=1;
    int DESTROY_EVENT=2;

    EventContext getContext();

}
