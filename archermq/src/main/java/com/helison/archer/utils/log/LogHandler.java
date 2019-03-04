package com.helison.archer.utils.log;/*
 *@author:wukang
 */

import java.util.logging.Logger;

public interface LogHandler {
    Logger getLogger();
    default void log(){

    }
}
