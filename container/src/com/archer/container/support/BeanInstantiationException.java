package archer.container.support;/*
 *@author:wukang
 */

import archer.container.BeanException;

public class BeanInstantiationException extends BeanException {
    public BeanInstantiationException(String beanid,String msg) {
        super(beanid,msg);
    }
}
