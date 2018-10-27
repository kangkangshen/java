package archer.container.support;/*
 *@author:wukang
 */

public class NameAliasMappingChangedException extends RuntimeException {
    public NameAliasMappingChangedException(String msg){
        super(msg);
    }
    public NameAliasMappingChangedException(){
        super("Try to modify bean's name alias mapping,but not be allowed");
    }
}
