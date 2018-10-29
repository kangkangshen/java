package archer.springaop;/*
 *@author:wukang
 */


import archer.container.support.ConfigurableBeanContainer;
import org.springframework.aop.config.AopNamespaceHandler;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.core.ResolvableType;

import java.lang.annotation.Annotation;
import java.util.Map;

public  class BeanContainerAdapter implements ListableBeanFactory{

}
