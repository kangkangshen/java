package archer.support;/*
 *@author:wukang
 */

import archer.context.lifecycle.Event;
import archer.context.lifecycle.EventContext;

public class ContainerEvent implements Event {
    private ConfigurableBeanContainer container;
    public ContainerEvent(ConfigurableBeanContainer container){
        this.container=container;
    }

    public void onStart(){};

    public void onInit(){};

    public void onDestroy(){};

    @Override
    public EventContext getContext() {
        return (EventContext)container;
    }
}
