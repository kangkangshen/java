package archer.config;
/*
 *@author:wukang
 *
 */

public interface HasVerticalRelationShip<T> {

    void setParent(T t);

    T getparent();

}
