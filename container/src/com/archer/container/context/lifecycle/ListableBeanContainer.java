package archer.container.context.lifecycle;/*
 *@author:wukang
 */

import archer.container.definition.BeanDefinition;
import archer.container.support.NoSuchBeanException;

import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Map;


public interface ListableBeanContainer {
     List<BeanDefinition> getAllBeanDefinitions();

     List<BeanDefinition> getAllResovledBeanDefinitions();

     List<String> getAllBeanDefinitionNames();

     List<String> getBeanNames();

    boolean containsBeanDefinition(String beanName);

    int getBeanDefinitionCount();

    String[] getBeanNamesForType(Class<?> beanType);

    String[] getBeanNamesForType(Class<?> var1, boolean var2, boolean var3);

    <T> Map<String, T> getBeansOfType(Class<T> beanType) throws NoSuchBeanException;

    String[] getBeanNamesForAnnotation(Class<? extends Annotation> classAnnotation);

    Map<String, Object> getBeansWithAnnotation(Class<? extends Annotation> classAnnotation) ;

    <A extends Annotation> A findAnnotationOnBean(String var1, Class<A> classAnnotation) ;
}
