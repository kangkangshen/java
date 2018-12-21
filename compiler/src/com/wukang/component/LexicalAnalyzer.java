package com.wukang.component;
/*
 *@author:wukang
 */
import com.wukang.*;
import com.wukang.util.ArrayUtils;
import sun.java2d.opengl.WGLSurfaceData;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

//词法分析器
public class LexicalAnalyzer {

    private String fileLocation;
    private Queue<Entry> wordQueue;
    private List<VariableNameItem> variableNameItems;
    private List<ConstantItem> constantItems;

    public LexicalAnalyzer(String fileLocation){
        this.fileLocation=fileLocation;
        wordQueue=new LinkedList<>();
        variableNameItems=new LinkedList<>();
        constantItems=new LinkedList<>();
    }

    public boolean isConst(String str){
        return SystemWord.CONST.equals(str);
    }

    public boolean isVariable(String str){
        return SystemWord.VARIBLE.equals(str);
    }

    public boolean isAssign(String str){
        return SystemWord.ASSIGN.equals(str);
    }

    public boolean isBegin(String str){
        return SystemWord.BEGIN.equals(str);
    }

    public boolean isEnd(String str){
        return SystemWord.END.equals(str);
    }

    public boolean isProcedure(String str){
        return SystemWord.PROCEDURE.equals(str);
    }

    public boolean isMathematics(String str){
        return ArrayUtils.contains(SystemWord.MATHEMATICS,str);
    }

    public boolean isLogistics(String str){
        return ArrayUtils.contains(SystemWord.LOGISTICS,str);
    }

    public boolean isCall(String str){
        return SystemWord.CALL.equals(str);
    }

    public boolean isRead(String str) {return SystemWord.READ.equals(str);}

    public boolean isWrite(String str){return SystemWord.WRITE.equals(str);}

    public boolean isTerminator(String str){
        return SystemWord.TERMINATOR.equals(str);
    }

    public boolean isSeparator(String str){
        return SystemWord.SEPARATOR.equals(str);
    }

    public boolean isLeftParenthesis(String str){
        return SystemWord.LEFT_PARENTHESIS.equals(str);
    }

    public boolean isRightParenthesis(String str){
        return SystemWord.RIGHT_PARENTHESIS.equals(str);
    }

    public boolean isPeriod(String str){
        return SystemWord.PERIOD.equals(str);
    }

    public boolean isODD(String str){return SystemWord.ODD.equals(str);}

    public boolean isVariableName(String str){
        for(int i=0;i<str.length();i++){
            if(!((str.charAt(i)>='0'&&str.charAt(i)<='9')||(str.charAt(i)>='a'&&str.charAt(i)<='z')||(str.charAt(i)>='A'&&str.charAt(i)<='Z'))){
                return false;
            }
        }
        return true;
    }

    public boolean isConstant(String str){
        try{
            Integer.parseInt(str);
        }catch(NumberFormatException e){
            return false;
        }
        return true;
    }

    private String digestTxt() throws IOException {
        if(fileLocation!=null){
            BufferedReader reader=new BufferedReader(new FileReader(fileLocation));
            String words="";
            String temp=null;
            while((temp=reader.readLine())!=null){
                words+=temp;
            }
            return words;
        }else{
            throw new IllegalArgumentException("insert a null fileLocation");
        }
    }

    public int analyze(){
        String orginalWords= null;
        try {
            orginalWords = digestTxt();
        } catch (IOException e) {
            throw new IllegalStateException("occure a exception when digest txt");
        }
        String handledWors=insertWhiteSpace(orginalWords);
        List<String> words= new LinkedList<>();
        String[] splitedWords=handledWors.split(" |\t|\n");
        for(int i=0;i<splitedWords.length;i++){
            if(!splitedWords[i].equals("")){
                words.add(splitedWords[i]);
            }
        }
        int i=0;
        if(words==null||words.isEmpty()){
            System.err.println("none scan");
            return 0;
        }else{
            int variableOffset=0;
            int constantOffset=0;
            for(String word:words){
                if(isConst(word)){
                    wordQueue.add(new SystemWordEntry(word,0));
                }else if(isVariable(word)){
                    wordQueue.add(new SystemWordEntry(word,1));
                }else if(isAssign(word)){
                    wordQueue.add(new SystemWordEntry(word,2));
                }else if(isBegin(word)){
                    wordQueue.add(new SystemWordEntry(word,3));
                }else if(isEnd(word)){
                    wordQueue.add(new SystemWordEntry(word,4));
                }else if(isProcedure(word)){
                    wordQueue.add(new SystemWordEntry(word,5));
                }else if(isMathematics(word)){
                    wordQueue.add(new SystemWordEntry(word,6));
                }else if(isLogistics(word)){
                    wordQueue.add(new SystemWordEntry(word,7));
                }else if(isCall(word)){
                    wordQueue.add(new SystemWordEntry(word,8));
                }else if(isRead(word)){
                    wordQueue.add(new SystemWordEntry(word,9));
                }else if(isWrite(word)){
                    wordQueue.add(new SystemWordEntry(word,10));
                }else if(isTerminator(word)){
                    wordQueue.add(new SystemWordEntry(word,11));
                }else if(isSeparator(word)){
                    wordQueue.add(new SystemWordEntry(word,12));
                }else if(isLeftParenthesis(word)){
                    wordQueue.add(new SystemWordEntry(word,13));
                }else if(isRightParenthesis(word)){
                    wordQueue.add(new SystemWordEntry(word,14));
                }else if(isPeriod(word)){
                    wordQueue.add(new SystemWordEntry(word,15));
                }else if(isODD(word)){
                    wordQueue.add(new SystemWordEntry(word,16) );
                }else if(isConstant(word)){
                    wordQueue.add(new ConstantEntry(word,17,Integer.parseInt(word),constantOffset));
                    constantItems.add(new ConstantItem(word,Integer.parseInt(word),constantOffset));
                    constantOffset++;
                }else if(isVariableName(word)){
                    wordQueue.add(new VariableEntry(word,18,variableOffset));
                    variableNameItems.add(new VariableNameItem(word,variableOffset));
                    variableOffset++;
                }else {
                    throw new RuntimeException("lexical analyze failed,located at "+i+" .");
                }
                i++;
            }
            System.out.println("lexical analyze succeed,resolve "+i+" words");
            return i;
        }
    }

    public Queue<Entry> getWordQueue() {
        return wordQueue;
    }

    public List<ConstantItem> getConstantItems() {
        return constantItems;
    }

    public List<VariableNameItem> getVariableNameItems() {
        return variableNameItems;
    }

    private String insertWhiteSpace(String orginalWords){

        Character[] delimiters={'+','-','*','/',',',';','.','(',')','=','#'};
        Character[] dualOperatorsPre={':','<','>'};
        String[] dualOperators={":=","<=",">="};
        String temp="";
        int i=0;
        while(i<orginalWords.length()){
            if(ArrayUtils.contains(delimiters,orginalWords.charAt(i))){
                String op=orginalWords.charAt(i)+"";
                String pre=orginalWords.substring(0,i);
                String post=orginalWords.substring(i+1);
                orginalWords=pre+" "+op+" "+post;
                i=i+2;
            }else if(ArrayUtils.contains(dualOperatorsPre,orginalWords.charAt(i))){
                String op=orginalWords.substring(i,i+2);
                if(ArrayUtils.contains(dualOperators,op)){
                    String pre=orginalWords.substring(0,i);
                    String post=orginalWords.substring(i+2);
                    orginalWords=pre+" "+op+" "+post;
                    i=i+3;
                }else{
                    op=orginalWords.charAt(i)+"";
                    String pre=orginalWords.substring(0,i);
                    String post=orginalWords.substring(i+1);
                    orginalWords=pre+" "+op+" "+post;
                    i=i+2;
                }

            }else{
                i=i+1;
            }

        }
        return orginalWords;
    }
}
