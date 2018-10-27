package archer.container.support.convert;/*
 *@author:wukang
 */

import archer.container.BeanContainer;
import archer.container.Rankable;
import archer.container.support.TypeConverter;
import archer.container.support.debug.Description;
import java.util.*;

@Description(description = "类型转化器组合类，用来组合所有的类型转化器")
public class TypeConverterComposite implements TypeConverter {
    private static final  String DEFAULT_INTERNAL__CONVERTERS_LOCATION="./";
    private List<TypeConverter> typeConverters=new ArrayList<>(16);
    private TypeConverter currentConvertreFlag=null;
    public TypeConverterComposite(){
        loadInternalTypeConverters();
    }

    @Override
    public boolean support(Class from, Class to) {
        if(typeConverters!=null||!typeConverters.isEmpty()){
            for(TypeConverter typeConverter:typeConverters){
                if(typeConverter.support(from,to)){
                    currentConvertreFlag=typeConverter;
                    return true;
                }
            }
            return false;
        }
        return false;
    }



    @Override
    public Object convert(Object arg) {
        if(currentConvertreFlag==null){
            throw new RuntimeException("Please ensure the call timing (support; Convert), or provide the appropriate type converter");
        }else{
            return currentConvertreFlag.convert(arg);
        }
    }

    public void registerTypeConventer(TypeConverter converter){
        //todo
        if(!typeConverters.contains(converter)){
            typeConverters.add(converter);
        }
    }
    protected void sort(){
        if(typeConverters!=null||!typeConverters.isEmpty()){
            typeConverters.sort(new Comparator<TypeConverter>() {
                @Override
                public int compare(TypeConverter o1, TypeConverter o2) {
                    return o1.rank()-o2.rank();
                }
            });
        }
    }

    @Override
    public int rank() {
        return Rankable.HIGHEST_PRIORITY;
    }

    public void detectAllTypeConverter(BeanContainer container){
        List<TypeConverter> converters=container.getBeansOfSpecifiedType(TypeConverter.class);
        for(TypeConverter converter:converters){
            registerTypeConventer(converter);
        }
    }

    @Description(description = "蹩脚的设计，未来将替换成扫描当前包下的所有类并将其实例化添加到该类中")
    void loadInternalTypeConverters(){
        this.typeConverters.add(new Date2StringConverter());
        this.typeConverters.add(new String2DateConverter());
        this.typeConverters.add(new String2PrimitiveConverter());
        this.typeConverters.add(new String2ResourceConverter());
    }
}
