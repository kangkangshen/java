package archer.container.support;

import archer.container.BeanContainer;
import archer.container.definition.BeanDefinition;

import java.util.List;

/*
添加 add,remove,getNames等方法
 */
public interface ListableBeanContainer extends BeanContainer {
   void addBean(String name,Object bean);

   void removeBean(String name);

   void removeBean(Class<?> type);

   List<String> getBeanNames();

   int getBeanCount();

   BeanDefinition getBeanDefinition(String name);

   List<BeanDefinition> getBeanDefinitions();

   List<BeanDefinition> getBeanDefinition(Class<?> type);

















}
