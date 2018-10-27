package archer.container.context.lifecycle;/*
 *@author:wukang
 */

public interface EventListener<T extends Event> {

     boolean interested(Class<T> event);

     void onEvent();



}
