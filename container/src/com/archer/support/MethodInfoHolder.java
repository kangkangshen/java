package archer.support;
/*
 *@author:wukang
 */

import java.lang.reflect.Method;

public class MethodInfoHolder implements MethodInfoAccessor {

    private ParamNameMethodDiscover discover;

    @Override
    public void setParamNameDiscover(ParamNameMethodDiscover discover) {
        if(discover!=null){
            this.discover=discover;
        }else{
            if(this.discover==null){
                this.discover=new DefaultParamNameDiscover();
            }
        }
    }

    @Override
    public boolean isConstructor(Method method) {
        return false;
    }

    @Override
    public boolean isAccess(Method method) {
        return false;
    }

    @Override
    public boolean isFinal(Method method) {
        return false;
    }

    @Override
    public boolean isOverriding(Method method) {
        return false;
    }


    @Override
    public String getMethodName(Method method) {
        return null;
    }

    public String[] getMethodParamNames(Method method) {
        return new String[0];
    }

}
