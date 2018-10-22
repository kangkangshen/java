package archer.config;/*
 *@author:wukang
 */

import archer.BeanContainer;
import archer.definition.BeanDefinition;
import archer.support.NoSuchBeanException;
import org.dom4j.Element;


import java.util.List;

public class NameSpaceHandlerComposite implements NameSpaceHandler {

    private List<NameSpaceHandler> handlerList;

    @Override
    public boolean supports(String nameSpace) {
        for(NameSpaceHandler handler:handlerList){
            if(handler.supports(nameSpace)){
                return true;
            }
        }
        return false;
    }

    @Override
    public List<BeanDefinition> parse(Element element) {
        for(NameSpaceHandler handler:handlerList){
            if(handler.supports(element.getNamespaceURI())){
                return handler.parse(element);
            }
        }
        return null;
    }
    public void addNameSpaceHandler(NameSpaceHandler handler){
        handlerList.add(handler);
    }
    public void fetchAndAddFromContainer(BeanContainer container) throws NoSuchBeanException {

        List<NameSpaceHandler> handlers = (List<NameSpaceHandler>) container.getBean(NameSpaceHandler.class);
    }
}
