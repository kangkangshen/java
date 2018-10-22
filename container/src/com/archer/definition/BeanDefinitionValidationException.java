package archer.definition;/*
 *@author:wukang
 */

import archer.BeanException;

public class BeanDefinitionValidationException extends BeanException {
    public BeanDefinitionValidationException(String beanid, String msg) {
        super(beanid, msg);
    }
}
