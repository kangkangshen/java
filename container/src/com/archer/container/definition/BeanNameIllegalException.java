package archer.container.definition;/*
 *@author:wukang
 */

import archer.container.BeanException;

public class BeanNameIllegalException extends BeanException {

    public BeanNameIllegalException(String beanid, String msg) {
        super(beanid, msg);
    }
}
