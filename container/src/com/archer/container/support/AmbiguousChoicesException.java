package archer.container.support;/*
 *@author:wukang
 */


import org.apache.log4j.Logger;
import org.apache.log4j.Priority;

public class AmbiguousChoicesException extends RuntimeException {
    private Logger logger=Logger.getLogger(this.getClass());


    public AmbiguousChoicesException(String msg){
        super(msg);
        log(msg);
    }


    public void log(String msg){
        if(logger.isEnabledFor(Priority.ERROR)){
            logger.error(msg);
        }
    }


}
