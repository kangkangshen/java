package archer.config;

/*
 *@author:wukang
 * 用于解析xml文件
 */


import archer.BeanNameGenerator;
import archer.PropertyValue;
import archer.config.annnotation.Bean;
import archer.definition.*;
import archer.support.ConfigurableBeanContainer;
import archer.support.DefaultBeanNameGenerator;
import archer.support.debug.Description;
import archer.util.*;
import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.dom4j.io.SAXValidator;
import org.xml.sax.*;

import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Description(description = "现只支持xsd文件的验证")
public class XmlReader {
    private static Logger logger = Logger.getLogger(XmlReader.class);
    private static BeanDefinitionManager manager;
    private static SAXReader reader;
    private static String defaultEncoding = "utf-8";
    private static String defalutValidateDocLocation = "META-INF/";
    private static BeanNameGenerator beanNameGenerator=new DefaultBeanNameGenerator();
    private static final String ROOT_ELEMENT="beans";
    private ConfigurableBeanContainer container;
    public XmlReader(ConfigurableBeanContainer container){
        this.container=container;
        XmlParser.initNSH();
    }
    protected void validateXml(Document document) {
        //SchemaFactory factory=SchemaFactory.newInstance("http://www.w3.org/2001/XMLSchema");
    }
    //todo
    public Document loadDocument(EncodeResource resource) {
        long start = System.nanoTime();
        Document document = null;
        File file = resource.getFile();
        SAXReader saxReader = new SAXReader();
        saxReader.setIgnoreComments(true);
        if (resource.getEncode() != null) {
            saxReader.setEncoding(resource.getEncode());
        } else {
            saxReader.setEncoding(defaultEncoding);
        }
        if(this.container.isNeedValidation()){
            validateXml(document);
        }
        try {
            document = saxReader.read(file);
        } catch (DocumentException e) {
            e.printStackTrace();
        }
        long consume = start - System.nanoTime();
        if (logger.isInfoEnabled()) {
            String fileName = file.getName();
            logger.info("解析文档 " + fileName + "共花费了 " + TimeUnit.NANOSECONDS.toSeconds(consume) + " s");
        }
        return document;
    }

    public Document loadDocument(String filePath) {
        return loadDocument(new EncodeResource(filePath));
    }

    public Document loadDocument(File file){
        long start = System.nanoTime();
        SAXReader reader=new SAXReader();
        Document document=null;
        try {
             document= reader.read(file);
        } catch (DocumentException e) {
            e.printStackTrace();
        }
        long consume =start- System.nanoTime();
        if (logger.isInfoEnabled()) {
            String fileName = file.getName();
            logger.info("解析文档 " + fileName + "共花费了 " + TimeUnit.NANOSECONDS.toSeconds(consume) + " s");
        }
        return document;

    }

    public List<BeanDefinition> parse(Document document, ConfigurableBeanContainer container) {

        return XmlParser.parseDocument(document,container);
    }

    public List<BeanDefinition> parse(Element element) {
        return XmlParser.parse(element,container);
    }

    public ConfigurableBeanContainer getContainer(){
        return container;
    }
    private static class XmlParser {
        private static ConfigurableBeanContainer container;
        private static volatile XmlParser parser;
        private static List<NameSpaceHandler> handlers=new ArrayList<>();
        private static final String DEFAULT_NSHANDLER_LOCATION="classpath://META-INF/namespaceHandler.properties";
        private static String userSpecifiedNSH;
        private static NameSpaceHandler defaultHandler;
        private static final String[] DEFAULT_BEAN_ELEMENTS={"bean","import"};
        private static final String[] OTHER_USES_ELEMENTS={"env","alias"};

        private XmlParser(ConfigurableBeanContainer var) {
            container=var;
        }

        public static XmlParser getParser(ConfigurableBeanContainer container) {
            if (parser == null) {
                synchronized (XmlParser.class) {
                    parser = new XmlParser(container);
                }
            }
            return parser;
        }

        public static void initNSH(){
            if(userSpecifiedNSH!=null&&StringUtils.roughEqual(userSpecifiedNSH,"")){
                Resource resource=new Resource(userSpecifiedNSH);
                Properties properties=new Properties();
                try {
                    properties.load(resource.getInputStream());
                    List<NameSpaceHandler> nameSpaceHandlers=new ArrayList<>(properties.size());
                    Enumeration<String> namespaces= (Enumeration<String>) properties.propertyNames();
                    while (namespaces.hasMoreElements()){
                        String namespace=namespaces.nextElement();
                        String handlerClass=properties.getProperty(StringUtils.trimWhitespace(namespace));
                        NameSpaceHandler handler= BeanUtils.instantiate(handlerClass);
                        nameSpaceHandlers.add(handler);
                    }
                } catch (IOException e) {
                    throw new XmlParseException("User specified nameSpace handler location is invalid,file path is:\""+userSpecifiedNSH+"\"");
                }

            }
        }

        public static List<BeanDefinition> parseDocument(Document document,ConfigurableBeanContainer container) {
            List<BeanDefinition> beanDefinitions=new ArrayList<BeanDefinition>(16);
            Element rootElement= document.getRootElement();
            List<Element> elements=rootElement.elements();
            for(Element element:elements){
                List<BeanDefinition> definitions=XmlParser.parse(element,container);
                beanDefinitions.addAll(definitions);
            }
            return beanDefinitions;
        }

        private static List<BeanDefinition> parse(Element element,ConfigurableBeanContainer container) {
            if(isDefaultBeanEles(element)){
                List<BeanDefinition> definition=new ArrayList<>(1);
                definition.add(parseBeanEleInternal(element));
                return definition;
            }else if(isRootContainerEle(element)){
                return parseRootContainerEle(element,container);
            }else{
                return parseCustomizedInternal(element);
            }
        }

        private static boolean isRootContainerEle(Element element) {
            return element.getName().equals("container");
        }

        private static List<BeanDefinition> parseRootContainerEle(Element element,ConfigurableBeanContainer container){
            String containerid=element.attributeValue("id");
            String profile=element.attributeValue("profile");
            container.setId(containerid);
            //
            //container.setProfile(profile);
            //
            List<Element> elements=element.elements();
            List<BeanDefinition> definitions=new ArrayList<>();
            for(Element subEle:elements){
                definitions.addAll(parse(subEle,container));
            }
            return definitions;
        }

        private static BeanDefinition parseBeanEleInternal(Element beanElement){
            String clazz=beanElement.attributeValue("class");
            Class<?> beanClass=null;
            try {
                beanClass=Class.forName(clazz);
            } catch (ClassNotFoundException e) {
                throw new XmlParseException("The class named \""+clazz+"\"not found,check your config file");
            }
            BaseBeanDefinitionHolder definition=new BaseBeanDefinitionHolder(beanClass);
            definition.setBeanClassName(clazz);
            //设置beanId
            String id=beanElement.attributeValue("id");
            String name=beanElement.attributeValue("name");
            List<String> aliases=null;
            if(id==null){
                if(name==null){
                    id=beanNameGenerator.generateBeanName(definition);
                }else{
                    String[] names=name.split(",");
                    if(names.length==1){
                        id=names[0];
                    }else if(names.length>1){
                        id=names[0];
                        aliases=new ArrayList<>(names.length-1);
                        for(int i=1;i<names.length;i++){
                            aliases.add(names[i]);
                        }
                    }
                }
            }
            ObjectUtils.setter(definition,"id",id);
            if(aliases!=null){
                ObjectUtils.setter(definition,"aliases",aliases.toArray());
            }

            //提取构造器
            ConstructorArgumentValues constructorArgumentValues=null;
            List<Element> constructorEles=beanElement.elements("constructor-arg");
            List<PropertyValue> constructorValues=new ArrayList<>(constructorEles.size());
            for(Element cstEle:constructorEles){
                String cstname=cstEle.attributeValue("name");
                String ref=cstEle.attributeValue("ref");
                String value=cstEle.attributeValue("val");
                String cstindex=cstEle.attributeValue("index");
                PropertyValue constProp;
                if(cstname!=null&&!StringUtils.roughEqual(cstname,"")){
                    if(ref!=null&&!StringUtils.roughEqual("ref","")){
                        constProp=new PropertyValue(cstname,ref.concat("#"),true);
                    }else{
                        constProp=new PropertyValue(cstname,value,true);
                    }
                }else {
                    int index=Integer.parseInt(cstEle.attributeValue("index"));
                    if(ref!=null&&!StringUtils.roughEqual("ref","")){
                        constProp=new PropertyValue(index,ref.concat("#"),true);
                    }else{
                        constProp=new PropertyValue(index,value,true);
                    }
                }
                constructorValues.add(constProp);
            }

            constructorArgumentValues=new ConstructorArgumentValues(beanClass,constructorValues);
            ObjectUtils.setter(definition,"constructorArgumentValues",constructorArgumentValues);


            //提取Setter
            List<Element> setterEles=beanElement.elements("property");
            List<PropertyValue> setterProps=new ArrayList<>(setterEles.size());
            OptionalPropertyValues setterValue=null;
            for(Element element:setterEles){
                PropertyValue setterProp=null;
                String targetName=element.attributeValue("name");
                String value=element.attributeValue("value");
                String ref=element.attributeValue("ref");
                if(!StringUtils.roughEqual(targetName,"")){
                    if(!StringUtils.roughEqual("ref","")){
                        setterProp=new PropertyValue(targetName,ref.concat("#"),true);
                    }else{
                        setterProp=new PropertyValue(targetName,value,true);
                    }
                }
                setterProps.add(setterProp);
            }
            setterValue=new OptionalPropertyValues(beanClass,setterProps);
            ObjectUtils.setter(definition,"optionalPropertyValues",setterValue);

            //提取lookup-method
            List<Element> lookups=beanElement.elements("lookup-method");
            Map<String,String> lookupProp=null;
            if(lookups!=null&&lookups.size()!=0){
                lookupProp=new HashMap<>();
                for(Element lookup:lookups){
                    String method=lookup.attributeValue("method");
                    String beanName=lookup.attributeValue("bean");
                    lookupProp.put(method,beanName);
                }

            }
            if(lookupProp!=null&&!lookupProp.isEmpty()){
                ObjectUtils.setter(definition,"lookupMethod",lookupProp);
            }

            //提取replace-method
            List<Element> replaces=beanElement.elements("replace-method");
            Map<String,String> replaceProp=null;
            if(replaces!=null&&!replaces.isEmpty()){
                replaceProp=new HashMap();
                for(Element replace:replaces){
                    String method =replace.attributeValue("method");
                    String replaceMethod=replace.attributeValue("replace");
                    replaceProp.put(method,replaceMethod);
                }
            }
            if(replaceProp!=null&&!replaceProp.isEmpty()){
                ObjectUtils.setter(definition,"replaceMethod",replaceProp);
            }

            //提取初始化方法
            Element initEle=beanElement.element("init-method");
            PropertyValue initProp=null;
            if(initEle!=null){
                String initMethod=initEle.attributeValue("method");
                initProp=new PropertyValue("init-method",initMethod);
            }
            if(initProp!=null){
                ObjectUtils.setter(definition,"initMethod",initProp.getValue());
            }

            //提取销毁方法
            Element destroyEle=beanElement.element("destroy-method");
            PropertyValue destroyProp=null;
            if(destroyEle!=null){
                String destroyMethod=destroyEle.attributeValue("method");
                destroyProp=new PropertyValue("destroy-method",destroyMethod);
            }
            if(destroyProp!=null){
                ObjectUtils.setter(definition,"destroyMethod",destroyProp.getValue());
            }

            //提取单例方法
            Element factoryEle=beanElement.element("factory-method");
            PropertyValue factoryProp=null;
            if(factoryEle!=null){
                String factoryMethod=factoryEle.attributeValue("method");
                factoryProp=new PropertyValue("factory-method",factoryMethod);
            }
            if(factoryProp!=null){
                ObjectUtils.setter(definition,"factoryMethod",factoryProp.getValue());
            }

            //提取bean作用域
            String scope=beanElement.attributeValue("scope");
            if(scope==null){
                scope=BeanDefinition.SCOPE_SINGLETON;
            }
            ObjectUtils.setter(definition,"scope",scope);

            return definition;
        }

        private static BeanDefinition[] parseImportElement(Element element){
            String resources=element.attributeValue("resource");
            boolean overriding=Boolean.valueOf(element.attributeValue("overriding"));
            EncodeResource resource=new EncodeResource(resources);
            return null;
        }

        private static BeanDefinition parseImportClassElement(Element element){
            return null;
        }

        private static BeanDefinition parseEnvElement(Element element){
            return null;
        }

        private static BeanDefinition parseAliasElement(Element element){
            return null;
        }

        protected static List<BeanDefinition> parseCustomizedInternal(Element element){
            for(NameSpaceHandler handler:handlers){
                if(handler.supports(element.getNamespaceURI())){
                    return handler.parse(element);
                }
            }
            throw new IllegalStateException("No found or no defined supports"+element.getNamespaceURI()+"handler(s)，check the config file!");
        }

        private static boolean isDefaultBeanEles(Element element){
            for(String defalutEle:DEFAULT_BEAN_ELEMENTS){
                if(element.getName().equals(defalutEle)){
                    return true;
                }
            }
            return false;
        }


        //暂未启用
        private static class DefaultNameSpaceHanler implements NameSpaceHandler {

            @Override
            public boolean supports(String nameSpace) {
                if (nameSpace != null && nameSpace.equals("beans")) {
                    return true;
                }
                return false;
            }
            @Override
            public List<BeanDefinition> parse(Element element) {
                return null;
            }
        }
    }

    static {
        //进行环境检查 要求JDK1.8+
        String jdkVersion=System.getProperty("java.version");
        String jdkRoughVersion=jdkVersion.substring(0,3);
        double version=Double.parseDouble(jdkRoughVersion);
        if(version<1.8){
            throw new Error("sorry , the current jdk version "+jdkVersion+" not meeting minimum requirements ,jdk rough version must be >= 1.8" );
        }
    }

}