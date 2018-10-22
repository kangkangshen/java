package archer.util;/*
 *@author:wukang
 */

import java.io.File;

public abstract class FileUtils {
    public static boolean isExist(String file){
        File targetFile=new File(file);
        return targetFile.exists();
    }
    public static boolean isOpen(String file){
        return false;
    }

}
