package archer.definition;/*
 *@author:wukang
 */

import archer.BeanException;

public class DuplicateRegisteredBeanException extends BeanException {
    public DuplicateRegisteredBeanException(String beanid, String msg) {
        super(beanid, msg);
    }
}
