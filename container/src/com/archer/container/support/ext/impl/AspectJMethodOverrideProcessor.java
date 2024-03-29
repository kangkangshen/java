package archer.container.support.ext.impl;
/*
 *@author:wukang
 */

import archer.container.BeanContainer;
import archer.container.support.BeanPostProcessor;
import archer.container.definition.MethodOverrides;
import archer.container.definition.RootBeanDefinition;
import archer.container.support.BeanContatinerAware;
import archer.container.support.ConfigurableBeanContainer;
import archer.container.util.ObjectUtils;

public class AspectJMethodOverrideProcessor implements BeanPostProcessor , BeanContatinerAware {

    private ConfigurableBeanContainer container;

    @Override
    public void setBeanContainer(BeanContainer container) {
        if(ObjectUtils.isInstanceOf(container, ConfigurableBeanContainer.class)){
            this.container= (ConfigurableBeanContainer) container;
        }
        throw new IllegalArgumentException("Current version require container must be ConfigurableBeanContainer type");
    }

    private Object getMethodOverridesProxy(RootBeanDefinition originalBeanDefinition, MethodOverrides overrides) {
        return null;

    }

    @Override
    public Object processRawBeanBeforeInitialization(Object rawBean) {
        return null;
    }

    @Override
    public Object processInstanceAfterInitialization(Object bean) {
        return null;
    }
}
