package com.helison.archer.protocol.function;/*
 *@author:wukang
 */


import com.helison.archer.annotation.API;

@API
public class  Connection {
    /*
    命令列表
     */
    private static final String CLASS="Connection";
    public static final Command START = new StandardCommand(CLASS,"Start");
    public static final Command BREAK = new StandardCommand(CLASS,"Break");





}
