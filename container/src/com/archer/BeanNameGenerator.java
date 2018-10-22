package archer;

import archer.definition.BeanDefinition;
import archer.support.BeanWrapper;
import archer.support.DefaultBeanNameGenerator;

public interface BeanNameGenerator {

    String generateBeanName(BeanWrapper wrapper);

    BeanNameGenerator getWrapedImpl();

    String generateBeanName(BeanDefinition definition);

}
