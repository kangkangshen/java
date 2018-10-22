package archer.support;
/*
 *@author:wukang
 */

import archer.BeanDefinitionRegistrar;
import archer.BeanNameGenerator;
import archer.definition.BeanDefinition;
import archer.definition.BeanDefinitionManager;
import archer.support.baseImpl.BasedCountingBeanNameGenerator;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class DefaultBeanNameGenerator implements BeanNameGenerator {
    private boolean cached=false;
    private BeanNameGenerator instance;
    private Object mutex=new Object();

    public DefaultBeanNameGenerator(BeanNameGenerator impl) {
        this.instance=impl;
    }
    public DefaultBeanNameGenerator(){
        this.instance=new BasedCountingBeanNameGenerator();
    }


    @Override
    public String generateBeanName(BeanWrapper bean) {
        return instance.generateBeanName(bean);
    }

    public void setCached(boolean cached) {
        this.cached = cached;
    }

    private boolean isResovled(BeanDefinition bean){
        return false;
    }

    @Override
    public BeanNameGenerator getWrapedImpl() {
        return this.instance;
    }

    @Override
    public String generateBeanName(BeanDefinition definition) {
        return instance.generateBeanName(definition);
    }
}
