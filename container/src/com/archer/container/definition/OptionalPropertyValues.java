package archer.container.definition;

import archer.container.PropertyValue;

import java.util.List;

/*
用于表示setter方法所需要的属性值
 */
public class OptionalPropertyValues {
    private Class beanClass;
    private List<PropertyValue> values;
    public OptionalPropertyValues(Class beanClass,List<PropertyValue> propertyValues) {
        this.beanClass=beanClass;
        this.values=propertyValues;
    }
    public List<PropertyValue> getOptionalPropertyValues(){
        return values;
    }

    public boolean containsProperty(String name){
        if(getPropertyValue(name)!=null){
            return true;
        }else{
            return false;
        }
    }

    public void updateProperty(String name,PropertyValue newValue){
        PropertyValue oldValue=getPropertyValue(name);
        if(oldValue!=null&&oldValue!=newValue){
            values.remove(oldValue);
            values.add(newValue);
        }
    }

    public PropertyValue getPropertyValue(String name){
       for(PropertyValue value:values){
           if(value.getParamName().equals(name)){
               return value;
           }
       }
       return null;
    }

    public Class getBeanClass(){
        return this.beanClass;
    }




}
