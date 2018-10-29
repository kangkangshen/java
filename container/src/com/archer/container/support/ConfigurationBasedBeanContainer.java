package archer.container.support;/*
 *@author:wukang
 */

import archer.container.PropertyValue;
import archer.container.config.Configuration;
import archer.container.config.XmlReader;
import archer.container.definition.*;
import archer.container.support.convert.TypeConverterComposite;
import archer.container.support.debug.Description;
import archer.container.support.ext.ExtensibleBeanContainer;
import archer.container.util.Assert;
import archer.container.util.BeanUtils;
import archer.container.util.EncodeResource;
import org.dom4j.Document;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

public class ConfigurationBasedBeanContainer extends AbstractBeanContainer implements ExtensibleBeanContainer, HierarchicalBeanContainer, ConfigurableBeanContainer, BeanDefinitionManager {
    private XmlReader reader;
    private Map<String, BeanDefinition> definitionMap;

    public ConfigurationBasedBeanContainer(String configLocation) {
        super(configLocation);
    }

    public ConfigurationBasedBeanContainer(String configLocation,String containerId){
        super(configLocation,containerId);
    }

    @Override
    protected void parseAndInitInternal(){
        EncodeResource resource=new EncodeResource(configLocation);
        if(reader==null) {
            reader=new XmlReader(this);
        }
        Document document=reader.loadDocument(resource);
        List<BeanDefinition> definitions=reader.parse(document,this);
        for(BeanDefinition definition:definitions){
            registerBeanDefinition(definition,definition.getId());
        }

    }

    @Description(description = "将相应的#ref转化成相应的bean,并验证definition中定义的methodOverriding合法性")
    private BeanDefinition getPreparedLocalBeanDefinition(String beanId) {
        BaseBeanDefinitionHolder definition= (BaseBeanDefinitionHolder) definitionMap.get(beanId);
        if(definition!=null){
            if(!definition.isResovled()){
                ConstructorArgumentValues cstValues=definition.getConstructorArgumentValues();
                List<PropertyValue> cstPropVals=cstValues.getConstructorArgumentValues();
                for(PropertyValue propertyValue:cstPropVals){
                    //此处必须保证相应的调用时序，否则将会发生java.lang.ClassCastException
                    String originalValue= (String) propertyValue.getOriginalValue();
                    if(originalValue.startsWith("#")){
                        String beanName=originalValue.substring(originalValue.charAt('#'));
                        Object value=this.getBean(beanName);
                        propertyValue.setValue(value);
                    }
                }
                OptionalPropertyValues opValues=definition.getOptionalPropertyValues();
                List<PropertyValue> opPropVals=cstValues.getConstructorArgumentValues();
                for(PropertyValue propertyValue:cstPropVals){
                    //此处必须保证相应的调用时序，否则将会发生java.lang.ClassCastException
                    String originalValue= (String) propertyValue.getOriginalValue();
                    if(originalValue.startsWith("#")){
                        String beanName=originalValue.substring(originalValue.charAt('#'));
                        Object value=this.getBean(beanName);
                        propertyValue.setValue(value);
                    }else{
                        propertyValue.setValue(originalValue);
                    }
                }
                //验证相应的methodOverrides
                definition.validateMethodOverrides();
            }
            definition.setResovled(true);
        }
        return definition;

    }

    @Override
    public List<String> getBeanNames() {
        return null;
    }

    public List<BeanDefinition> getBeanDefinitions() {
        if(!definitionMap.isEmpty()){
            if(logger.isDebugEnabled()){
                logger.debug("No fetch any bean definition in your configuration files");
            }
            return null;
        }else{
            List<BeanDefinition> beanDefinitions=new ArrayList<>(definitionMap.size());
            beanDefinitions.addAll(definitionMap.values());
            return beanDefinitions;
        }
    }

    public List<BeanDefinition> getBeanDefinition(Class<?> type) {
        List<BeanDefinition> definitions=new ArrayList<>();
        for(BeanDefinition definition:this.definitionMap.values()){
            if(type.isAssignableFrom(definition.getBeanClass())){
                definitions.add(definition);
            }
        }
        return definitions;
    }

    @Override
    public boolean instantiable(BeanDefinition beanDefinition) {
        String className=beanDefinition.getBeanClassName();
        Class<?> type=beanDefinition.getBeanClass();
        try {
            if(this.getClass().getClassLoader().loadClass(className)!=type){
                return false;
            }
            if(type.isInterface()||type.isAnnotation()|| Modifier.isAbstract(type.getModifiers())){
                //如果定义的bean的class是一个接口，注解，抽象的由类定义引起的问题
                return false;
            }
            if(!BeanUtils.hasAnyAccessableConstructor(type)){
                return false;
            }

        } catch (ClassNotFoundException e){
            if(logger.isDebugEnabled()){
                logger.debug("The specified class with name \""+className+" was not found by the current classloader");
            }
            return false;
        }
        return true;
    }

    @Override
    public boolean isLazyInit(BeanDefinition definition) {
        return false;
    }

    @Override
    public void removeBeanDefinition(String beanName) throws NoSuchBeanDefinitionException{
        if(!containsBeanDefinition(beanName)){
            throw new NoSuchBeanDefinitionException();
        }else{
            String beanid=getBeanIdfromName(beanName);
            definitionMap.remove(beanid);
        }
    }

    @Override
    public boolean containsBeanDefinition(String var1) {
        return false;
    }

    @Override
    protected OptionalPropertyValues patchBeanDefinition(String beanId, Class<?> beanType) {
        BeanDefinition definition=getBeanDefinition(beanId);
        return definition.getPropertyValues();
    }

    @Override
    public String[] getBeanDefinitionNames() {
        return (String[]) definitionMap.keySet().toArray();
    }

    @Override
    public int getBeanDefinitionCount() {
        return this.definitionMap.size();
    }

    @Override
    public boolean isBeanNameInUse(String name){
        if(super.isBeanNameInUse(name)){
            return true;
        }else{
            if(this.definitionMap.containsValue(name)){
                return true;
            }
        }
        return false;
    }

    @Override
    public String getBeanIdfromName(String name){
        String beanName=formalizeBeanName(name);
        String beanId=super.getBeanIdfromName(name);
        if(beanId==null){
            if(this.definitionMap.containsKey(beanName)){
                beanId=beanName;
            }
        }
        return beanId;
    }

    @Override
    public void registerBeanDefinition(BeanDefinition bean, String beanId) {
        if(definitionMap==null){
            definitionMap=new ConcurrentHashMap<>(16);
        }
            //检查重复注册
            boolean isDuplicateregistered = false;

            if (isBeanNameInUse(formalizeBeanName(beanId))) {
                isDuplicateregistered = true;
            }
            if (!isDuplicateregistered) {
                //未发生重复注册
                this.definitionMap.put(beanId, bean);
                this.objectMap.remove(beanId);
                this.exposureObjectMap.remove(beanId);
            } else {
                //发生重复注册bean
                if (isAllowBeanDefinitionOverriding()) {
                    //该容器允许bean覆盖
                    if (bean.isOverriding()) {
                        //bean本身要求覆盖
                        if (logger.isDebugEnabled()) {
                            logger.debug("The bean has been repeatedly registered, " +
                                    "the container allows the bean to be overwritten, " +
                                    "and the bean named \"" + beanId + "\" itself requires coverage.");
                        }
                        this.definitionMap.put(beanId, bean);
                        this.objectMap.remove(beanId);
                        this.exposureObjectMap.remove(beanId);
                    } else {
                        //bean本身要求跳过
                        if (logger.isDebugEnabled()) {
                            logger.debug("The bean is repeatedly registered," +
                                    " the container allows the bean to be overwritten," +
                                    " and the bean named \"" + beanId + "\"itself asks to skip");
                        }
                    }

                } else {
                    if (bean.isOverriding()) {
                        throw new DuplicateRegisteredBeanException(beanId,
                                "The bean has been repeatedly registered, " +
                                        "the container does not allow the bean to be overwritten, " +
                                        "but the bean itself requires coverage.");
                    }
                }
            }


    }

    @Override
    public BeanDefinition getBeanDefinition(String beanName) {
        String name=formalizeBeanName(beanName);
        String beanId=getBeanIdfromName(name);
        if(definitionMap.containsKey(beanId)){
            return definitionMap.get(beanId);
        }else{
            if(logger.isDebugEnabled()){
                logger.debug("There is no such bean definition with name \""+beanName+"\" , check your bean configurations");
            }
            return null;
        }
    }
    @Override
    protected void loadBeansEagerly(){

    }

    @Override
    protected void actionsAfterLoading(){

    }

    @Override
    public boolean isSingleton(String name) {
        BeanDefinition beanDefinition=definitionMap.get(formalizeBeanName(name));
        if(beanDefinition!=null){
            return beanDefinition.isSingleton();
        }
        return false;
    }

    @Override
    public boolean isPrototype(String name) {
        BeanDefinition beanDefinition=definitionMap.get(formalizeBeanName(name));
        if(beanDefinition!=null){
            return beanDefinition.isPrototype();
        }
        return false;
    }

    @Override
    protected <T> List<T> resovleRawBeanDefinitions(Class<T> type) {
        return null;
    }

    @Override
    protected String[] getDependsOn(String beanId) {
        BeanDefinition definition=getBeanDefinition(beanId);
        return definition.getDependsOn();
    }


    @Override
    protected boolean hasFactoryMethod(String beanId, Class beanType) {
        return false;
    }

    @Override
    protected Method findAppropriateFactoryMethod(String beanId, Class<?> beanType, Object[] args) {
        return null;
    }

    @Override
    protected Constructor findApplicableConstructor(String beanId, Class beanType, Object[] args) {
        Assert.notNull(beanId,"beanId must be not null");
        BeanDefinition definition=this.definitionMap.get(beanId);
        if(beanType==null){
            beanType=definition.getBeanClass();
        }
        MethodAccessor methodAccessor=getMethodAccessor();
        Constructor ret=null;

        if(methodAccessor==null){
            methodAccessor=new MethodAccessor(new DefaultParamNameDiscover(),this);
        }
        if(args!=null){
            ConstructorArgumentValues cstArgs=resovleConstructorArgs(beanId,beanType,args);
            ret=findApplicableConstructor(beanType.getConstructors(),cstArgs);
        }else{
            ret=findApplicableConstructor(beanType.getConstructors(),definition.getConstructorArgumentValues());
        }
        return ret;
    }

    @Override
    protected ConstructorArgumentValues resovleConstructorArgs(String beanId,Class beanType,Object[] args){
        ConstructorArgumentValues ret=super.resovleConstructorArgs(beanId,beanType,args);
        if(ret==null){
            BeanDefinition definition=this.definitionMap.get(beanId);
            ret= definition.getConstructorArgumentValues();
        }
        return ret;
    }

    @Override
    public int extractBeanFrom(String fileName) {
        return 0;
    }

    @Override
    public int extractBeanFrom(File file) {
        return 0;
    }

    @Override
    public int extractBeanFrom(String[] file) {
        return 0;
    }

    @Override
    public int extractBeanFrom(File[] files) {
        return 0;
    }

    @Override
    public void setConfigLocation(String configLocation) {
        this.configLocation=configLocation;
    }


    @Override
    public void setConfiguration(Configuration configLocation) {
        this.configuration=configLocation;
    }

    @Override
    public Configuration getConfiguration() {
        return this.configuration;
    }

    @Override
    public boolean isNeedValidation() {
        return false;
    }

}
