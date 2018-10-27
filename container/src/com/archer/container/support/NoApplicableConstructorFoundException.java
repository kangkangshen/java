package archer.container.support;/*
 *@author:wukang
 */

import archer.container.BeanException;

public class NoApplicableConstructorFoundException extends BeanException {
    public NoApplicableConstructorFoundException(String beanid, String msg) {
        super(beanid, msg);
    }
}
