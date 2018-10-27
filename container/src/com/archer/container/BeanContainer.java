package archer.container;

import java.util.List;

public interface BeanContainer {

    String FACTORY_BEAN_PREFIX="&";

    Object getBean(String name);

    <T> T getBean(Class<T> type);

    <T> T getBean(String name,Class<T> beanType);

    Object getBean(String name,Object[] args);

    Object getBean(Class<?> type,Object[] args);

    <T> T getBean(String name,Class<T> type,Object[] args);

    boolean isSingleton(String name);

    boolean isPrototype(String name);

    boolean containsBean(String name);

    boolean containsBean(Class beanType);

    Class<?> getType(String name);

    <T> List<T> getBeansOfSpecifiedType(Class<T> type);



}
