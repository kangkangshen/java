package archer.container.support;/*
 *@author:wukang
 */

import archer.container.PropertyValue;
import archer.container.definition.ConstructorArgumentValues;
import archer.container.definition.OptionalPropertyValues;
import archer.container.support.debug.Description;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/*
    为什么这么写，考虑到在使用中往往ClassPool总是取默认值，且本身此类也对ParamNameMethodDiscover做了实现，
    因此提供一个获取默认的工厂方法，但是不能排除用户有个性化设置，因此也提供了一个构造器
 */
@Description(description = "此类需要配合ConfigurableBeanContainer使用")
public class MethodAccessor implements ParamNameMethodDiscover,MethodInfoAccessor{
    private ListableBeanContainer container;
    private Class<?> currentlyAccessedClass;
    private ParamNameMethodDiscover delegation;
    private static MethodAccessor accessor=new MethodAccessor();
    private Map<Class,ConstructorArgumentValues> cstCache=new HashMap<>(16);
    private Map<Class,OptionalPropertyValues> opCache=new HashMap<>(16);
    private MethodAccessor(){
        delegation=new DefaultParamNameDiscover();
    }
    public MethodAccessor(ParamNameMethodDiscover delegation, ListableBeanContainer container){
        this.delegation=delegation;
        this.container=container;
    }
    public static MethodAccessor getDefault(){
        return accessor;
    }

    @Override
    public void setCurrentlyAccessedClass(Class<?> type) {
        this.currentlyAccessedClass=type;
    }

    @Override
    public String[] getMethodParamNames(String methodName) {
        return delegation.getMethodParamNames(methodName);
    }

    @Override
    public int getIndexOfParam(String methodName, String paramName) {
        return delegation.getIndexOfParam(methodName,paramName);
    }

    @Override
    public Class<?> getTypeOfParam(String methodName, String paramName) {
        return delegation.getTypeOfParam(methodName,paramName);
    }

    @Override
    public Class<?> getTypeOfParam(String methodName, int index) {
        return delegation.getTypeOfParam(methodName,index);
    }

    @Override
    public boolean isTypeMatch(String methodName, Object[] args) {
        return delegation.isTypeMatch(methodName,args);
    }

    @Override
    public String[] getMethodParamNames(Method method) {
        return delegation.getMethodParamNames(method);
    }

    @Override
    public String[] getConstructorParamNames(Constructor constructor) {
        return delegation.getConstructorParamNames(constructor);
    }

    @Override
    public int getIndexOfParam(Method method, String param) {
        return delegation.getIndexOfParam(method,param);
    }

    @Override
    public int getIndexOfParam(Constructor constructor, String paramName) {
        return delegation.getIndexOfParam(constructor,paramName);
    }

    @Override
    public Class<?> getTypeOfParam(Method method, String paramName) {
        return delegation.getTypeOfParam(method,paramName);
    }

    @Override
    public Class<?> getTypeOfParam(Method method, int index) {
        return delegation.getTypeOfParam(method,index);
    }

    @Override
    public Class<?> getTypeOfParam(Constructor c, int index) {
       return  delegation.getTypeOfParam(c,index);
    }

    @Description(description = "处理效果与isTypeMatch(Constructor,ConstructorArgumentValues)相同")
    @Override
    public boolean isTypeMatch(Method method, OptionalPropertyValues methodParam) {
        if(method.getDeclaringClass().equals(methodParam.getBeanClass())){
            List<TypeConverter> converters=null;
            if(container!=null){
                converters=container.getBeansOfSpecifiedType(TypeConverter.class);
            }
            List<PropertyValue> propertyValues=methodParam.getOptionalPropertyValues();
            Class<?>[] paramTypes=method.getParameterTypes();
            if(propertyValues.size()==paramTypes.length){
                for(PropertyValue propertyValue:propertyValues){
                    int index=propertyValue.getIndex();
                    if(index==-1){
                        index=getIndexOfParam(method,propertyValue.getParamName());
                        if(index==-1){
                            return false;
                        }
                    }
                    boolean existCorrespondConverter=false;
                    if(converters!=null&&!converters.isEmpty()){
                        for(TypeConverter converter:converters){
                            if(converter.support(propertyValue.getValue().getClass(),paramTypes[index])){
                                existCorrespondConverter=true;
                            }
                        }
                    }
                    if((!paramTypes[index].isAssignableFrom(propertyValue.getValue().getClass()))&&!existCorrespondConverter){
                        return false;
                    }
                }
                return true;
            }else{
                return false;
            }
        }else{
            return false;
        }
    }

    @Description(description = "当前constructorArgs.originalValue存的是从xml中读取的string,constructorArgs.value存的是经过container内部#ref所指的bean")
    @Override
    public boolean isTypeMatch(Constructor constructor, ConstructorArgumentValues constructorArgs) {
        if(constructor.getDeclaringClass().equals(constructorArgs.getBeanClass())){
            List<TypeConverter> converters=null;
            if(container!=null){
                converters=container.getBeansOfSpecifiedType(TypeConverter.class);
            }
            List<PropertyValue> propertyValues=constructorArgs.getConstructorArgumentValues();
            Class<?>[] paramTypes=constructor.getParameterTypes();
            if(propertyValues.size()==paramTypes.length){
                for(PropertyValue propertyValue:propertyValues){
                    int index=propertyValue.getIndex();
                    if(index==-1){
                        index=getIndexOfParam(constructor,propertyValue.getParamName());
                        if(index==-1){
                            return false;
                        }
                    }
                    boolean existCorrespondConverter=false;
                    if(converters!=null&&!converters.isEmpty()){
                        for(TypeConverter converter:converters){
                            if(converter.support(propertyValue.getOriginalValue().getClass(),paramTypes[index])){
                                //todo
                                propertyValue.setValue(converter.convert(propertyValue.getOriginalValue()));
                                existCorrespondConverter=true;
                            }
                        }
                    }
                    if((!paramTypes[index].isAssignableFrom(propertyValue.getValue().getClass()))&&!existCorrespondConverter){
                        return false;
                    }
                }
                return true;
            }else{
                return false;
            }
        }else{
            return false;
        }
    }

    @Override
    public boolean isTypeMatch(Method method, Object[] args) {
        return delegation.isTypeMatch(method,args);
    }

    @Override
    public boolean isTypeMatch(Constructor<?> constructor, Object[] args) {
        return delegation.isTypeMatch(constructor,args);
    }

    @Override
    public void setParamNameDiscover(ParamNameMethodDiscover discover) {
        this.delegation=discover;
    }

    @Override
    public boolean isConstructor(Method method) {
        return false;
    }

    @Override
    public boolean isAccess(Method method) {
        return method.isAccessible();
    }

    @Override
    public boolean isFinal(Method method) {
        return Modifier.isFinal(method.getModifiers());
    }

    @Override
    public boolean isOverriding(Method method) {
        return method.getAnnotation(Override.class)==null;
    }

    @Override
    public String getMethodName(Method method) {
        return method.getName();
    }




}
