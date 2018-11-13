package com.wukang;/*
 *@author:wukang
 */

public abstract class Entry {
    protected String literal; //字面值，比如CONST,VARIBLES,a
    protected int type;
    protected int offset;
    public Entry(String literal,int type){
        this.literal=literal;
        this.type=type;
    }
    public Entry(String literal,int type,int offset){
        this.literal=literal;
        this.type=type;
        this.offset=offset;
    }

    public String getLiteral() {
        return literal;
    }

    public int getType() {
        return type;
    }
}
