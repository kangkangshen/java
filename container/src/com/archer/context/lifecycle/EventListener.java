package archer.context.lifecycle;/*
 *@author:wukang
 */

import archer.context.lifecycle.Event;

public interface EventListener<T extends Event> {

     boolean interested(Class<T> event);

     void onEvent();



}
