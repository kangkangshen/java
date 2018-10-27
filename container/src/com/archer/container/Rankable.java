package archer.container;

import archer.container.support.debug.Description;


@Description(description = "当出现定义多重相同类型的单例bean时，需要进行排名找出最优bean进行调用，若未实现此接口，可能抛出AmbiguousInvokeOrReturnException")
public interface Rankable {

    int HIGHEST_PRIORITY =Integer.MAX_VALUE;
    int UPPER_MIDDLE=1000;
    int MIDDLE_PRIORITY=0;
    int LOWER_MIDDLE=-1000;
    int LOWEST_PRIORITY =Integer.MIN_VALUE;

    @Description(description = "返回值int代表当前bean的优先级，未考虑优先级程度，当前返回int值而非枚举，返回值越大，优先级越高")
    int rank();
}
