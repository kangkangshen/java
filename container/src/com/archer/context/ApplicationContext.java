package archer.context;/*
 *@author:wukang
 */

import archer.context.lifecycle.LifeCycleBean;
import archer.support.ConfigurableBeanContainer;
import archer.support.debug.Description;
import archer.support.ext.ExtensibleBeanContainer;

@Description(description = "添加注解支持和java配置文件")
public interface ApplicationContext extends LifeCycleBean, ConfigurableBeanContainer, ExtensibleBeanContainer {

    void setConfigClass(Class<?> configClass);

    ConfigurableBeanContainer getConfigurableBeanContainer();



}
