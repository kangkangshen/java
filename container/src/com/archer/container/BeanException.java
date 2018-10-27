package archer.container;/*
 *@author:wukang
 */


import org.apache.log4j.Logger;


public abstract class BeanException extends RuntimeException {
    protected String decription;
    private static Logger logger=Logger.getLogger("rootLogger");
    public BeanException(){}
    public BeanException(String beanid,String msg){
        super(beanid);
    }

    public void log(BeanException ex,String decription){
        if(logger.isDebugEnabled()){
            logger.debug(ex.getMessage()+" " +decription);
        }
    }

}
