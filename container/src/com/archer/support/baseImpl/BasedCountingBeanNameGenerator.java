package archer.support.baseImpl;/*
 *@author:wukang
 */

import archer.BeanNameGenerator;
import archer.PropertyValue;
import archer.definition.BeanDefinition;
import archer.support.BeanWrapper;
import archer.support.ReturnValuesDontMatchException;
import archer.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/*
基于计数的name生成器
 */
//todo
public class BasedCountingBeanNameGenerator implements BeanNameGenerator {
    private Properties nameProperties;
    public BasedCountingBeanNameGenerator(){
        this.nameProperties=new Properties();
    }
    @Override
    public String generateBeanName(BeanWrapper wrapper) {
        Class<?> beanClass=wrapper.getClass();
        String simpleClassName=beanClass.getSimpleName();
        return generateJavaBeanName(simpleClassName);
    }

    @Override
    public BeanNameGenerator getWrapedImpl() {
        return null;
    }

    @Override
    public String generateBeanName(BeanDefinition definition) {
        try {
            Class<?> beanClass=Class.forName(definition.getBeanClassName());
            String simpleClassName=beanClass.getSimpleName();
            return generateJavaBeanName(simpleClassName);
        } catch (ClassNotFoundException e) {
            throw new ReturnValuesDontMatchException("expected beanClass named\""+definition.getBeanClassName()+"\" is definied ,but not found ,check your class definition");
        }
    }
    private String generateJavaBeanName(String simpleClassName){
        if(nameProperties.containsKey(simpleClassName)){
            int oldIndex=Integer.parseInt(nameProperties.getProperty(simpleClassName));
            String name=simpleClassName+"$"+nameProperties.getProperty(simpleClassName)+oldIndex;
            nameProperties.putIfAbsent(simpleClassName, ++oldIndex);
            return StringUtils.decorate(name);
        }
        else{
            int index=0;
            nameProperties.put(simpleClassName,index);
            return StringUtils.decorate(simpleClassName);
        }
    }

}
