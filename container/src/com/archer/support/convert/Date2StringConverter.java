package archer.support.convert;/*
 *@author:wukang
 */

import archer.TypeConvertException;
import archer.support.TypeConverter;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Date2StringConverter implements TypeConverter<Date,String> {
    private static final DateFormat DEFAULT_DATEFORMTER=new SimpleDateFormat("yyyy-MM-dd:hh-mm-ss");
    private DateFormat format;
    private String pattern;
    @Override
    public boolean support(Class<Date> from, Class<String> to) {
        if(Date.class.isAssignableFrom(from)&&String.class.isAssignableFrom(to)){
            return true;
        }
        return false;
    }

    @Override
    public String convert(Date arg) {
        try{
        if(format!=null) {
            return format.format(arg);
        }else{
            return DEFAULT_DATEFORMTER.format(arg);
        }
        }catch (Exception e){
            throw new TypeConvertException(e,null );
        }
    }
    public void setPattern(String pattern){
        this.pattern=pattern;
        format=new SimpleDateFormat(pattern);
    }
    public void setFormat(DateFormat format){
        this.format=format;
    }
}
