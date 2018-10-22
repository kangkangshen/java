package archer.definition;

import archer.support.ListableBeanContainer;
import archer.support.NoSuchBeanDefinitionException;

/*
判断当前beanDifinition状态以及管理BeanDefinition
 */
public interface BeanDefinitionManager extends ListableBeanContainer {

    boolean instantiable(BeanDefinition beanDefinition);

    void setAllowBeanDefinitionOverride(boolean allowBeanDefinitionOverride);

    boolean getAllowBeanDefinitionOverride();

    boolean isLazyInit(BeanDefinition definition);

    boolean containsBeanDefinition(BeanDefinition definition);

    void removeBeanDefinition(String beanName) throws NoSuchBeanDefinitionException;

    @Override
    BeanDefinition getBeanDefinition(String beanName) throws NoSuchBeanDefinitionException;

    boolean containsBeanDefinition(String beanName);

    String[] getBeanDefinitionNames();

    int getBeanDefinitionCount();

    boolean isBeanNameInUse(String beanName);






}
