package archer.config;

import archer.definition.BeanDefinition;
import org.dom4j.Element;

import java.util.List;


public interface NameSpaceHandler {
    boolean supports(String nameSpace);

    List<BeanDefinition> parse(Element element);
}
