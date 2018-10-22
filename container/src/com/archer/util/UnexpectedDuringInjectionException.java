package archer.util;/*
 *@author:wukang
 */

import archer.BeanException;

public class UnexpectedDuringInjectionException extends BeanException {

    public UnexpectedDuringInjectionException(String beanid,String msg,Exception ex){
        super(beanid,msg);
        log(this,msg);
        System.err.println(msg);
    }
}
