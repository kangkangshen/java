package archer;/*
bean 注册器
 */


import archer.definition.BeanDefinition;

public interface BeanDefinitionRegistrar {

    void registerBeanDefinition(BeanDefinition bean, String name);




}
