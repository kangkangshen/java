package archer.container.support;/*
 *@author:wukang
 */

import archer.container.BeanException;

public class NoSuchBeanException extends BeanException {
    public NoSuchBeanException(String beanId){
        super(beanId,"The bean definition named name was not found");
    }
}
