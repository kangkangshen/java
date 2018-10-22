package archer.support;

import archer.Aware;
import archer.BeanContainer;

public interface BeanContatinerAware extends Aware {
    void setBeanContainer(BeanContainer container);
}
