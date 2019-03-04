package com.helison.archer.protocol.function;/*
 *@author:wukang
 */

import com.helison.archer.utils.Assert;
import com.helison.archer.utils.StringUtils;

/*
包级私有，不公开
 */
class StandardCommand implements Command{

    private String className;
    private String functionName;


    public StandardCommand(String className,String functionName){
        Assert.notNull(className,functionName);
        Assert.mustTrue(!(StringUtils.roughEqual("",className)||StringUtils.roughEqual("",functionName)));
        this.className=className;
        this.functionName=functionName;
    }

    @Override
    public String getClassName() {
        return className;
    }

    @Override
    public String getFunctionName() {
        return functionName;
    }
}
