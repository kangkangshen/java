package archer.definition;

public interface BeanPostProcessor {
    Object processRawBeanBeforeInstantiation(Object rawBean);
    Object processInstanceAfterInstantiation(Object bean);
}
