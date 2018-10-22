package archer.support.wrapper;/*
 *@author:wukang
 */

import archer.definition.BeanDefinition;

public class BeanWrapper {
    private final Object bean;
    private final BeanDefinition originalDefinition;
    private final String className;
    private  String beanName;
    private final Class<?> beanClass;


    public BeanWrapper(Object bean,String beanName,BeanDefinition definition){
        this.bean=bean;
        this.beanName=beanName;
        this.originalDefinition=definition;
        this.className=bean.getClass().getName();
        this.beanClass=bean.getClass();
    }
    public Object getBean() {
        return bean;
    }

    public Class<?> getBeanClass() {
        return beanClass;
    }

    public String getBeanName() {
        return beanName;
    }

    public String getBeanClassName() {
        return className;
    }

    public BeanDefinition getOriginalDefinition() {
        return originalDefinition;
    }
}
