package archer.container.support;/*
 *@author:wukang
 */

import archer.container.definition.BeanDefinition;
import archer.container.context.lifecycle.Event;
import archer.container.context.lifecycle.EventListener;
import archer.container.context.lifecycle.LifeCycleBean;
import archer.container.context.lifecycle.ListableBeanContainer;

import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Map;

public class LifeCycleBeanContainer extends ConfigurationBasedBeanContainer implements ListableBeanContainer, LifeCycleBean {

    public LifeCycleBeanContainer(String configLocation) {
        super(configLocation);
    }

    @Override
    public void registerListener(EventListener listener) {

    }

    @Override
    public void start() {

    }

    @Override
    public void init() {

    }

    @Override
    public void destroy() {

    }

    @Override
    public void publishEvent(Event event) {

    }

    @Override
    public List<BeanDefinition> getAllBeanDefinitions() {
        return null;
    }

    @Override
    public List<BeanDefinition> getAllResovledBeanDefinitions() {
        return null;
    }

    @Override
    public List<String> getAllBeanDefinitionNames() {
        return null;
    }

    @Override
    public void addBean(String name, Object bean) {

    }

    @Override
    public void removeBean(String name) {

    }

    @Override
    public void removeBean(Class<?> type) {

    }

    @Override
    public List<String> getBeanNames() {
        return null;
    }

    @Override
    public int getBeanCount() {
        return 0;
    }

    @Override
    public List<BeanDefinition> getBeanDefinitions() {
        return null;
    }

    @Override
    public List<BeanDefinition> getBeanDefinition(Class<?> type) {
        return null;
    }

    @Override
    public boolean containsBeanDefinition(String beanName) {
        return false;
    }

    @Override
    public int getBeanDefinitionCount() {
        return 0;
    }

    @Override
    public String[] getBeanNamesForType(Class<?> beanType) {
        return new String[0];
    }

    @Override
    public String[] getBeanNamesForType(Class<?> var1, boolean var2, boolean var3) {
        return new String[0];
    }

    @Override
    public <T> Map<String, T> getBeansOfType(Class<T> beanType) {
        return null;
    }

    @Override
    public String[] getBeanNamesForAnnotation(Class<? extends Annotation> classAnnotation) {
        return new String[0];
    }

    @Override
    public Map<String, Object> getBeansWithAnnotation(Class<? extends Annotation> classAnnotation) {
        return null;
    }

    @Override
    public <A extends Annotation> A findAnnotationOnBean(String var1, Class<A> classAnnotation) {
        return null;
    }

    @Override
    public <T> T getBean(Class<T> type) {
        return null;
    }

    @Override
    public Object getBean(String name, Object[] args) {
        return null;
    }

    @Override
    public Object getBean(Class<?> type, Object[] args) {
        return null;
    }

    @Override
    public <T> T getBean(String name, Class<T> type, Object[] args) {
        return null;
    }

    @Override
    public boolean isSingleton(String name) {
        return false;
    }

    @Override
    public boolean isPrototype(String name) {
        return false;
    }

    @Override
    public Class<?> getType(String name) {
        return null;
    }
}
