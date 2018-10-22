package archer.support;/*
 *@author:wukang
 */

import java.lang.reflect.Method;

public interface MethodInfoAccessor {

    void setParamNameDiscover(ParamNameMethodDiscover discover);

    boolean isConstructor(Method method);

    boolean isAccess(Method method);

    boolean isFinal(Method method);

    boolean isOverriding(Method method);

    String getMethodName(Method method);

}
