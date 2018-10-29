package archer.container.definition;

import archer.container.support.ListableBeanContainer;
import archer.container.support.NoSuchBeanDefinitionException;

/*
判断当前beanDifinition状态以及管理BeanDefinition
 */
public interface BeanDefinitionManager extends ListableBeanContainer {

    boolean instantiable(BeanDefinition beanDefinition);

    boolean isLazyInit(BeanDefinition definition);

    void removeBeanDefinition(String beanName) throws NoSuchBeanDefinitionException;

    BeanDefinition getBeanDefinition(String beanName) throws NoSuchBeanDefinitionException;

    boolean containsBeanDefinition(String beanName);

    String[] getBeanDefinitionNames();

    int getBeanDefinitionCount();

    boolean isBeanNameInUse(String beanName);

}
