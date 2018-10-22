package archer.util;/*
 *@author:wukang
 */

import archer.PropertyValue;
import archer.definition.ConstructorArgumentValues;
import archer.definition.OptionalPropertyValues;
import archer.definition.ResovledBy;
import archer.support.BeanCreationException;
import archer.support.MethodAccessor;
import org.apache.log4j.Logger;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;


public abstract class BeanUtils {
    private static final Logger logger = Logger.getLogger(BeanUtils.class);

    public BeanUtils() {
    }
    public static boolean hasAnyAccessableConstructor(Class<?> type){
        Constructor[] constructors=type.getConstructors();
        if(constructors==null){
            return false;
        }
        return true;
    }
    /*
    values must be match the constructor
     */
    public static Object invokeConstructor(Constructor constructor, ConstructorArgumentValues values) throws BeanCreationException {
        MethodAccessor methodAccessor=MethodAccessor.getDefault();
        Assert.notNull(constructor,"constructor must be not null");
        Assert.notNull(values,"constructor params must be not null");
        int paramCount=constructor.getParameterCount();
        Object[] args=new Object[paramCount];
        for(PropertyValue propertyValue:values.getConstructorArgumentValues()){
            int index=propertyValue.getIndex();
            args[index]=propertyValue.getValue();
        }

        try {
            return constructor.newInstance(args);
        } catch (InstantiationException e) {
            throw new BeanCreationException("","");
        } catch (IllegalAccessException e) {
            ;
        } catch (InvocationTargetException e) {
            throw new BeanCreationException("","");
        }
        return null;
    }
    public static Object invokeMethod(Method method, OptionalPropertyValues values) throws RuntimeException{
        return null;
    }
    //使用public的无参构造器进行实例化
    public static <T> T instantiate(Class<?> beanClass){
        try {
            Constructor constructor=beanClass.getConstructor(null);
            return (T)constructor.newInstance(null);
        } catch (NoSuchMethodException e) {
            throw new BeanCreationException(e,"There is no public no-argument constructor in \""+beanClass.getName()+"\"");
        } catch (IllegalAccessException e) {
            ;
        } catch (InstantiationException e) {
            throw new BeanCreationException(e,"An exception unanticipated occur");
        } catch (InvocationTargetException e) {
            throw new BeanCreationException(e,"An exception unanticipated occur");
        }
        return null;
    }
    public static <T> T instantiate(String beanClassName){
        Class<?> beanClass= null;
        try {
            beanClass = Class.forName(beanClassName);
        } catch (ClassNotFoundException e) {
            throw new BeanCreationException(e,"The class \""+beanClassName+"\"is not found. Check the class name and consider resetting the classloader");
        }
        return instantiate(beanClass);
    }



}
