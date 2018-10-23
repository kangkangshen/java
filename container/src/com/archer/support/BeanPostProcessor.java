package archer.support;

public interface BeanPostProcessor {
    Object processRawBeanBeforeInstantiation(Object rawBean);
    Object processInstanceAfterInstantiation(Object bean);
}
