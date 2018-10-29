package archer.container.support.ext;

import archer.container.support.ConfigurableBeanContainer;

public interface ExtensibleBeanContainer  {
    void detectAllBeanPostProcessor();
    void detectAllBeanContainerProcessor();
    Object applyBeanPostProcessorBeforeInitialization(Object o);
    Object applyBeanPostProcessorAfterInitialization(Object o);
    void applyBeanContainerPostProcessor();
}
