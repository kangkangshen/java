package archer.definition;/*
 *@author:wukang
 */

import archer.PropertyValue;
import archer.config.XmlReader;

import java.util.HashMap;
import java.util.Map;

public class MethodOverrides {
    public static final String[] SUPPORTED_METHOD_OVERRIDES_TYPE={"replace","lookup"};
    private Map<String, PropertyValue> overrides=new HashMap<>();
    public void validate() {

    }

}
