package com.helison.archer.utils;
/*
 *@author:wukang
 */

public final class Assert {
    private Assert(){}

    public static void notNull(Object...arg){
        for(int i=0;i<arg.length;i++){
            if(arg[i]==null){
                throw new NullPointerException("the "+i+"th arg not allowed to be null");
            }
        }
    }
    public static void mustTrue(boolean expression){
        if(!expression){
            throw new IllegalArgumentException("expression must be true");
        }

    }
}
