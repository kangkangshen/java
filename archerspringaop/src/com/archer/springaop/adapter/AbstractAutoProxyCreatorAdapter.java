package archer.springaop.adapter;/*
 *@author:wukang
 */

import archer.container.support.BeanPostProcessor;
import archer.container.support.ConfigurableBeanContainer;
import archer.container.support.ListableBeanContainer;
import org.springframework.aop.framework.autoproxy.AbstractAutoProxyCreator;

public class AbstractAutoProxyCreatorAdapter implements BeanPostProcessor {
    private AbstractAutoProxyCreator delegation;
    private ListableBeanContainer container;
    @Override
    public Object processRawBeanBeforeInstantiation(Object o) {
        return o;
    }

    @Override
    public Object processInstanceAfterInstantiation(Object o) {
        return delegation.postProcessAfterInstantiation(o,null);
    }
}
