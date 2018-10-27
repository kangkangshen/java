package archer.container.definition;/*
 *@author:wukang
 */

import archer.container.BeanException;

public class BeanDefinitionValidationException extends BeanException {
    public BeanDefinitionValidationException(String beanid, String msg) {
        super(beanid, msg);
    }
}
