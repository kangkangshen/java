package com.wukang;/*
 *@author:wukang
 */

public class SystemWordEntry extends Entry {

    public SystemWordEntry(String literal, int type) {
        super(literal, type);
    }

    @Override
    public String toString() {
        return this.literal+"\t"+this.type;
    }


}
