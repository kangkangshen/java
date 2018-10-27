package archer.container;/*
bean 注册器
 */


import archer.container.definition.BeanDefinition;

public interface BeanDefinitionRegistrar {

    void registerBeanDefinition(BeanDefinition bean, String name);




}
