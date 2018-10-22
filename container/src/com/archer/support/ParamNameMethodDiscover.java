package archer.support;/*
 *@author:wukang
 */

import archer.definition.ConstructorArgumentValues;
import archer.definition.OptionalPropertyValues;
import archer.support.debug.Description;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

public interface ParamNameMethodDiscover {

    void setCurrentlyAccessedClass(Class<?> type);

    String[] getMethodParamNames(String methodName);

    int getIndexOfParam(String methodName,String paramName);

    Class<?> getTypeOfParam(String methodName,String paramName);

    Class<?> getTypeOfParam(String methodName,int index);

    boolean isTypeMatch(String methodName,Object[]args);

    String[] getMethodParamNames(Method method);

    String[] getConstructorParamNames(Constructor constructor);

    int getIndexOfParam(Method method,String paramName);

    int getIndexOfParam(Constructor constructor,String paramName);

    Class<?> getTypeOfParam(Method method,String paramName);

    Class<?> getTypeOfParam(Method method,int index);

    Class<?> getTypeOfParam(Constructor c,int index);

    boolean isTypeMatch(Method method, OptionalPropertyValues methodParam);

    boolean isTypeMatch(Constructor constructor, ConstructorArgumentValues constructorArgs);

    boolean isTypeMatch(Method method,Object[]args);

    boolean isTypeMatch(Constructor<?> constructor,Object[] args);



}
