package archer.container.support;/*
 *@author:wukang
 */

import archer.container.BeanException;

/*
 * @Author wukang
 * @Description 当存在这种情况时:func(Super)和func(Sub)同时存在,其中Sub继承与Super，而给的参数值最终类型是Sub,
 * 根据java重载方法的选择是发生在编译时，此种情况下将抛出此异常，当前版本的处理是根据声明先后，优先选择最先能匹配的
 * @Date 2018-10-06 15:23
 * @Param
 * @return
 **/

public class NoUniqueMethodOrConstructorException extends BeanException {
    public NoUniqueMethodOrConstructorException(String beanid, String msg) {
        super(beanid, msg);
    }
}
