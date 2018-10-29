package archer.container.support;

public interface BeanPostProcessor {
    Object processRawBeanBeforeInitialization(Object rawBean);
    Object processInstanceAfterInitialization(Object bean);
}
