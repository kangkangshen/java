package archer.support;/*
 *@author:wukang
 */

import archer.BeanException;

public class BeanInstantiationException extends BeanException {
    public BeanInstantiationException(String beanid,String msg) {
        super(beanid,msg);
    }
}
