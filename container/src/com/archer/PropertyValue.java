package archer;
/*
 *@author:wukang
 */

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class PropertyValue {
    private Method method;
    private Field field;
    private String paramName;
    private Class paramType;
    private Object value;
    private Object originalValue;
    private Class beanType;
    private boolean isRequired=false;
    private boolean isResovled=false;
    private boolean allowConverted=true;
    private int index=-1;


    private PropertyValue(String paramName,int index,Object value,Method method,Field field,boolean isRequired,boolean isResovled) {
        this.paramName = paramName;
        this.originalValue=value;
        this.method=method;
        this.field=field;
        this.isRequired=isRequired;
        this.index=index;
    }

    //根据名称设置
    public PropertyValue(String paramName,Object value,boolean isRequired){
        this(paramName,-1,value,null,null,isRequired,false);
    }
    //根据序列设置
    public PropertyValue(int index,Object value,boolean isRequired){
        this(null,index,value,null,null,isRequired,false);
    }


    public PropertyValue(String paramName,Object value){
        this(paramName,-1,value,null,null,false,false);
    }


    public int getIndex() {
        return index;
    }

    public boolean isRequired() {
        return isRequired;
    }

    public boolean isResovled() {
        return isResovled;
    }

    public void setResovled(boolean resovled) {
        isResovled = resovled;
    }

    public Object getOriginalValue() {
        return originalValue;
    }

    public void setOriginalValue(Object originalValue) {
        this.originalValue = originalValue;
    }

    public Field getField() {
        return field;
    }

    public void setField(Field field) {
        this.field = field;
    }

    public Class getBeanType() {
        if(method!=null||field!=null){
            if(method!=null){
                beanType=method.getDeclaringClass();
            }else{
                beanType=field.getDeclaringClass();
            }
        }

        return beanType;
    }

    public void setBeanType(Class beanType) {
        this.beanType = beanType;
    }

    public String getParamName() {
        return paramName;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }
}
