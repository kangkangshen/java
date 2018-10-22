package archer.support;/*
 *@author:wukang
 */

import archer.support.debug.Description;

@Description(description = "In general, users should ensure that convert is invoked before support. ")
public interface TypeConverter<F,T> {
    boolean support(Class<F> from,Class<T> to);
    T convert(F arg);
}
