package archer.container.support.convert;

import archer.container.Rankable;
import archer.container.support.TypeConverter;

public class String2PrimitiveConverter implements TypeConverter {
    private Class targetClass;
    private final Class[] PRIMITIVE_TYPES={byte.class,short.class,long.class,int.class,char.class,boolean.class,float.class,double.class};
    @Override
    public boolean support(Class from, Class to) {
        if(String.class.isAssignableFrom(from)){
            for(Class type:PRIMITIVE_TYPES){
                if(type.equals(to)){
                    targetClass=to;
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public Object convert(Object arg) {
        if(arg instanceof String){
            String s= (String) arg;
            if(targetClass.equals(int.class)){
                return Integer.parseInt(s);
            }else if(targetClass.equals(short.class)){
                return Short.parseShort(s);
            }else if(targetClass.equals(byte.class)){
                return Byte.parseByte(s);
            }else if(targetClass.equals(char.class)){
                if(s.length()==1){
                    return s.charAt(0);
                }else {
                    throw new IllegalArgumentException("if you want convert string to a char,please ensure string's length equals 1");
                }
            }else if(targetClass.equals(long.class)){
                return Long.parseLong(s);
            }else if(targetClass.equals(float.class)){
                return Float.parseFloat(s);
            }else if(targetClass.equals(boolean.class)){
                return Boolean.parseBoolean(s);
            }else if(targetClass.equals(double.class)){
                return Double.parseDouble(s);
            }else{
                throw new UnsupportedOperationException();
            }
        }else{
            throw new UnsupportedOperationException("arg must is a instance of String");
        }
    }

    @Override
    public int rank() {
        return Rankable.MIDDLE_PRIORITY;
    }
}
