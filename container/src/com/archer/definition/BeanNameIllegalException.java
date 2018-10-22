package archer.definition;/*
 *@author:wukang
 */

import archer.BeanException;

public class BeanNameIllegalException extends BeanException {

    public BeanNameIllegalException(String beanid, String msg) {
        super(beanid, msg);
    }
}
