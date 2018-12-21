package com.wukang;/*
 *@author:wukang
 */


public abstract class TableItem {
    protected String literals;
    protected int realVal;
    protected int offset;

    public TableItem(int realVal,int offset){
        this.literals=literals;
        this.realVal=realVal;
        this.offset=offset;
    }

    public TableItem(String literals,int offset){
        this.literals=literals;
        this.offset=offset;
    }

    public static class Pointer<K,V>{
        private K itself;
        private V target;
        private int offset;
        public Pointer(K itself,V target,int offset){
            this.itself=itself;
            this.target=target;
            this.offset=offset;
        }
    }

}
