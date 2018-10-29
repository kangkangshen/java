package archer.container.support;/*
 *@author:wukang
 */

import archer.container.*;
import archer.container.config.Configuration;
import archer.container.config.XmlReader;
import archer.container.context.lifecycle.EventListener;
import archer.container.context.lifecycle.InitializingBean;
import archer.container.definition.*;
import archer.container.support.baseImpl.AbstractAliasRegistrar;
import archer.container.support.convert.TypeConverterComposite;
import archer.container.support.debug.Description;
import archer.container.support.ext.ExtensibleBeanContainer;
import archer.container.util.*;
import jdk.internal.org.objectweb.asm.ClassReader;
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

public abstract class AbstractBeanContainer extends AbstractAliasRegistrar implements ExtensibleBeanContainer,HierarchicalBeanContainer,NamedBeanContainer {
    private Environment environment;

    private BeanContainer parrent;
    protected Logger logger=Logger.getLogger(this.getClass());
    private MethodAccessor methodAccessor;

    protected Map<String,Object> objectMap;
    protected Set<String> creationSet;
    protected Map<String, ObjectCreator> exposureObjectMap;

    private boolean allowEagerClassLoading=false;
    private boolean allowBeanOverriding=false;

    private boolean fetchListener=true;
    private List<BeanPostProcessor> processors;
    private List<BeanContainerPostProcessor> containerPostProcessors;
    private Set<EventListener<ContainerEvent>> listeners;


    protected String configLocation;
    protected Class configClass;
    protected Configuration configuration;

    private final static String DEFAULT_CONFIG_LOCATION="META-INF/applicationContext.xml";
    private String containerName;
    private String containerId;
    private boolean inited=false;
    private boolean needValidation;
    private ReentrantLock lock;


    public AbstractBeanContainer(String configLocation){
        this(configLocation,null,null,false,true);
    }

    public AbstractBeanContainer(String configLocation,String containerId){
        this(configLocation,null,containerId,false,true);
    }

    public AbstractBeanContainer(Class configClass){
        this(null,configClass,null,false,true);
    }

    public AbstractBeanContainer(Class configClass,String containerId){
        this(null,configClass,containerId,false,true);
    }

    private AbstractBeanContainer(String configLocation,Class configClass,String containerId,boolean allowEagerClassLoading,boolean allowBeanOverriding){
        if(configClass!=null||(configLocation!=null|| StringUtils.roughEqual(configLocation,""))){
            // Make sure that at least one of the two approaches works
            this.configLocation=configLocation;
            this.configClass=configClass;
        }
        initMembervariables();
        parseAndInitInternal();
        prepareToloadBeans();
        if(allowEagerClassLoading){
            loadBeansEagerly();
            actionsAfterLoading();
        }
    }

    private void initMembervariables(){
        this.exposureObjectMap=new ConcurrentHashMap<>(16);
        this.objectMap=new ConcurrentHashMap<>(16);
        this.creationSet=new HashSet<>(16);
        this.containerId=containerId;
        this.allowEagerClassLoading=allowEagerClassLoading;
        this.allowBeanOverriding=allowBeanOverriding;
        this.methodAccessor=new MethodAccessor(new DefaultParamNameDiscover(),this);
        this.environment=new DefaultEnvironment();
        this.processors=new ArrayList<BeanPostProcessor>(16);
        this.containerPostProcessors=new ArrayList<BeanContainerPostProcessor>(16);
        this.listeners=new HashSet<>(4);
        this.lock=new ReentrantLock();

    }

    protected abstract void parseAndInitInternal();

    protected void prepareToloadBeans(){
        TypeConverterComposite defaultInternalConverter=new TypeConverterComposite();
        defaultInternalConverter.detectAllTypeConverter(this);
        addBean("internalTypeConvertomposite",new TypeConverterComposite());
        detectAllBeanContainerProcessor();
        detectAllBeanPostProcessor();
    }

    protected abstract void loadBeansEagerly();

    protected abstract void actionsAfterLoading();

    public void registerBeanPostProcessor(BeanPostProcessor processor){
        this.processors.add(processor);
    }

    public void registerBeanContainerPostProcessor(BeanContainerPostProcessor processor){
        this.containerPostProcessors.add(processor);
    }

    @Override
    public Object getBean(String name) {
        return getBean(name,null,null);
    }

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
        //2.检查正在创建的bean
        for(Object bean:this.exposureObjectMap.values()){
            if(ObjectUtils.isInstanceOf(bean,type)){
                if(logger.isDebugEnabled()){
                    logger.debug("Bean of type \""+ type.getName()+"\" is being created, returning an unfinished bean");
                }
                beans.add((T)bean);
            }
        }
        //3.检查子类对raw bean definitions 的某些处理
        List<T> rawBeansIfDefined=resovleRawBeanDefinitions(type);
        if(rawBeansIfDefined!=null&&!rawBeansIfDefined.isEmpty()){
            beans.addAll(rawBeansIfDefined);
        }
        return beans;

    }

    protected abstract <T> List<T> resovleRawBeanDefinitions(Class<T> type);

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
        //如果当前原生bean定义不包含，去父容器寻找
        if(!containsBeanDefinition(name)&&parrent!=null){
            return parrent.getBean(name,type,args);
        }
        T bean=doGetBean(name,type,args);
        if(bean==null){
            throw new NoSuchBeanException(name);
        }
        return bean;
    }

    @Description(description = "此处beanDefinition指的是是否有该bean的原生定义，并不专指使用xml配置构造的beanDefinition")
    protected abstract boolean containsBeanDefinition(String name);

    protected <T> T doGetBean(String name,Class<?> type,final Object[] args){
        boolean requireFactoryBean=name.startsWith("&");
        String beanName=formalizeBeanName(name);
        String beanId=getBeanIdfromName(beanName);

        Object beanInstance=getSingleton(beanId,requireFactoryBean);
        if(beanInstance!=null&&args==null){
            if(!type.isAssignableFrom(beanInstance.getClass())){
                if(logger.isDebugEnabled()){
                    logger.debug("Bean \""+beanName+"\"  is found , but the type is not match ");
                }
                return null;
            } else {
                if (logger.isDebugEnabled()) {
                    logger.debug("Return cached bean instance with name \"" + beanName + "\" as result ");
                }

                beanInstance = getObjectForBeanInstance(beanId, beanInstance);
                return (T) beanInstance;
            }
        }
        else if(isSingletonCurrentlyInCreation(beanId)){
            if(logger.isDebugEnabled()){
                logger.debug("Bean \""+beanName+"\"  is creating , cannt return  a not fully initialized bean as result ");
            }
            return null;
        }
        else{
            if(isPrototypeCurrentlyInCreation(beanId)){
                if(logger.isDebugEnabled()){
                    logger.debug("Bean named  \""+beanName+"\" in creation .");
                }
                return null;
            }
            String[] dependson=getDependsOn(beanId);
            if(dependson!=null){
                for(String dependOn:dependson){
                    getBean(dependOn);
                }
            }

            beanInstance=createBeanInstance(beanId,type,args);

            OptionalPropertyValues setterValues=patchBeanDefinition(beanId,type);

            populateProperties(beanId,beanInstance,setterValues);

            applyBeanPostProcessorBeforeInitialization(beanInstance);

            initializeBean(beanInstance);

            applyBeanPostProcessorBeforeInitialization(beanInstance);

            if(isSingleton(beanId)){
                addBean(beanId,beanInstance);
            }
            if(isPrototype(beanId)){
                //todo something need to be done
            }
            if(requireFactoryBean){
                beanInstance=getObjectForBeanInstance(beanId,beanInstance);
            }

        }
        return (T)beanInstance;
    }

    protected abstract OptionalPropertyValues patchBeanDefinition(String beanId, Class<?> beanType);

    @Description(description = "由子类维护相关数据结构")
    protected abstract String[] getDependsOn(String beanId);

    public  Object createBeanInstance(String beanId, Class beanType, Object[] args){
        try{
            this.creationSet.add(beanId);
            return doInstantiateRawBean(beanId,beanType,args);
        }finally {
            this.creationSet.remove(beanId);
        }

    }

    private Object getSingleton(String beanId,boolean requireFactoryBean){
        //此方法只能使用beanid获取
        Object bean=null;
        if(objectMap.containsKey(beanId)){
            bean= objectMap.get(beanId);
        }
        if(exposureObjectMap.containsKey(beanId)){
            bean= exposureObjectMap.get(beanId).getObject();
        }
        if(requireFactoryBean){
            bean=getObjectForBeanInstance(beanId,bean);
        }
        return bean;
    }

    private boolean isSingletonCurrentlyInCreation(String beanId){
        if(creationSet.contains(beanId)){
            return true;
        }
        return false;
    }

    public Object getObjectForBeanInstance(String beanId,Object beanInstance){
        if(beanInstance instanceof FactoryBean){
            return ((FactoryBean)beanInstance).getObject();
        }
        return beanInstance;
    }

    private boolean isPrototypeCurrentlyInCreation(String beanId){
        return false;
    }

    @Description(description = "return a bean id from container 's beans, if there has no this bean name represented , return null  ")
    public String getBeanIdfromName(String name) {
        String beanName=formalizeBeanName(name);
        String autonym=getAutonym(beanName);
        if(autonym==null){
            //没有该name对应的真名，查找当前已注册的bean
            if(this.objectMap.containsKey(beanName)||this.exposureObjectMap.containsKey(beanName)){
                autonym=beanName;
            }
        }
        return autonym;
    }

    @Override
    public boolean containsBean(String name) {
        String beanName=formalizeBeanName(name);
        String beanId=getBeanIdfromName(beanName);
        //1.已经创建好
        if(objectMap.containsKey(beanId)){
            return true;
        }
        //2.提前暴露
        if(exposureObjectMap.containsKey(beanId)){
            return true;
        }
        //3.正在创建
        if(isSingletonCurrentlyInCreation(beanId)){
            if(logger.isInfoEnabled()){
                logger.info("This bean is not included in the object cache, but it is creating");
            }
            return true;
        }
        //4.检查子类是否有关此类的定义
        return containsBeanDefinition(beanId);
    }

    @Override
    public boolean containsBean(Class beanType) {
        boolean inCache=false,inCreating=false;
        for(Object bean:objectMap.values()){
            if(ObjectUtils.isInstanceOf(bean,beanType)){
                inCache=true;
                break;
            }
        }
        for(Object bean:exposureObjectMap.values()){
            if(ObjectUtils.isInstanceOf(bean,beanType)){
                inCreating=true;
                break;
            }
        }

        return inCache||inCreating;

    }

    @Override
    public Class<?> getType(String name) {
        if(containsBean(name)){
            return getBean(name).getClass();
        }else{
            throw new NoSuchBeanException(name);
        }
    }


    public void setAllowBeanOverride(boolean allowBeanOverride) {
        this.allowBeanOverriding=allowBeanOverride;
    }

    public boolean isAllowBeanDefinitionOverriding(){return this.allowBeanOverriding;}

    @Override
    public void addBean(String name, Object bean) {
        lock.lock();
        if(!isBeanNameInUse(name)){
            String beanId=formalizeBeanName(name);
            this.objectMap.put(beanId,bean);
        }else{
            if(isAllowBeanDefinitionOverriding()){
                if(logger.isDebugEnabled()){
                    logger.debug("this name \""+name+"\" has been used ,but original bean is overrided .");
                }
                this.objectMap.put(getBeanIdfromName(name),bean);
            }
        }
    }

    @Description(description = "不建议代码中直接调用。正确用法是当且仅当容器关闭，也即应用停止，由容器自行销毁bean")
    @Override
    public void removeBean(String name) {
        if(!containsBean(name)){
            return ;
        }
        String beanId=getBeanIdfromName(name);

        lock.lock();
        if(isSingletonCurrentlyInCreation(beanId)){
            //当前正在创建，只能等待创建完毕，然后才能对其进行删除
            waitBeanCreated(beanId,-1L);
        }
        objectMap.remove(beanId);
        exposureObjectMap.remove(beanId);
        lock.unlock();
    }

    @Override
    public void removeBean(Class<?> type) {
        throw new UnsupportedOperationException();

    }

    @Override
    public int getBeanCount(){
        return this.objectMap.size();
    }

    private void waitBeanCreated(String beanId,long time){
        if(time==-1){
            while(true){
                if(!isSingletonCurrentlyInCreation(beanId)){
                    break;
                }
            }
        }else{
            throw new UnsupportedOperationException("暂不支持基于定时器的销毁操作");
        }

    }

    public boolean isBeanNameInUse(String var1) {
        if(getAutonym(var1)!=null){
            return true;
        }
        if(this.objectMap.containsValue(var1)||this.exposureObjectMap.containsValue(var1)){
            return true;
        }
        return false;
    }

    protected void initializeBean(Object o) {
        if(o instanceof InitializingBean){
            InitializingBean bean=(InitializingBean)o;
            bean.init();
        }
    }

    public void populateProperties(String beanId,Object bean,OptionalPropertyValues optionalPropertyValues){
        try {
            Assert.isTrue(ObjectUtils.isInstanceOf(bean, optionalPropertyValues.getBeanClass()), "setter args must match the bean .");
            this.exposureObjectMap.put(beanId, new ObjectCreator() {
                @Override
                public Object getObject() {
                    return bean;
                }
            });
            List<PropertyValue> propertyValues = optionalPropertyValues.getOptionalPropertyValues();
            for (PropertyValue prop : propertyValues) {
                String filed = prop.getParamName();
                ObjectUtils.setter(bean, filed, prop.getValue());
            }
        }finally {
            this.exposureObjectMap.remove(beanId);
        }
    }

    public void resovleDependencies(String beanId){
        String[] beansDependsOn=getDependsOn(beanId);
        for(String dependOn:beansDependsOn){
            getBean(dependOn);
        }
    }

    protected Object doInstantiateRawBean(String beanId,Class<?> beanType,Object[] args){
        if(hasFactoryMethod(beanId,beanType)){
            Method factoryMethod=findAppropriateFactoryMethod(beanId,beanType,args);
            return doInstantiateBeanUseFactoryMethod(beanId,beanType,factoryMethod,args);
        }else{
            Constructor constructor=findApplicableConstructor(beanId,beanType,args);
            if(args!=null){
                return doInstantiateBeanUseConstructor(beanId,beanType,constructor,args);
            }
            ConstructorArgumentValues cstAgs=resovleConstructorArgs(beanId,beanType,args);
            return BeanUtils.invokeConstructor(constructor,cstAgs);
        }
    }

    protected abstract boolean hasFactoryMethod(String beanId, Class beanType);

    protected abstract Method findAppropriateFactoryMethod(String beanId, Class<?> beanType, Object[] args);

    protected abstract Constructor findApplicableConstructor(String beanId,Class beanType,Object[] args);

    private Object doInstantiateBeanUseFactoryMethod(String beanId,Class beanType,Method factoryMethod,Object[]args) {
        try {
            return factoryMethod.invoke(beanType,args);
        } catch (IllegalAccessException e) {
            if(logger.isDebugEnabled()){
                logger.debug("factory method of \""+beanType.getName()+"\" named \""+factoryMethod.getName()+"\" is not public ,check your class define");
                throw new BeanCreationException(beanId,e);
            }
        } catch (InvocationTargetException e) {
            if(logger.isDebugEnabled()){
                logger.debug("An err occur when factory method was invoked by\""+beanId+"\"");
            }
            throw new BeanCreationException(beanId,e);
        }
        return null;
    }

    @Description(description = "请尽力确保args不为空，并按照顺序排列，否则请考虑BeanUtils.invokeConstructor")
    private Object doInstantiateBeanUseConstructor(String beanId,Class beanType,Constructor constructor, Object[] args) {
        try {
            Assert.notNull(constructor,"constructor must be not null");
            return constructor.newInstance(args);
        } catch (IllegalAccessException e) {
            if(logger.isDebugEnabled()){
                logger.debug("constructor of \""+beanType.getName()+"\" named \""+beanId+"\" is not public ,check your class define");
                throw new BeanCreationException(beanId,e);
            }
        } catch (InstantiationException e) {
            if(logger.isDebugEnabled()){
                logger.debug("An exception unexpected has happened when bean named \""+beanId+"\"is instanting ");
            }
            throw new BeanCreationException(beanId,e);
        } catch (InvocationTargetException e) {
            if(logger.isDebugEnabled()){
                logger.debug("An exception occur when constructor of +\""+beanType.getName()+"\" invoked ,check your args config and rerun ");
            }
            throw new BeanCreationException(beanId,e);
        }
        return null;
    }


    protected ConstructorArgumentValues resovleConstructorArgs(String beanId,Class beanClass,Object[] args) {
        if(args!=null){
            List<PropertyValue> propertyValues=new ArrayList<>(args.length);
            for(int i=0;i<args.length;i++){
                propertyValues.add(new PropertyValue(i,args[i],true));
            }
            ConstructorArgumentValues values=new ConstructorArgumentValues(beanClass,propertyValues);
            return values;
        }
        return null;
    }

    @Description(description = "此处应该尽全力寻找每个参数类型继承树最下面的，如果没办法保证每个参数都是这样，那么只能报错，Effective Java强调慎用重载，尤其是构造器.未来可能添加注解标识当出现冲突时，优先选择哪一个")
    protected Constructor findApplicableConstructor(Constructor[] constructors, ConstructorArgumentValues constructorArgumentValues) {
        List<Constructor> appliableConstructors=new ArrayList<>();
        for(Constructor constructor:constructors){
            if(methodAccessor.isTypeMatch(constructor,constructorArgumentValues)){
                appliableConstructors.add(constructor);
            }
        }
        if(appliableConstructors.size()==1){
            return appliableConstructors.get(0);
        }else{
            Constructor optimalConstructor=appliableConstructors.get(0);
            for(int i=1;i<appliableConstructors.size();i++){
                Class[] paramTypes=optimalConstructor.getParameterTypes();
                Class[] others=appliableConstructors.get(i).getParameterTypes();
                boolean accurater=false;
                boolean rougher=false;
                for(int j=0;j<paramTypes.length;j++){
                    if(others[j].isAssignableFrom(paramTypes[j])){
                        accurater=true;
                    }else{
                        rougher=true;
                    }
                }
                if(accurater&&rougher){
                    throw new AmbiguousChoicesException("Can not find the most appliable Constructor because your class's constructor designed confusion .");
                }
                if(rougher){
                    optimalConstructor=appliableConstructors.get(i);
                }
            }
            return optimalConstructor;
        }
    }

    @Override
    public void detectAllBeanContainerProcessor(){
        try{
            List<BeanContainerPostProcessor> processors=this.getBeansOfSpecifiedType(BeanContainerPostProcessor.class);
            for(BeanContainerPostProcessor postProcessor:processors){
                registerBeanContainerPostProcessor(postProcessor);
            }
        }catch (NoSuchBeanException e){
            if(logger.isDebugEnabled()){
                logger.debug("The current container with name \""+this.getId()+"\" has no any bean container processor");
            }
        }
    }

    @Override
    public Object applyBeanPostProcessorBeforeInitialization(Object bean) {
        if(this.processors!=null&&!this.processors.isEmpty()){
            for(BeanPostProcessor processor:processors){
                bean=processor.processRawBeanBeforeInitialization(bean);
                if(bean==null){
                    break;
                }
            }
        }
        return bean;
    }

    @Override
    public Object applyBeanPostProcessorAfterInitialization(Object bean) {
        if(this.processors!=null&&!this.processors.isEmpty()){
            for(BeanPostProcessor processor:processors){
                bean=processor.processInstanceAfterInitialization(bean);
                if(bean==null){
                    break;
                }
            }
        }
        return bean;
    }

    @Override
    public void applyBeanContainerPostProcessor() {
        if(containerPostProcessors!=null||!containerPostProcessors.isEmpty()){
            for(BeanContainerPostProcessor processor:containerPostProcessors){
                //todo

            }
        }
    }

    @Override
    public void detectAllBeanPostProcessor(){
        try {
            List<BeanPostProcessor> processors=this.getBeansOfSpecifiedType(BeanPostProcessor.class);
            for(BeanPostProcessor postProcessor:processors){
                registerBeanPostProcessor(postProcessor);
            }
        } catch (NoSuchBeanException e) {
            if(logger.isDebugEnabled()){
                logger.debug("The current container with name \""+this.getId()+"\" has no any bean container processor");
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

    @Override
    public void setName(String name) {
        if(containerId==null){
            containerId=name;
        }
        containerName=name;
    }

    @Override
    public String getName(String name) {
        return containerName==null?containerId:containerName;
    }

    @Override
    public void setId(String id) {
        this.containerId=id;
    }

    @Override
    public String getId() {
        return containerId;
    }

    public MethodAccessor getMethodAccessor() {
        return methodAccessor;
    }

    public void setMethodAccessor(MethodAccessor methodAccessor) {
        this.methodAccessor = methodAccessor;
    }
}

