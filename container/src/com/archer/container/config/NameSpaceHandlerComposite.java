package archer.container.config;/*
 *@author:wukang
 */

import archer.container.BeanContainer;
import archer.container.definition.BeanDefinition;
import archer.container.support.NoSuchBeanException;
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
