package archer.support;/*
 *@author:wukang
 */

import archer.BeanContainer;
import archer.BeanException;

public class BeanCreationException extends BeanException {
    private Throwable cause;
    private String msg;
    public BeanCreationException(String beanid, String msg) {
        super(beanid, msg);
    }

    public BeanCreationException(String beanId,Throwable ex){
        this.cause=ex;
    }
    public BeanCreationException(Throwable ex,String msg){
        this.msg=msg;
        this.cause=ex;
    }
}
