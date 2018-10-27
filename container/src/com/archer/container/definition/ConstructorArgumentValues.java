package archer.container.definition;
import archer.container.PropertyValue;

import java.lang.reflect.Constructor;
import java.util.List;

/*
构造函数参数 通常表示必须值
 */
public class ConstructorArgumentValues {
    private Class beanClass;
    private List<PropertyValue> values;
    private Constructor targetConstructor;
    private ResovledBy resovledBy;
    public ConstructorArgumentValues(Class beanClass) {
        this.beanClass = beanClass;
    }
    public ConstructorArgumentValues(Class beanClass,List<PropertyValue> propertyValues){
        this.beanClass=beanClass;
        this.values=propertyValues;
    }

    public List<PropertyValue> getConstructorArgumentValues() {
        return values;
    }

    public boolean containsProperty(String name) {
        if (getPropertyValue(name) != null) {
            return true;
        } else {
            return false;
        }
    }

    public void updateProperty(String name, PropertyValue newValue) {
        PropertyValue oldValue = getPropertyValue(name);
        if (oldValue != null && oldValue != newValue) {
            values.remove(oldValue);
            values.add(newValue);
        }
    }

    public PropertyValue getPropertyValue(String name) {
        for (PropertyValue value : values) {
            if (value.getParamName().equals(name)) {
                return value;
            }
        }
        return null;
    }

    public ResovledBy getResovledBy() {
        return resovledBy;
    }

    public void setResovledBy(ResovledBy resovledBy) {
        this.resovledBy = resovledBy;
    }

    public Class getBeanClass(){
        return this.beanClass;
    }
}

