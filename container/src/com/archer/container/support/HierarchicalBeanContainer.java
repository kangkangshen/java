package archer.container.support;/*
 *@author:wukang
 */

import archer.container.BeanContainer;

public interface HierarchicalBeanContainer {
    void setParentContainer(BeanContainer parentContainer);
}
