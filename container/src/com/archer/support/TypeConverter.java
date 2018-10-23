package archer.support;/*
 *@author:wukang
 */

import archer.Rankable;
import archer.support.debug.Description;

@Description(description = "In general, users should ensure that convert() is invoked before support(). ")
public interface TypeConverter<F,T> extends Rankable {
    boolean support(Class<F> from,Class<T> to);
    T convert(F arg);
}
