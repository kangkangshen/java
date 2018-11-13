package com.wukang;/*
 *@author:wukang
 */

public class ConstantEntry extends Entry {
    private int realValue;
    public ConstantEntry(String literal, int type,int realValue,int offset) {
        super(literal, type,offset);
        this.realValue=realValue;
    }

    @Override
    public String toString() {
        return this.literal+"\t"+this.type+"\t"+this.offset;
    }
}
