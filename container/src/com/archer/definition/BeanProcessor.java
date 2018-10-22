package archer.definition;

public interface BeanProcessor {

    boolean interested (Class<?> beanClass);

    boolean interested(String beanName);

    boolean interested(String beanName,Class<?> beanClass);

    boolean interested(Object args);

    void postProcess();

}
