package archer.container.util;/*
 *@author:wukang
 */

import archer.container.BeanException;

public class UnexpectedDuringInjectionException extends BeanException {

    public UnexpectedDuringInjectionException(String beanid,String msg,Exception ex){
        super(beanid,msg);
        log(this,msg);
        System.err.println(msg);
    }
}
