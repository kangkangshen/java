package archer.container.context;/*
 *@author:wukang
 */

import archer.container.context.lifecycle.LifeCycleBean;
import archer.container.support.ConfigurableBeanContainer;
import archer.container.support.debug.Description;
import archer.container.support.ext.ExtensibleBeanContainer;

@Description(description = "添加注解支持和java配置文件")
public interface ApplicationContext extends LifeCycleBean, ConfigurableBeanContainer, ExtensibleBeanContainer {

    void setConfigClass(Class<?> configClass);

    ConfigurableBeanContainer getConfigurableBeanContainer();



}
