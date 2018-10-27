package archer.container.util;/*
 *@author:wukang
 */

import java.io.File;
import java.io.InputStream;

public class Resource {
    private static final String[] SUPPORTED_FILE_TYPES={".xml"};
    private static final String MUTIPLE="*";
    private static final String RELATIVE="relative:";
    private static final String SEARCH="search:";
    private static final String CLASSPATH="classpath:";
    private String filelocation;
    private File targetFile;
    public Resource(String filelocation){
        this.filelocation=filelocation;
        this.checkValidity();
        this.targetFile=parse(filelocation);
    }
    public File getFile(){
        return targetFile;
    }
    public InputStream getInputStream(){
        return null;
    }
    protected void checkValidity(){
        String fileSuffix=filelocation.substring(filelocation.lastIndexOf('.'));
        for(String support:SUPPORTED_FILE_TYPES){
            if(!support.equals(fileSuffix.trim())){
                throw new UnsupportedOperationException("This format is temporarily not supported");
            }
        }
    }
    private File parse(String configlocation){
        String realLocation=null;
        if(configlocation.startsWith(CLASSPATH)){
            String classPathPrefix=this.getClass().getResource("/").getPath();
            realLocation=classPathPrefix+configlocation.substring(CLASSPATH.length());
        }
        else if(configlocation.startsWith(RELATIVE)){
            throw new UnsupportedOperationException();
        }
        else if(configlocation.startsWith(SEARCH)){
            throw new UnsupportedOperationException();
        }else{
           realLocation=configlocation;
        }
        if(!StringUtils.roughEqual("",realLocation)){
            return new File(realLocation);
        }
        return null;
    }

}
