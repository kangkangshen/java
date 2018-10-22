package archer.support.ext;

import archer.support.ConfigurableBeanContainer;

public interface ExtensibleBeanContainer  {
    void detectAllBeanPostProcessor(ConfigurableBeanContainer container);
    void detectAllBeanContainerProcessor(ConfigurableBeanContainer container);
    Object applyBeanPostProcessorBeforeInstantiation(Object o,Object[] args);
    Object applyBeanPostProcessorAfterInstantiation(Object o,Object[] args);
    void applyBeanContainerPostProcessor(ConfigurableBeanContainer container);
}
