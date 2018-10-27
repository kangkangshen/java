package archer.container.definition;

import java.util.Map;

public interface BeanDefinition  {
    String SCOPE_SINGLETON = "singleton";
    String SCOPE_PROTOTYPE = "prototype";
    int ROLE_APPLICATION = 0;
    int ROLE_SUPPORT = 1;
    int ROLE_INFRASTRUCTURE = 2;


    void setId(String id);

    String getId();

    void setAliases(String[] aliases);

    String[] getAliases();

    /*
    不提供此方法
    理由是：该对象代表从XML解析出来的bean的原始定义，是属于静态的，不应该提供动态的添加别名的功能
     */
    //void addAlias(String alias);

    void setParentName(String parentName);

    String getParentName();

    void setBeanClassName(String beanClassName);

    String getBeanClassName();

    Class<?> getBeanClass();

    void setScope(String scope);

    String getScope();

    void setLazyInit(boolean lazyInit);

    boolean isLazyInit();

    void setDependsOn(String... beanNames);

    String[] getDependsOn();

    void setPrimary(boolean primary);

    boolean isPrimary();

    void setFactoryBeanName(String factoryBeanName);

    String getFactoryBeanName();

    void setFactoryMethodName(String factoryMethodName);

    String getFactoryMethodName();

    ConstructorArgumentValues getConstructorArgumentValues();

    OptionalPropertyValues getPropertyValues();

    boolean isSingleton();

    boolean isPrototype();

    String getDescription();

    BeanDefinition getOriginatingBeanDefinition();

    String getInitMethod();

    Map<String,String> getLookUpMethod();

    Map<String,String> getReplaceMethod();

    String getDestroyMethod();

    boolean isOverriding();


}