package com.wukang;/*
 *@author:wukang
 */

public class ConstantItem extends TableItem {
    private String literal;
    public ConstantItem(String literal,int realVal, int offset) {
        super(realVal, offset);
        this.literal=literal;
    }

    public String getLiteral() {
        return literal;
    }

    public void setLiteral(String literal) {
        this.literal = literal;
    }
}
