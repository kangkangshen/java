package archer.container.util;/*
 *@author:wukang
 */

import archer.container.support.debug.Description;

public class ArrayUtils {

    @Description(description = "判断item是否属于数组array")
    public static boolean belongTo(Object[] array,Object item){
        for(Object o:array){
            if(o==null&&item==null){
                return true;
            }
            if(o.equals(item)){
                return true;
            }
        }
        return false;
    }

    @Description(description = "判断var2中的元素是否都在var1中存在")
    public static boolean containTo(Object[] var1,Object[] var2){
      return false;
    }


    public static int getIndexOf(Object[] array,Object item){
        for(int i=0;i<array.length;i++){
            if(array[i]==null&&item==null){
                return i;
            }
            else if(array[i].equals(item)){
              return i;
            }
        }
        return -1;
    }
}
