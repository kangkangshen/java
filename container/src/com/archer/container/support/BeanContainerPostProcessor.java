package archer.container.support;/*
 *@author:wukang
 */

import archer.container.support.debug.Description;
import archer.container.support.ext.ExtensibleBeanContainer;

public interface BeanContainerPostProcessor {
    @Description(description = "当容器调用构造器创建后调用此方法")
    void whenContainerCreated(ExtensibleBeanContainer container);

    @Description(description = "当容器调用初始化方法后调用此方法")
    void whenContainerInited(ExtensibleBeanContainer container);

    @Description(description = "当容器销毁后调用此方法")
    void whenContainerDestroyed(ExtensibleBeanContainer container);
}
