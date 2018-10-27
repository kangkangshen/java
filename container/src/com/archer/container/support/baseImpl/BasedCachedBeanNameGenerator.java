package archer.container.support.baseImpl;/*
 *@author:wukang
 */

import archer.container.BeanNameGenerator;
import archer.container.definition.BeanDefinition;
import archer.container.support.BeanWrapper;
import archer.container.support.ListableBeanContainer;

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
