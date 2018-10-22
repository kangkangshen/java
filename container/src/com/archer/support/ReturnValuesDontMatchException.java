package archer.support;/*
 *@author:wukang
 */

import org.apache.log4j.Logger;

public class ReturnValuesDontMatchException extends RuntimeException {
    private Logger logger=Logger.getLogger(ReturnValuesDontMatchException.class);
    public ReturnValuesDontMatchException(String msg){
        super(msg);
        if(logger.isDebugEnabled()){
            logger.debug(msg);
        }
    }


}
