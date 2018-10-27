package archer.container;

import archer.container.definition.BeanDefinition;
import archer.container.support.BeanWrapper;

public interface BeanNameGenerator {

    String generateBeanName(BeanWrapper wrapper);

    BeanNameGenerator getWrapedImpl();

    String generateBeanName(BeanDefinition definition);

}
