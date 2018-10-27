package archer.container.support;
/*
 *@author:wukang
 * 使用asm实现
 */
import archer.container.PropertyValue;
import archer.container.definition.ConstructorArgumentValues;
import archer.container.definition.OptionalPropertyValues;
import archer.container.support.debug.Description;
import archer.container.util.ArrayUtils;
import archer.container.util.Assert;
import archer.container.util.ObjectUtils;
import org.objectweb.asm.*;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.LocalVariableNode;
import org.objectweb.asm.tree.MethodNode;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;

@Description(description = "remind you ,this class do not support get method param names from jdk! all tested.")
public final class DefaultParamNameDiscover implements ParamNameMethodDiscover {
    private static final String[] BYTECODE_PRIMITIVE_TYPES={"I","C","B","Z","F","D","J","S"};
    private static final String[] JAVA_PRIMITIVE_TYPE_NAMES={"int","char","byte","boolean","float","double","long","short"};
    private static final Class[] JAVA_PRIMITIVE_TYPES={int.class,char.class,byte.class,boolean.class,float.class,double.class,long.class,short.class};
    private static final Class[] JAVA_PRIMITIVE_BOXED_TYPES={Integer.class,Character.class,Byte.class,Boolean.class, Float.class,Double.class,Long.class,Short.class};
    private static final String BYTECODE_REFERENCE_PREFIX="L";
    private static final String BYTECODE_ARRAY_PREFIX="[";
    private Class<?> type;
    private Map<Constructor,ConstructorArgumentValues> cstCache;
    private Map<Method,OptionalPropertyValues> metCache;
    public DefaultParamNameDiscover(){
        this.cstCache=new HashMap<>();
        this.metCache=new HashMap<>();
    }

    @Override
    public void setCurrentlyAccessedClass(Class<?> type) {
        this.type=type;
    }

    @Override
    public String[] getMethodParamNames(String methodName) {
        if(type==null){
            throw new IllegalStateException("The current class is not set");
        }
        Method method=ObjectUtils.getMethodIfUnique(methodName,type);
        return getMethodParamNames(method);
    }

    @Override
    public int getIndexOfParam(String methodName, String paramName) {
        if(type==null){
            throw new IllegalStateException("The current class is not set");
        }
        Method method=ObjectUtils.getMethodIfUnique(methodName,type);
        return getIndexOfParam(method,paramName);
    }

    @Override
    public Class<?> getTypeOfParam(String methodName, String paramName) {
        if(type==null){
            throw new IllegalStateException("The current class is not set");
        }
        Method method=ObjectUtils.getMethodIfUnique(methodName,type);
        return getTypeOfParam(method,paramName);
    }

    @Override
    public Class<?> getTypeOfParam(String methodName, int index) {
        if(type==null){
            throw new IllegalStateException("The current class is not set");
        }
        Method method=ObjectUtils.getMethodIfUnique(methodName,type);
        return getTypeOfParam(method,index);
    }

    @Override
    public boolean isTypeMatch(String methodName, Object[] args) {
        if(type==null){
            throw new IllegalStateException("The current class is not set");
        }
        Method method=ObjectUtils.getMethodIfUnique(methodName,type);
        return isTypeMatch(method,args);
    }

    private boolean sameType(Type[] types, Class<?>[] clazzes) {
        if (types.length != clazzes.length) {
            return false;
        }
        for (int i = 0; i < types.length; i++) {
            if (!Type.getType(clazzes[i]).equals(types[i])) {
                return false;
            }
        }
        return true;
    }

    @Override
    public String[] getMethodParamNames(final Method m) {
        MethodNode methodNode=getMethodNodeMatched(m);
        String[] params=new String[m.getParameterCount()];
        List<LocalVariableNode> lvNodes=methodNode.localVariables;
        if(Modifier.isStatic(m.getModifiers())){
            for(int i=0;i<m.getParameterCount();i++){
                params[i]=lvNodes.get(i).name;
            }
        }else{
            for(int i=0;i<m.getParameterCount();i++){
                params[i]=lvNodes.get(i+1).name;
            }
        }

        return params;
    }

    @Override
    public String[] getConstructorParamNames(final Constructor c) {
        Assert.notNull(c,"Param constructor must be not null");
        MethodNode methodNode=getMethodNodeMatched(c);
        String[] params=new String[c.getParameterCount()];
        List<LocalVariableNode> lvNodes=methodNode.localVariables;
        for(int i=0;i<c.getParameterCount();i++){
            params[i]=lvNodes.get(i).name;
        }
        return params;
    }

    @Description(description = "返回 paramName 在该方法第一次出现时的位置下标，若未出现，返回-1")
    @Override
    public int getIndexOfParam(Method method, String paramName) {
        String[] names=getMethodParamNames(method);
        for(int i=0;i<names.length; i++){
            if(names[i].equals(paramName)){
                return i;
            }
        }
        return -1;
    }

    @Override
    public int getIndexOfParam(Constructor constructor, String paramName){
        MethodNode matchedNode=getMethodNodeMatched(constructor);
        if(matchedNode!=null){
            List<LocalVariableNode> lvNodes=matchedNode.localVariables;
            for(LocalVariableNode lvNode:lvNodes){
                if(lvNode.name.equals(paramName)){
                    return lvNode.index-1;
                }
            }
        }
        return -1;

    }

    @Override
    public Class<?> getTypeOfParam(Method method, String paramName) {
        int index=getIndexOfParam(method,paramName);
        if(index!=-1){
            return method.getParameterTypes()[index];
        }
        return null;
    }

    @Override
    public Class<?> getTypeOfParam(Method method, int index) {
        if(index<method.getParameterCount()) {
            return method.getParameterTypes()[index];
        }
        return null;
    }

    @Override
    public Class<?> getTypeOfParam(Constructor c, int index) {
        if(index<c.getParameterCount()&&index>0){
            return c.getParameterTypes()[index];
        }
        return null;
    }

    @Override
    public boolean isTypeMatch(Method method, OptionalPropertyValues methodParam) {
        List<PropertyValue> propertyValues=methodParam.getOptionalPropertyValues();
        Class<?>[] paramTypes=method.getParameterTypes();
        if(paramTypes.length==propertyValues.size()) {
            for (PropertyValue propertyValue : propertyValues) {
                if (propertyValue.getIndex() != -1) {
                    int index = propertyValue.getIndex();
                    if (!propertyValue.getOriginalValue().getClass().isAssignableFrom(paramTypes[index])) {
                        return false;
                    }
                } else {
                    String paramName = propertyValue.getParamName();
                    int index = getIndexOfParam(method, paramName);
                    if (index != -1) {
                        if (!paramTypes[index].isAssignableFrom(propertyValue.getOriginalValue().getClass())) {
                            return false;
                        }
                    } else {
                        return false;
                    }

                }
            }
            return true;
        }else{
            return false;
        }
    }

    @Override
    public boolean isTypeMatch(Constructor constructor, ConstructorArgumentValues constructorArgs) {
        if(constructor.getDeclaringClass().equals(constructorArgs.getBeanClass())){
            List<PropertyValue> propertyValues=constructorArgs.getConstructorArgumentValues();
            Class<?>[] paramTypes=constructor.getParameterTypes();
            if(propertyValues.size()==paramTypes.length){
                for(PropertyValue propertyValue:propertyValues){
                    if(propertyValue.getIndex()!=-1){
                        int index=propertyValue.getIndex();
                        if(!propertyValue.getOriginalValue().getClass().isAssignableFrom(paramTypes[index])){
                            return false;
                        }
                    }else{
                        String paramName=propertyValue.getParamName();
                        int index=getIndexOfParam(constructor,paramName);
                        if(index!=-1){
                            if(!paramTypes[index].isAssignableFrom(propertyValue.getOriginalValue().getClass())){
                                return false;
                            }
                        }else{
                            return false;
                        }
                    }
                }
                return true;
            }else{
                return false;
            }
        }else{
            return false;
        }
    }

    @Override
    public boolean isTypeMatch(Method method, Object[] args) {
        Class<?>[] paramTypes=method.getParameterTypes();
        if(paramTypes.length==args.length){
            for(int i=0;i<args.length;i++){
                if(!paramTypes[i].isAssignableFrom(args[i].getClass())){
                    return false;
                }
            }
            return true;
        }else{
            return false;
        }
    }

    @Override
    public boolean isTypeMatch(Constructor<?> constructor,Object[] args){
        if(constructor.getParameterCount()!=args.length){
            return false;
        }
        Class<?>[] cstParamTypes=constructor.getParameterTypes();
        for(int i=0;i<args.length;i++){
            if(!cstParamTypes[i].isAssignableFrom(args[i].getClass())){
               return false;
            }
        }
        return true;

    }

    public List<MethodNode> getMethodNodes(Class<?> type){
        ClassReader reader= null;
        try {
            reader = new ClassReader(type.getName());
        } catch (IOException e) {
            e.printStackTrace();
        }
        ClassNode classNode=new ClassNode();
        reader.accept(classNode,ClassReader.EXPAND_FRAMES);
        return classNode.methods;
    }

    protected void clearCacheInternal(){
        if(cstCache!=null){
            cstCache.clear();
            cstCache=null;
        }
        if(metCache!=null){
            metCache.clear();
            metCache=null;
        }
    }

    private MethodNode getMethodNodeMatched(Constructor c){
        List<MethodNode> methodNodes=getMethodNodes(c.getDeclaringClass());
        Class[] paramTypesOfMethod=c.getParameterTypes();
        for(MethodNode methodNode:methodNodes){
            if(methodNode.name.equals("<init>")) {
                List methodNodeDescResult=decorateMethodNodeDesc(methodNode.desc);
                methodNodeDescResult.remove(methodNodeDescResult.size()-1);
                List paramTypesOfMethodNode=methodNodeDescResult;
                if(Arrays.equals(paramTypesOfMethod,paramTypesOfMethodNode.toArray())){
                    return methodNode;
                }
            }
        }
        return null;
    }

    private MethodNode getMethodNodeMatched(Method m){
        List<MethodNode> methodNodes=getMethodNodes(m.getDeclaringClass());
        Class[] paramTypesOfMethod=m.getParameterTypes();
        for(MethodNode methodNode:methodNodes){
            if(methodNode.name.equals(m.getName())) {
                List methodNodeDescResult=decorateMethodNodeDesc(methodNode.desc);
                methodNodeDescResult.remove(methodNodeDescResult.size()-1);
                List paramTypesOfMethodNode=methodNodeDescResult;
                if(Arrays.equals(paramTypesOfMethod,paramTypesOfMethodNode.toArray())){
                    return methodNode;
                }
            }
        }
        return null;
    }

    @Description(description = "将字节码类型名称转化成相对应的java类型名称，当查找失败时，返回null")
    private String decorateByteCodeType(String byteCodeType){
        if(byteCodeType==null||byteCodeType.trim().equals("")){
            return null;
        }
        String prefix=byteCodeType.trim().substring(0,1);
        if(ArrayUtils.belongTo(BYTECODE_PRIMITIVE_TYPES,prefix)){
            for(int i=0;i<BYTECODE_PRIMITIVE_TYPES.length;i++){
                if(prefix.equals(BYTECODE_PRIMITIVE_TYPES[i])){
                    return JAVA_PRIMITIVE_TYPE_NAMES[i];
                }
            }
        }else if(BYTECODE_REFERENCE_PREFIX.equals(prefix)){
                return (byteCodeType.substring(1)).replaceAll("/",".").replace(";","");
        }else if(BYTECODE_ARRAY_PREFIX.equals(prefix)){
                return byteCodeType.replaceAll("/",".");
        }else{
            return null;
        }
        return null;
    }

    private List<Class<?>> decorateMethodNodeDesc(String desc)  {
        List<Class<?>> paramAndRetList=new ArrayList<>();
        int begin=desc.indexOf('(');
        int end=desc.indexOf(')');
        String params=desc.substring(begin+1,end);
        String ret=desc.substring(end+1);
        int index=0;
        while(index<params.length()){
            if(ArrayUtils.belongTo(BYTECODE_PRIMITIVE_TYPES,params.charAt(index)+"")){
                String primType=decorateByteCodeType(params.charAt(index)+"");
                paramAndRetList.add(getClassOfPrimitiveType(primType));
                index++;
            }else if(params.charAt(index)=='['){
                int arrayTypeBegin=index;
                while(params.charAt(index)!=';'){
                    index++;
                }
                int arrayTypeEnd=index+1;
                try{
                    paramAndRetList.add(Class.forName(decorateByteCodeType(params.substring(arrayTypeBegin, arrayTypeEnd))));
                }catch (ClassNotFoundException e){
                    e.printStackTrace();
                }

                index++;
            }else if(params.charAt(index)=='L'){
                int refTypeBegin=index;
                while(params.charAt(index)!=';'){
                    index++;
                }
                int refTypeEnd=index;
                try{
                    paramAndRetList.add(Class.forName(decorateByteCodeType(params.substring(refTypeBegin,refTypeEnd))));
                }catch (ClassNotFoundException e){
                    e.printStackTrace();
                }

                index++;
            }else{
                //todo
            }
        }
        if(ret.equals("V")){
            //无返回值
            paramAndRetList.add(void.class);
        }else{
            try {
                paramAndRetList.add(Class.forName(decorateByteCodeType(ret)));
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }

        return paramAndRetList;
    }

    private Class getClassOfPrimitiveType(String javaOrBytecodePrimType) {
        int index=ArrayUtils.getIndexOf(BYTECODE_PRIMITIVE_TYPES,javaOrBytecodePrimType);
        if(index!=-1){
             return JAVA_PRIMITIVE_TYPES[index];
        }else{
            index=ArrayUtils.getIndexOf(JAVA_PRIMITIVE_TYPE_NAMES,javaOrBytecodePrimType);
            if(index!=-1){
                return JAVA_PRIMITIVE_TYPES[index];
            }
            else{
                throw new IllegalArgumentException("func arg must be java or byte code primitive type name");
            }
        }
    }
}
