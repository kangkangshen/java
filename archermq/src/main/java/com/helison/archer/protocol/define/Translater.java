package com.helison.archer.protocol.define;/*
 *@author:wukang
 */

/*
函数式接口，实现将 P类型的实例 翻译成 R类型的实例
 */
public interface Translater<P,R> {
    R translate(P msg);
}
