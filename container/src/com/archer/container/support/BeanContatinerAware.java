package archer.container.support;

import archer.container.Aware;
import archer.container.BeanContainer;

public interface BeanContatinerAware extends Aware {
    void setBeanContainer(BeanContainer container);
}
