package archer.container;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Objects;
import java.util.logging.FileHandler;

public interface BeanPropertiesAccessor {

    void setter(String name,Object value);

    Object getter(String name);

    void setIfNull(String name,Object value);

    Modifier getModifier(String name);


}
