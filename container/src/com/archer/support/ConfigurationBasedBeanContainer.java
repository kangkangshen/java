package archer.support;/*
 *@author:wukang
 */

import archer.*;
import archer.config.Configuration;
import archer.config.XmlReader;
import archer.definition.*;
import archer.context.lifecycle.InitializingBean;
import archer.context.lifecycle.EventListener;
import archer.support.baseImpl.AbstractAliasRegistrar;
import archer.support.convert.*;
import archer.support.debug.Description;
import archer.support.ext.ExtensibleBeanContainer;
import archer.util.*;
import org.apache.log4j.Logger;
import org.dom4j.Document;
import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

public  class ConfigurationBasedBeanContainer extends AbstractAliasRegistrar implements ExtensibleBeanContainer, HierarchicalBeanContainer, ConfigurableBeanContainer,BeanDefinitionManager {
    private Environment environment;
    private MethodAccessor methodAccessor;
    private BeanContainer parrent;
    private Logger logger=Logger.getLogger(this.getClass());
    private XmlReader reader;
    private Map<String,BeanDefinition> definitionMap =new ConcurrentHashMap<String,BeanDefinition>(16);
    private Map<String,Object> objectMap=new ConcurrentHashMap<String,Object>(16);
    private Set<String> creationSet=new HashSet<String>(16);
    private Map<String,String> aliasMap=new ConcurrentHashMap<String,String>(16);
    private Map<String, ObjectCreator> exposureObjectMap=new ConcurrentHashMap<>(16);
    private boolean allowEagerClassLoading=false;
    private boolean allowBeanDefinitionOverriding=true;
    private boolean fetchListener=true;
    private String configLocation;
    private final static String DEFAULT_CONFIG_LOCATION="META-INF/applicationContext.xml";
    private Configuration configuration;
    private String containerName;
    private String containerId;
    private boolean inited=false;
    private List<BeanPostProcessor> processors=new ArrayList<BeanPostProcessor>(16);
    private List<BeanContainerPostProcessor> containerPostProcessors=new ArrayList<BeanContainerPostProcessor>(16);
    private Set<EventListener<ContainerEvent>> listeners=new HashSet<>(4);
    private ReentrantLock lock=new ReentrantLock();
    private boolean needValidation;

    public ConfigurationBasedBeanContainer(String configLocation){
        this(configLocation,null,false,true);
    }

    public ConfigurationBasedBeanContainer(String configLocation,String containerId){
        this(configLocation,containerId,false,true);
    }

    public ConfigurationBasedBeanContainer(String configLocation,String containerId,boolean allowEagerClassLoading,boolean allowBeanDefinitionOverriding){
        this.configLocation=configLocation;
        this.containerId=containerId;
        this.allowEagerClassLoading=allowEagerClassLoading;
        this.allowBeanDefinitionOverriding=allowBeanDefinitionOverriding;
        this.methodAccessor=new MethodAccessor(new DefaultParamNameDiscover(),this);
        this.environment=new DefaultEnvironment();
        //todo
        this.reader=new XmlReader(this);
        parseAndInitInternal();
        prepareToloadBeans();
        if(allowEagerClassLoading){
            loadBeansEagerly();
            actionsAfterLoading();
        }
    }

    private void parseAndInitInternal(){
        EncodeResource resource=new EncodeResource(configLocation);
        if(reader!=null){
            Document document=reader.loadDocument(resource);
            List<BeanDefinition> definitions=reader.parse(document,this);
            for(BeanDefinition definition:definitions){
                registerBeanDefinition(definition,definition.getId());
            }
        }
    }

    protected void prepareToloadBeans(){
        TypeConverterComposite defaultInternalConverter=new TypeConverterComposite();
        defaultInternalConverter.detectAllTypeConverter(this);
        this.objectMap.putIfAbsent("internalTypeConverterComposite",new TypeConverterComposite());
    }
    protected void loadBeansEagerly(){

    }

    protected void actionsAfterLoading(){

    }

    public void registerBeanPostProcessor(BeanPostProcessor processor){
        this.processors.add(processor);
    }

    public void registerBeanContainerPostProcessor(BeanContainerPostProcessor processor){
        this.containerPostProcessors.add(processor);
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

    @Override
    public void setName(String name) {
        this.containerName=name;
    }

    @Override
    public String getName(String name) {
        return this.containerName;
    }

    @Override
    public void setId(String id) {
        this.containerId=id;
    }

    @Override
    public String getId() {
        return containerId;
    }

    @Override
    public Object getBean(String name) {
        return getBean(name,null,null);
    }

    @Description(description = "")
    @Override
    public <T> T getBean(Class<T> type) {
        List<T> beans=getBeansOfSpecifiedType(type);
        if(beans==null||beans.isEmpty()){
            ;
        }else if(beans.size()>1){
            //sort and choose the first

        }else{
            ;
        }
        return null;
    }

    @Override
    public <T> List<T> getBeansOfSpecifiedType(Class<T> type){
        List<T> beans=new LinkedList<>();
        //1.检查缓存发现预注册的bean或者已经已经加载过的bean
        for(Object bean:this.objectMap.values()){
            if(ObjectUtils.isInstanceOf(bean,type)){
                beans.add((T) bean);
            }
        }
        for(Object bean:this.exposureObjectMap.values()){
            if(ObjectUtils.isInstanceOf(bean,type)){
                if(logger.isDebugEnabled()){
                    logger.debug("Bean of type \""+ type.getName()+"\" is being created, returning an unfinished bean");
                }
                beans.add((T)bean);
            }
        }
        //3.检查当前的definitionMap
        for(BeanDefinition definition:this.definitionMap.values()){
            if(type.isAssignableFrom(definition.getBeanClass())){
                Object beanInstance=createBeanInstance(definition.getId(),definition,null);
                Object bean=getObjectForBeanInstance(definition.getId(),beanInstance);
                beans.add((T) bean);
            }
        }
        return beans;
    }

    @Override
    public <T> T getBean(String name, Class<T> beanType) {
        return getBean(name,beanType,null);
    }

    @Override
    public Object getBean(String name, Object[] args) {
        return getBean(name,null,args);
    }

    @Override
    public Object getBean(Class<?> type, Object[] args) {
        return getBean(null,type,args);
    }

    @Override
    public <T> T getBean(String name, Class<T> type, Object[] args) {
        //如果当前beandefinitions不包含，去父容器寻找
        if(!containsBeanDefinition(name)&&parrent!=null){
            return parrent.getBean(name,type,args);
        }
        T bean=doGetBean(name,type,args);
        if(bean==null){
            throw new NoSuchBeanException(name);
        }
        return bean;
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


    protected <T> T doGetBean(String name,Class<?> type,final Object[] args){
        String beanName=formalizeBeanName(name);
        final String beanId=getBeanIdfromName(beanName);
        Object beanInstance=getSingleton(beanId);
        if(beanInstance!=null&&args==null){
            if(!type.isAssignableFrom(beanInstance.getClass())){
                if(logger.isDebugEnabled()){
                    logger.debug("Bean \""+beanName+"\"  is found , but the type is not match ");
                }
                return null;
            }
            if(isSingletonCurrentlyInCreation(beanId)){
                if(logger.isDebugEnabled()){
                    logger.debug("Bean \""+beanName+"\"  is creating , return a not fully initialized bean as result ");
                }
            }else {
                if (logger.isDebugEnabled()) {
                    logger.debug("Return cached bean instance with name \"" + beanName + "\" as result ");
                }

                beanInstance = getObjectForBeanInstance(beanId, beanInstance);
                return (T) beanInstance;
            }
        }else{
            //todo
            /*
            if(isPrototypeCurrentlyInCreation(beanId)){
                throw new BeanCurrentlyInCreationException("Bean with name \""+beanName+"\" in creation .");
            }
            */
            final BeanDefinition rbd=getPreparedLocalBeanDefinition(beanId);

            String[] dependson=rbd.getDependsOn();
            if(dependson!=null){
                for(String dependOn:dependson){
                    getBean(dependOn);
                }
            }
            if(rbd.isSingleton()){
                beanInstance=createBeanInstance(beanId,rbd,args);
                beanInstance=getObjectForBeanInstance(beanId,beanInstance);
            }else if(rbd.isPrototype()){
                //...todo
            }else{
                //...todo
            }
        }
        return (T)beanInstance;
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

    private Object getSingleton(String beanId){
        //此方法只能使用beanid获取
        if(objectMap.containsKey(beanId)){
            return objectMap.get(beanId);
        }
        if(exposureObjectMap.containsKey(beanId)){
            return exposureObjectMap.get(beanId).createObject();
        }
        return null;
    }

    private boolean isSingletonCurrentlyInCreation(String beanId){
        if(creationSet.contains(beanId)){
            return true;
        }
        return false;
    }

    private Object getObjectForBeanInstance(String beanId,Object beanInstance){
        if(beanId.startsWith(BeanContainer.FACTORY_BEAN_PREFIX)){
            if(beanInstance instanceof FactoryBean){
                return ((FactoryBean)beanInstance).getObject();
            }
        }
        return beanInstance;
    }

    private boolean isPrototypeCurrentlyInCreation(String beanId){
        throw new UnsupportedOperationException();
    }

    private Object createBeanInstance(final String beanId, final BeanDefinition definition, final Object[] args){
        Assert.notNull(beanId,"bean ID must be not null");
        Assert.notNull(definition,"bean definition must be not null");
        if(logger.isDebugEnabled()){
            logger.debug("Start creating bean instance with ID \""+beanId+"\"");
        }
        if(instantiable(definition)){
            Object bean= instantiateBean(beanId,definition,args);
            saveBeanToCantainer(bean,definition);
            initializeBean(bean,definition);
            return bean;
        }
        return null;

    }

    private String getBeanIdfromName(String name) {
        String beanName=formalizeBeanName(name);
        String autonym=getAutonym(beanName);
        if(definitionMap.containsKey(autonym)){
            return autonym;
        }else{
            return null;
        }
    }

    @Override
    public boolean containsBean(String name) {
        String beanName=formalizeBeanName(name);
        String beanId=getBeanIdfromName(beanName);
        if(objectMap.containsKey(beanId)){
            return true;
        }

        if(exposureObjectMap.containsKey(beanId)){
            return true;
        }
        if(isSingletonCurrentlyInCreation(beanId)){
            if(logger.isInfoEnabled()){
                logger.info("This bean is not included in the object cache, but it is creating");
            }
            return true;
        }
        if(definitionMap.containsKey(beanId)){
            if(logger.isInfoEnabled()){
                logger.info("The bean is neither in the object cache nor is being created, but is defined");
            }
            return true;
        }
        return false;
    }


    //todo
    @Override
    public boolean containsBean(Class beanType) {
        boolean inCache=false,inCreating=false,defined=false;
        for(Object bean:objectMap.values()){
            if(beanType.isAssignableFrom(bean.getClass())){
                inCache=true;
                break;
            }
        }

        for(BeanDefinition definition:definitionMap.values()){
            if(beanType.isAssignableFrom(definition.getClass())){
                defined=true;
                break;
            }
        }

        if(!inCache&&defined){
            if(logger.isInfoEnabled()){
                logger.info("The bean is neither in the object cache nor is being created, but is defined");
            }
        }
        return inCache||inCreating||defined;

    }

    @Override
    public Class<?> getType(String name) {
        if(containsBean(name)){
            return getBean(name).getClass();
        }else{
            throw new NoSuchBeanException(name);
        }
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
    public void setAllowBeanDefinitionOverride(boolean allowBeanDefinitionOverride) {
        this.allowBeanDefinitionOverriding=allowBeanDefinitionOverride;
    }

    @Override
    public boolean getAllowBeanDefinitionOverride() {
        return this.allowBeanDefinitionOverriding;
    }

    @Override
    public boolean isLazyInit(BeanDefinition definition) {
        return definition.isLazyInit();
    }

    @Override
    public boolean containsBeanDefinition(BeanDefinition definition) {
        return definitionMap.containsKey(definition.getId());
    }

    @Override
    public void removeBeanDefinition(String beanName) {
        if(!containsBean(beanName)){
            return ;
        }

    }

    @Override
    public void addBean(String name, Object bean) {

    }

    @Override
    public void removeBean(String name) {

    }

    @Override
    public void removeBean(Class<?> type) {

    }

    @Override
    public List<String> getBeanNames() {
        Set<String> names=definitionMap.keySet();
        List<String> copy=new ArrayList<>(names.size());
        copy.addAll(names);
        return copy;

    }

    @Override
    public int getBeanCount() {
        return definitionMap.size();
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

    @Override
    public List<BeanDefinition> getBeanDefinition(Class<?> type) {
        return null;
    }

    @Override
    public boolean containsBeanDefinition(String var1) {
        return false;
    }

    @Override
    public String[] getBeanDefinitionNames() {
        return new String[0];
    }

    @Override
    public int getBeanDefinitionCount() {
        return 0;
    }

    @Override
    public boolean isBeanNameInUse(String var1) {
        return false;
    }

    @Override
    public void registerBeanDefinition(BeanDefinition bean, String beanId) {
        lock.lock();
        try{
            //检查重复注册
            boolean isDuplicateregistered=false;
            String testId=getBeanIdfromName(formalizeBeanName(beanId));
            if(testId!=null){
                isDuplicateregistered=true;
            }
            if(!isDuplicateregistered){
                //未发生重复注册
                this.definitionMap.put(beanId,bean);
                this.objectMap.remove(beanId);
                this.exposureObjectMap.remove(beanId);
            }else{
                //发生重复注册bean
                if(allowBeanDefinitionOverriding){
                    //该容器允许bean覆盖
                    if(bean.isOverriding()){
                        //bean本身要求覆盖
                        if(logger.isDebugEnabled()){
                            logger.debug("The bean has been repeatedly registered, " +
                                    "the container allows the bean to be overwritten, " +
                                    "and the bean named \""+beanId+"\" itself requires coverage.");
                        }
                        this.definitionMap.put(testId,bean);
                        this.objectMap.remove(testId);
                        this.exposureObjectMap.remove(testId);
                    }else{
                        //bean本身要求跳过
                        if(logger.isDebugEnabled()){
                            logger.debug("The bean is repeatedly registered," +
                                    " the container allows the bean to be overwritten," +
                                    " and the bean named \""+beanId+"\"itself asks to skip");
                        }
                    }

                }else{
                    if(bean.isOverriding()){
                        throw new DuplicateRegisteredBeanException(beanId,
                                "The bean has been repeatedly registered, " +
                                "the container does not allow the bean to be overwritten, " +
                                "but the bean itself requires coverage.");
                    }
                }
            }

        }finally {
            lock.unlock();
        }

    }


    protected void fetchAllListener(ConfigurableBeanContainer container){
        //todo

    }
    protected Object instantiateBean(String beanId,BeanDefinition rbd,Object[] args)  {
        Object bean;
        try{
            bean=applyBeanPreProcessor(rbd,this);
            if(bean!=null){
                bean=applyBeanPostProcessor(bean,rbd,this);
                return bean;
            }
        }catch (Throwable ex){
            throw new BeanCreationException(beanId,"");
        }
        bean=doInstantiateBeanDefinition(rbd,args);
        applyBeanPostProcessor(bean,rbd,this);
        return bean;

    }
    protected Object applyBeanPreProcessor(Object o, ConfigurableBeanContainer configurableBeanContainer){
        Object rawBean=null;
        if(this.processors!=null&&this.processors.isEmpty()){
            for(BeanPostProcessor processor:processors){
               rawBean =processor.processRawBeanBeforeInstantiation(o);
                if(rawBean==null){
                    return rawBean;
                }
            }
        }
        return rawBean;

    }
    protected void saveBeanToCantainer(Object o,BeanDefinition definition){
        String beanName=definition.getId();
        if(allowBeanDefinitionOverriding){
            this.objectMap.putIfAbsent(beanName,o);
        }

    }
    protected void initializeBean(Object o, BeanDefinition definition) {
        if(o instanceof InitializingBean){
            InitializingBean bean=(InitializingBean)o;
            bean.init();
        }
    }
    protected Object applyBeanPostProcessor(Object o,BeanDefinition definition,BeanContainer container){

        for(BeanPostProcessor processor:processors){
            if(o!=null){
                processor.processInstanceAfterInstantiation(o);
            }
        }
        return o;
    }

    protected void populateProperties(Object bean,Object[] args){

    }

    protected void resovleDependencies(BeanDefinition definition){
        if(definition.getDependsOn()!=null){
            String[] dependsOn= definition.getDependsOn();
            for(String dependOn:dependsOn){
                getBean(dependOn);
            }
        }
    }

    protected Object doInstantiateBeanDefinition(BeanDefinition definition,Object[] args){
        if(definition.getFactoryBeanName()!=null){
            return doInstantiateBeanUseFactoryBean(definition,args);
        }
        else if(definition.getFactoryMethodName()!=null){
            return doInstantiateBeanUseFactoryMethod(definition,args);
        }
        else{
            return doInstantiateBeanUseConstructor(definition,args);
        }
    }

    //todo
    private Object doInstantiateBeanUseFactoryMethod(BeanDefinition definition, Object[] args) {
        Class<?> type= null;
        try {
            type = definition.getClass()!=null?definition.getClass(): Class.forName(definition.getBeanClassName());
        } catch (ClassNotFoundException e) {
            ;
        }
        Method[] methods=type.getMethods();
        for(Method method:methods){
            if(methodAccessor.isTypeMatch(method,args)){
                try {
                    return method.invoke(null,args);
                } catch (IllegalAccessException e) {
                    ;
                } catch (InvocationTargetException e) {
                    throw new BeanCreationException(definition.getId(),e);
                }
            }
        }
        return null;
    }

    private Object doInstantiateBeanUseFactoryBean(BeanDefinition definition, Object[] args) {
        return null;
    }

    private Object doInstantiateBeanUseConstructor(BeanDefinition definition, Object[] args) {
        try {
            Class<?> clazz=definition.getClass()==null?definition.getClass():Class.forName(definition.getBeanClassName());
            Constructor[] constructors=clazz.getDeclaredConstructors();
            Constructor targetConstructor=null;
            ConstructorArgumentValues constructorArgumentValues=null;
            if(args==null){
                targetConstructor=findApplicableConstructor(constructors,definition.getConstructorArgumentValues());
                constructorArgumentValues=definition.getConstructorArgumentValues();
            }else{
                constructorArgumentValues=resovleConstructorArgs(clazz,args);
                targetConstructor=findApplicableConstructor(constructors,constructorArgumentValues);
            }

            if(targetConstructor==null){
                throw new NoApplicableConstructorFoundException(definition.getId(),"Did not find a suitable constructor for the bean named Xiaoming based on the parameters filled in.");
            }
            return BeanUtils.invokeConstructor(targetConstructor,constructorArgumentValues);
        } catch (ClassNotFoundException e) {
            ;
        }
        return null;
    }


    private ConstructorArgumentValues resovleConstructorArgs(Class beanClass,Object[] args) {
        List<PropertyValue> propertyValues=new ArrayList<>(args.length);
        for(int i=0;i<args.length;i++){
            propertyValues.add(new PropertyValue(i,args[i],true));
        }
        ConstructorArgumentValues values=new ConstructorArgumentValues(beanClass,propertyValues);
        return values;

    }


    private Constructor findApplicableConstructor(Constructor[] constructors, ConstructorArgumentValues constructorArgumentValues) {
        for(Constructor constructor:constructors){
            if(methodAccessor.isTypeMatch(constructor,constructorArgumentValues)){
                return constructor;
            }
        }
        return null;
    }


    @Override
    public void detectAllBeanContainerProcessor(ConfigurableBeanContainer container){
        try{
            List<BeanContainerPostProcessor> processors=container.getBeansOfSpecifiedType(BeanContainerPostProcessor.class);
            for(BeanContainerPostProcessor postProcessor:processors){
                registerBeanContainerPostProcessor(postProcessor);
            }
        }catch (NoSuchBeanException e){
            if(logger.isDebugEnabled()){
                logger.debug("The current container with name \""+container.getId()+"\" has no any bean container processor");
            }
        }
    }

    @Override
    public Object applyBeanPostProcessorBeforeInstantiation(Object o, Object[] args) {
        return null;
    }

    @Override
    public Object applyBeanPostProcessorAfterInstantiation(Object o, Object[] args) {
        return null;
    }

    @Override
    public void applyBeanContainerPostProcessor(ConfigurableBeanContainer container) {
        if(containerPostProcessors!=null||!containerPostProcessors.isEmpty()){
            for(BeanContainerPostProcessor processor:containerPostProcessors){
                //todo
                ;
            }
        }
    }

    @Override
    public void detectAllBeanPostProcessor(ConfigurableBeanContainer container){
        try {
            List<BeanPostProcessor> processors=container.getBeansOfSpecifiedType(BeanPostProcessor.class);
            for(BeanPostProcessor postProcessor:processors){
                registerBeanPostProcessor(postProcessor);
            }
        } catch (NoSuchBeanException e) {
            if(logger.isDebugEnabled()){
                logger.debug("The current container with name \""+container.getId()+"\" has no any bean container processor");
            }
        }
    }

    @Override
    public void setParentContainer(BeanContainer parentContainer) {
        this.parrent=parentContainer;
    }

    public void setNeedValidation(boolean validation){
        this.needValidation=validation;
    }

}
