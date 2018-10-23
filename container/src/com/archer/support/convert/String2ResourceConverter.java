package archer.support.convert;/*
 *@author:wukang
 */

import archer.Rankable;
import archer.support.TypeConverter;
import archer.util.Resource;

public class String2ResourceConverter implements TypeConverter<String, Resource> {
    @Override
    public boolean support(Class<String> from, Class<Resource> to) {
        if(String.class.isAssignableFrom(from)&&Resource.class.isAssignableFrom(to)){
            return true;
        }
        return false;
    }

    @Override
    public Resource convert(String arg) {
        return new Resource(arg);
    }

    @Override
    public int rank() {
        return Rankable.MIDDLE_PRIORITY;
    }
}
