package archer.support;/*
 *@author:wukang
 */

import archer.BeanAliasRegistrar;
import archer.BeanDefinitionRegistrar;
import archer.NamedBeanContainer;
import archer.config.Configuration;
import archer.definition.BeanDefinition;
import org.dom4j.Document;
import java.io.File;

/*
给予容器再一次解析文件的机会
其中对于方法extractBeanFrom(String)，框架将对给的字符串有效性将做出判断
对于extractBeanFrom(File)，需要依赖用户自行保证给与文件的有效性
 */
public interface ConfigurableBeanContainer extends NamedBeanContainer, BeanAliasRegistrar, BeanDefinitionRegistrar {

    int extractBeanFrom(String fileName);

    int extractBeanFrom(File file);

    int extractBeanFrom(String[] file);

    int extractBeanFrom(File[] files);

    void setConfigLocation(String configLocation);

    void setConfiguration(Configuration configLocation);

    Configuration getConfiguration();

    boolean isNeedValidation();



}

