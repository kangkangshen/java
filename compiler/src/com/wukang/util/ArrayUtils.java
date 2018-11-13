package com.wukang.util;/*
 *@author:wukang
 */


import java.util.ArrayList;
import java.util.List;

public class ArrayUtils {
    public static boolean contains(Object[] arr,Object item){
        for(Object o:arr){
            if(o.equals(item)){
                return true;
            }
        }
        return false;
    }

    public static<T> int addAll(List<T> list, T[] arr){
        List<T> temp=new ArrayList<>(arr.length);
        int i=0;
        for(T t:arr){
            temp.add(t);
            i++;
        }
        list.addAll(temp);
        return i;
    }
}