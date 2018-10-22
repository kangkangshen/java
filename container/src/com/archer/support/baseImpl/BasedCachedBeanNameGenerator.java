package archer.support.baseImpl;/*
 *@author:wukang
 */

import archer.BeanNameGenerator;
import archer.definition.BeanDefinition;
import archer.support.BeanWrapper;
import archer.support.ConfigurableBeanContainer;
import archer.support.ListableBeanContainer;

public class BasedCachedBeanNameGenerator implements BeanNameGenerator {
    private ListableBeanContainer container;
    public BasedCachedBeanNameGenerator(ListableBeanContainer container){
        this.container=container;
    }
    @Override
    public String generateBeanName(BeanWrapper wrapper) {
        return null;
    }

    @Override
    public BeanNameGenerator getWrapedImpl() {
        return null;
    }

    @Override
    public String generateBeanName(BeanDefinition definition) {
        return null;
    }
}
