package com.wukang;/*
 *@author:wukang
 */

public class VariableEntry extends Entry{
    public VariableEntry(String literal, int type, int offset) {
        super(literal, type,offset);
    }

    @Override
    public String toString() {
        return this.literal+"\t"+this.type+"\t"+this.offset;
    }
}
