package archer.container.definition;/*
 *@author:wukang
 */


import archer.container.support.ReturnValuesDontMatchException;
import archer.container.util.ObjectUtils;
import archer.container.util.StringUtils;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.Set;

public class BaseBeanDefinitionHolder implements BeanDefinition {
    private String id;
    private String beanClassName;
    private Class<?> beanClass;
    private String factoryBeanName;
    private ConstructorArgumentValues constructorArgumentValues;
    private OptionalPropertyValues optionalPropertyValues;
    private boolean resovled=false;
    private String parentName;
    private String scope;
    private static String definitionScope=BeanDefinition.SCOPE_SINGLETON;
    private boolean lazyInit=false;
    private String[] dependsOn;
    //private boolean isAbstract;
    private String factoryMethod;
    private String[] aliases;
    private boolean primary;
    private String description;
    private BeanDefinition originalBeanDefinition;
    private String initMethod;
    private Map<String,String> lookupMethod;
    private Map<String,String> replaceMethod;
    private String destroyMethod;
    private boolean overriding;
    public BaseBeanDefinitionHolder(Class<?> beanClass){
        this.beanClass=beanClass;
    }
    public BaseBeanDefinitionHolder(String id,Class<?>beanClass){
        this.id=id;
        this.beanClass=beanClass;
    }
    public BaseBeanDefinitionHolder(String id,String beanClassName){
        this.id=id;
        this.beanClassName=beanClassName;
    }


    @Override
    public void setId(String id) {
        this.id=id;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public void setAliases(String[] aliases) {
        this.aliases=aliases;
    }

    @Override
    public String[] getAliases() {
        return this.aliases;
    }

    @Override
    public void setParentName(String parentName) {
        this.parentName=parentName;
    }

    @Override
    public String getParentName() {
        return this.parentName;
    }

    @Override
    public void setBeanClassName(String beanClassName) {
        StringUtils.hasText(beanClassName);
        try {
            Class beanClazz=Class.forName(beanClassName);
            this.beanClassName=beanClassName;
            this.beanClass=beanClazz;
        } catch (ClassNotFoundException e) {
            throw new IllegalArgumentException("class "+beanClassName+" not found");
        }
    }

    @Override
    public Class<?> getBeanClass() {
        return this.beanClass;
    }

    public void setBeanClass(Class<?> beanClass) {
        this.beanClass = beanClass;
    }

    @Override
    public String getBeanClassName() {
        return this.beanClassName;
    }

    @Override
    public void setScope(String scope) {
        this.scope=scope;
    }

    @Override
    public String getScope() {
        if(this.scope!=null) {
            return this.scope;
        }
        return BaseBeanDefinitionHolder.definitionScope;
    }

    @Override
    public void setLazyInit(boolean lazyInit) {
        this.lazyInit=lazyInit;
    }

    @Override
    public boolean isLazyInit() {
        return this.lazyInit;
    }

    @Override
    public void setDependsOn(String... beanNames) {
        this.dependsOn=beanNames;
    }

    @Override
    public String[] getDependsOn() {
        return this.dependsOn;
    }


    @Override
    public void setPrimary(boolean primary) {
        this.primary=primary;
    }

    @Override
    public boolean isPrimary() {
        return this.primary;
    }

    @Override
    public void setFactoryBeanName(String factoryBeanName) {
        this.factoryBeanName=factoryBeanName;
    }

    @Override
    public String getFactoryBeanName() {
        return this.factoryBeanName;
    }

    @Override
    public void setFactoryMethodName(String factoryMethodName) {
        this.factoryMethod=factoryMethodName;
    }

    @Override
    public String getFactoryMethodName() {
        return factoryMethod;
    }

    @Override
    public ConstructorArgumentValues getConstructorArgumentValues() {
        return this.constructorArgumentValues;
    }

    @Override
    public OptionalPropertyValues getPropertyValues() {
        return this.optionalPropertyValues;
    }

    @Override
    public boolean isSingleton() {
        return scope==null||scope.equals(BeanDefinition.SCOPE_SINGLETON);
    }

    @Override
    public boolean isPrototype() {
        if(isSingleton()){
            return false;
        }else{
            return true;
        }
    }


    @Override
    public String getDescription() {
        return this.description;
    }

    @Override
    public BeanDefinition getOriginatingBeanDefinition() {
        return this.originalBeanDefinition;
    }

    @Override
    public String getInitMethod() {
        return initMethod;
    }

    @Override
    public Map<String,String> getLookUpMethod() {
        return this.lookupMethod;
    }

    @Override
    public Map<String,String> getReplaceMethod() {
        return this.replaceMethod;
    }

    @Override
    public String getDestroyMethod() {
        return this.destroyMethod;
    }

    @Override
    public boolean isOverriding() {
        return overriding;
    }

    public void validateMethodOverrides() {
        if((replaceMethod==null&&lookupMethod==null)||(replaceMethod.isEmpty()&&lookupMethod.isEmpty())){
            return ;
        }
        Set<String> replaceKeys= replaceMethod.keySet();
        Set<String> lookupKeys=lookupMethod.keySet();
        Class<?> currentType=getBeanClass();
        for(String replaceKey:replaceKeys){
            Method method=ObjectUtils.getMethodIfUnique(replaceKey,currentType);
            String targetMethod=replaceMethod.get(replaceKey);
            if(replaceKey.equals(targetMethod)){
                throw new ReturnValuesDontMatchException("The two methods are essentially the same");
            }
            Method tarMethod=ObjectUtils.getMethodIfUnique(targetMethod,currentType);
            if(ObjectUtils.isArrayEquals(method.getParameterTypes(),tarMethod.getParameterTypes())&&tarMethod.getReturnType().isAssignableFrom(method.getReturnType())){
                continue;
            }else{
                throw new ReturnValuesDontMatchException("The parameters and return values of the two methods do not match");
            }
        }
        for(String lookupKey:lookupKeys){
            Method method= ObjectUtils.getMethodIfUnique(lookupKey,currentType);
            if(method.getReturnType().equals(void.class)){
                throw new ReturnValuesDontMatchException("Expect to return an object, but now return empty");
            }
        }

    }

    public void setConstructorArgumentValues(ConstructorArgumentValues constructorArgumentValues) {
        this.constructorArgumentValues = constructorArgumentValues;
    }

    public OptionalPropertyValues getOptionalPropertyValues() {
        return optionalPropertyValues;
    }

    public void setOptionalPropertyValues(OptionalPropertyValues optionalPropertyValues) {
        this.optionalPropertyValues = optionalPropertyValues;
    }
    public boolean isResovled(){
        return resovled;
    }

    public void setResovled(boolean resovled){
        this.resovled=resovled;
    }
}
