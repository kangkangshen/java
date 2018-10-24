package archer.support.ext.impl;
/*
 *@author:wukang
 */

import archer.BeanContainer;
import archer.support.BeanPostProcessor;
import archer.definition.MethodOverrides;
import archer.definition.RootBeanDefinition;
import archer.support.BeanContatinerAware;
import archer.support.ConfigurableBeanContainer;
import archer.util.BeanUtils;
import archer.util.ObjectUtils;
import net.sf.cglib.proxy.Enhancer;

public class AspectJMethodOverrideProcessor implements BeanPostProcessor , BeanContatinerAware {

    private ConfigurableBeanContainer container;

    @Override
    public Object processRawBeanBeforeInstantiation(Object rawBean) {
        if(rawBean instanceof RootBeanDefinition){
            RootBeanDefinition originalBeanDefinition= (RootBeanDefinition) rawBean;
            MethodOverrides overrides=originalBeanDefinition.getMethodOverrides();
            overrides.validate();
            return getMethodOverridesProxy(originalBeanDefinition,overrides);
        }
        return null;
    }



    @Override
    public Object processInstanceAfterInstantiation(Object bean) {
        return bean;
    }


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

}
