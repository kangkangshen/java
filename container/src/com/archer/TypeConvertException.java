package archer;/*
 *@author:wukang
 */

public class TypeConvertException extends RuntimeException {
    private Throwable e;
    private String msg;
    public TypeConvertException(Throwable e,String msg){
        this.e=e;
        this.msg=msg;
    }

    @Override
    public synchronized Throwable getCause() {
        return e;
    }
}
