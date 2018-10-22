package archer.util;/*
 *@author:wukang
 */

/*
暂未启用
 */
public class EncodeResource extends Resource {
    private static String DEFAULT_ENCODING="utf-8";
    private String encode;
    public EncodeResource(String filelocation) {
        super(filelocation);
        this.encode=DEFAULT_ENCODING;
    }
    public EncodeResource(String fileLocations,String encode){
        super(fileLocations);
        this.encode=encode;
    }
    public String getEncode(){
        return encode;
    }
}
