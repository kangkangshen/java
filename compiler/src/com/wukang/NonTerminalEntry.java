package com.wukang;/*
 *@author:wukang
 */


import com.wukang.util.Description;

@Description("非终结符类型统一为-1")
public class NonTerminalEntry extends Entry {
    public NonTerminalEntry(String literal) {
        super(literal, -1);
    }
}
