package archer.aop;

import archer.container.support.BeanPostProcessor;
import archer.container.support.debug.Description;

@Description(description = "该接口赋予处理器（或者说插件）接管创建bean的创建功能")
public interface BeanInstantiationPostProcessor extends BeanPostProcessor {

    Object postProcessBeforBeanInstantiation(Object rawBean,Object[] args);

    Object postProcessAfterBeanInstantiation(Object rawBean,Object[] args);
}
