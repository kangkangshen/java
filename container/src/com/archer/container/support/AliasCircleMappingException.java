package archer.container.support;/*
 *@author:wukang
 */

public class AliasCircleMappingException extends RuntimeException {

    public AliasCircleMappingException(String msg){
        super(msg);
    }
    public AliasCircleMappingException(){
        super("There are cyclic mappings between bean name and alias");
    }

}
