package archer.container.util;/*
 *@author:wukang
 */

import archer.container.support.DefaultParamNameDiscover;
import archer.container.support.ParamNameMethodDiscover;
import archer.container.support.ReturnValuesDontMatchException;
import archer.container.support.debug.Description;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

public class ObjectUtils {


    private ObjectUtils(){}

    public static void setter(Object bean,String field,Object val){
        try{
            if(!field.contains(".")){
                try{
                    Method setter=findSetter(bean,field);
                    if(isInstanceOf(val,setter.getParameterTypes()[0])) {
                        setter.invoke(bean, val);
                    }
                }catch (NoSuchFieldException|NoSuchMethodException| InvocationTargetException e){
                    throw new UnexpectedDuringInjectionException(null,"failed inject,check your class definition and our inject roles",e);
                }
            }else{
                Object beanField=bean.getClass().getDeclaredField(field).get(bean);
                field=field.substring(field.indexOf('.'));
                setter(beanField,field,val);
            }
        }catch (NoSuchFieldException|IllegalAccessException e){
            if(e instanceof NoSuchFieldException){
                throw new UnexpectedDuringInjectionException(null,"The bean does not contain a field named \""+field+"\"",e);
            }
            if(e instanceof IllegalAccessException){
                throw new UnexpectedDuringInjectionException(null,"Unsupport to inject field with unpublicly",e);
            }
        }


    }

    public static Object getter(Object bean,String field){
        return null;
    }

    public static boolean isInstanceOf(Object bean,Class<?> type){
        Class<?> beanType =bean.getClass();
        return type.isAssignableFrom(beanType);
    }

    private static Method findGetter(Object bean,String field){
        return null;
    }

    private static Method findSetter(Object bean,final String fieldName) throws NoSuchFieldException, NoSuchMethodException {
        ParamNameMethodDiscover discover=new DefaultParamNameDiscover();
        //check field exists
        Class<?> type=bean.getClass();
        Field field=type.getDeclaredField(fieldName);
        Method[] methods=type.getMethods();
        Method setter=null;
        for(Method method:methods){
            if(method.getName().startsWith("set")){
                String[] paramNames=discover.getMethodParamNames(method);
                if(paramNames.length==1&&paramNames[0].equals(fieldName)&&method.getReturnType().equals(void.class)){
                    setter=method;
                    break;
                }
            }
        }
        if(setter!=null){
            return setter;
        }else{
            throw new NoSuchMethodException();
        }

    }

    @Description(description = "返回当前方法的定义的接口，假设当前是由接口定义的，当该方法未被接口定义时，返回")
    public static Class getDefinedInterface(Method method){
        Class declaring=method.getDeclaringClass();
        if(declaring.isInterface()){
            Method[] methods=declaring.getMethods();
            for(Method method1:methods){
                if(isMatch(method1,method)){
                    return declaring;
                }
            }
        }else{
            Class[] superClazz=declaring.getInterfaces();
            for(Class type:superClazz){
                Class defineInterface=getDefinedInterface(method);
                if(defineInterface!=null){
                    return defineInterface;
                }
            }
        }
        return null;

    }

    public static boolean isMatch(Method var1,Method var2){
        if(var1!=null&&var2!=null){
            if(var1==var2){
                return true;
            }

            if(var1.getName().equals(var2.getName())&&var1.getReturnType().equals(var2.getReturnType())&&isArrayEquals(var1.getParameterTypes(),var2.getParameterTypes())){
                return true;
            }
        }

        if(var1==null&&var2==null){
            return true;
        }

        return false;
    }

    public static boolean isArrayEquals(Object[] var1,Object[] var2) {
        if (var1 == var2) {
            return true;
        }
        if (var1 != null && var2 != null && var1.length == var2.length) {
            int length = var1.length;
            for (int i = 0; i < length; i++) {
                if (!var1[i].equals(var2[i])) {
                    return false;
                }
            }
            return true;
        }

        return false;
    }

    @Description(description = "返回declaring类里面声明的唯一的一个方法")
    public static Method getMethodIfUnique(String methodName,Class<?> declaring){
        Method[] methods=declaring.getMethods();
        if(methods==null){
            throw new ReturnValuesDontMatchException("The method named name was not found");
        }
        Method reValue=null;
        int methodCount=0;
        for(Method method:methods){
            if(method.getName().equals(methodName)){
                reValue=method;
                methodCount++;
            }
        }
        if(methodCount!=1){
            throw new ReturnValuesDontMatchException("The expected number of methods is 1, now more than 1");
        }
        if(reValue==null){
            throw new ReturnValuesDontMatchException("The method named name was not found");
        }
        return reValue;
    }

    public  static <T>  T[] toArray(List<T> list){
        T[] array= (T[]) new Object[list.size()];
        for(int i=0;i<list.size();i++){
            array[i]=list.get(i);
        }
        return array;
    }

    public Object mustChooseOne(Object var1,Object var2){
        if(var1!=null){
            return var1;
        }
        if(var2!=null){
            return var2;
        }
        if(var1==null&&var2!=null){
            throw new IllegalArgumentException("Var1 and var2 must not be empty at the same time");
        }
        return null;
    }

    public Object chooseOne(Object var1,Object var2,Object alternative){
        return null;
    }


}