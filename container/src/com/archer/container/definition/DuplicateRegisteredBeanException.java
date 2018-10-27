package archer.container.definition;/*
 *@author:wukang
 */

import archer.container.BeanException;

public class DuplicateRegisteredBeanException extends BeanException {
    public DuplicateRegisteredBeanException(String beanid, String msg) {
        super(beanid, msg);
    }
}
