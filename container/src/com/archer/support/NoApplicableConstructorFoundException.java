package archer.support;/*
 *@author:wukang
 */

import archer.BeanException;

public class NoApplicableConstructorFoundException extends BeanException {
    public NoApplicableConstructorFoundException(String beanid, String msg) {
        super(beanid, msg);
    }
}
