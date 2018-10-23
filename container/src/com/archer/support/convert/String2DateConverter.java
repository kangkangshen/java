package archer.support.convert;/*
 *@author:wukang
 */

import archer.Rankable;
import archer.support.TypeConvertException;
import archer.support.TypeConverter;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class String2DateConverter implements TypeConverter<String, Date> {
    private static final DateFormat DEFAULT_DATEFORMTER=new SimpleDateFormat("yyyy-MM-dd:hh-mm-ss");
    private DateFormat format;
    private String pattern;

    @Override
    public boolean support(Class<String> from, Class<Date> to) {
        if(String.class.isAssignableFrom(from)&&Date.class.isAssignableFrom(to)){
            return true;
        }
        return false;
    }

    @Override
    public Date convert(String arg) {
        try{
            if(format!=null){
                return format.parse(arg);
            }else{
                return DEFAULT_DATEFORMTER.parse(arg);
            }
        }catch (ParseException e){
            throw new TypeConvertException(e,null);
        }

    }

    public void setPattern(String pattern) {
        this.pattern = pattern;
        if(format==null){
            format=new SimpleDateFormat(pattern);
        }else{
            throw new UnsupportedOperationException();
        }
    }

    public void setFormat(DateFormat format) {
        this.format = format;
    }

    @Override
    public int rank() {
        return Rankable.MIDDLE_PRIORITY;
    }
}
