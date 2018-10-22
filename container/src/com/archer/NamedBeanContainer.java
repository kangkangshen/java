package archer;
/*
允许为BeanContainer设置名字和ID ID唯一标识一个容器，
当不设置id时将会采用默认策略为其生成ID，当name为null时
使用id作为getName返回值
 */

import archer.support.ListableBeanContainer;

public interface NamedBeanContainer extends ListableBeanContainer {

    void setName(String name);

    String getName(String name);

    void setId(String id);

    String getId();



}
