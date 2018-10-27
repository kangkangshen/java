package archer.container;


import java.util.List;

public interface BeanAliasRegistrar {

    void registerAlias(String beanName,String alias);

    void registerAliases(String beanName,String[] aliases);

    void removeAllAliases(String beanName);

    boolean hasAnyAlias(String beanName);

    List<String> getAliases(String beanName);

    boolean isAlias(String name,String alias);

    String getAutonym(String alias);

}
