package com.wukang.component;
/*
 *@author:wukang
 */

import com.wukang.*;
import com.wukang.util.Description;
import com.wukang.util.ArrayUtils;
import java.util.Queue;

public class SyntacticParser {

    private Queue<Entry> wordsQueue;

    public SyntacticParser(){}

    public SyntacticParser(Queue<Entry> wordsQueue){
        this.wordsQueue=wordsQueue;
    }

    @Description("接口函数")
    public boolean parse(){
        role$0();
        return true;
    }

    @Description("<程序>→〈分程序〉.")
    public void role$0(){
        role$1();
        if(SystemWord.PERIOD.equals(wordsQueue.poll().getLiteral())){
            System.out.println("succeed");
        }else{
            throw new IllegalStateException("failed");
        }
    }

    @Description("<分程序>→ [<常量说明部分>][<变量说明部分>][<过程说明部分>]〈语句〉")
    public void role$1(){
        String nextToken=wordsQueue.peek().getLiteral();
        if(SystemWord.CONST.equals(nextToken)){
            role$2();
            nextToken=wordsQueue.peek().getLiteral();
        }
        if(SystemWord.VARIBLE.equals(nextToken)){
            role$5();
            nextToken=wordsQueue.peek().getLiteral();
        }
        if(SystemWord.PROCEDURE.equals(nextToken)){
            role$7();
        }
        role$9();
    }

    @Description("<常量说明部分> → CONST<常量定义>{ ,<常量定义>};")
    public void role$2(){
        if("const".equals(wordsQueue.poll().getLiteral())){
            role$3();
            String nextToken=wordsQueue.peek().getLiteral();
            while(SystemWord.SEPARATOR.equals(nextToken)){
                nextToken=wordsQueue.poll().getLiteral();
                role$3();
            }
            if(!SystemWord.TERMINATOR.equals(wordsQueue.poll().getLiteral())){
                throw new IllegalStateException("failed");
            }
        }else{
            throw new IllegalStateException("failed");
        }
    }

    @Description("<常量定义> → <标识符>=<无符号整数>")
    public void role$3(){
        role$6();
        if("=".equals(wordsQueue.poll().getLiteral())){
            role$4();
        }else{
            throw new IllegalStateException("failed");
        }
    }

    @Description("<无符号整数> → <数字>{<数字>}")
    public void role$4(){
        try{
            String nextToken=wordsQueue.poll().getLiteral();
            Integer.parseInt(nextToken);
        }catch (NumberFormatException e){
            throw new IllegalStateException("failed");
        }
    }

    @Description("<变量说明部分> → VAR<标识符>{ ,<标识符>};")
    public void role$5(){
        if(SystemWord.VARIBLE.equals(wordsQueue.poll().getLiteral())){
            role$6();
            String nextToken=wordsQueue.poll().getLiteral();
            while(SystemWord.SEPARATOR.equals(nextToken)){
                role$6();
                nextToken=wordsQueue.poll().getLiteral();
            }
            if(!SystemWord.TERMINATOR.equals(nextToken)){
                throw new IllegalStateException("failed");
            }
        }
    }

    @Description("<标识符> → <字母>{<字母>|<数字>}")
    public void role$6(){
        String nextToken=wordsQueue.poll().getLiteral();
        if(!nextToken.matches("^[a-zA-Z]+([a-zA-Z0-9])*")){
            throw new IllegalStateException("failed");
        }
    }

    @Description("<过程说明部分> → <过程首部><分程序>;{<过程说明部分>}")
    public void role$7(){
        role$8();
        role$1();
        if(SystemWord.TERMINATOR.equals(wordsQueue.poll().getLiteral())){
            //check next token is procedure ,beacuse <过程说明部分> 's FIRST is procedure
            String nextToken=wordsQueue.peek().getLiteral();
            if(SystemWord.PROCEDURE.equals(nextToken)){
                role$7();
            }
        }
    }

    @Description("<过程首部> → procedure<标识符>;")
    public void role$8(){
        if(SystemWord.PROCEDURE.equals(wordsQueue.poll().getLiteral())){
            role$6();
            if(!SystemWord.TERMINATOR.equals(wordsQueue.poll().getLiteral())){
                throw new IllegalStateException("failed");
            }
        }else{
            throw new IllegalStateException("failed");
        }
    }

    @Description("<语句> → <赋值语句>|<条件语句>|<当型循环语句>|<过程调用语句>|<读语句>|<写语句>|<复合语句>|<空>")
    public void role$9(){
        String nextToken=wordsQueue.peek().getLiteral();
        String[] follow={SystemWord.END,SystemWord.TERMINATOR};
        if(SystemWord.IF.equals(nextToken)){
            //match 条件语句 's FIRST
            role$19();
        }else if(SystemWord.WHILE.equals(nextToken)){
            //match 当性循环语句 's FIRST
            role$21();
        }else if(SystemWord.CALL.equals(nextToken)){
            //match 过程调用语句 's FIRST
            role$20();
        }else if(SystemWord.READ.equals(nextToken)){
            //match 读语句 's FIRST
            role$22();
        }else if(SystemWord.WRITE.equals(nextToken)){
            //match 写语句 's FIRST
            role$23();
        }else if(SystemWord.BEGIN.equals(nextToken)){
            //match 复合语句 's FIRST
            role$11();
        }else if(SystemWord.PERIOD.equals(nextToken)){
            //match current role 's FOLLOW
            ;
        }else if(ArrayUtils.contains(follow,nextToken)){
            //此处应是语句的FOLLOW集,此处取空
            ;
        }else if(nextToken.matches("^[a-zA-Z]+([a-zA-Z0-9])*")){
            //match 赋值语句 's FIRST
            role$10();
        }else{
            throw new IllegalStateException("failed");
        }
    }

    @Description("<赋值语句> → <标识符>:=<表达式>")
    public void role$10(){
        role$6();
        if(SystemWord.ASSIGN.equals(wordsQueue.poll().getLiteral())){
            role$13();
        }else{
            throw new IllegalStateException("failed");
        }
    }

    @Description("<复合语句> → begin<语句>{;<语句>}end")
    public void role$11(){
        if(SystemWord.BEGIN.equals(wordsQueue.poll().getLiteral())){
            role$9();
            String nextToken=wordsQueue.peek().getLiteral();
            while(SystemWord.TERMINATOR.equals(nextToken)){
                wordsQueue.poll();
                role$9();
                nextToken=wordsQueue.peek().getLiteral();
            }
            if(!SystemWord.END.equals(wordsQueue.poll().getLiteral())){
                throw new IllegalStateException("failed");
            }
        }else{
            throw new IllegalStateException("failed");
        }
    }

    @Description("<条件> → <表达式><关系运算符><表达式>|odd<表达式>")
    public void role$12(){
        String nextToken=wordsQueue.peek().getLiteral();
        if(SystemWord.ODD.equals(nextToken)){
            wordsQueue.poll();
            role$13();
        }else{
            role$13();
            role$18();
            role$13();
        }
    }

    @Description("<表达式> → [+|-]<项>{<加减运算符><项>}")
    public void role$13(){
        String nextToken=wordsQueue.peek().getLiteral();
        if("+".equals(nextToken)||"-".equals(nextToken)){
            role$16();
        }
        role$14();
        nextToken=wordsQueue.peek().getLiteral();
        if("+".equals(nextToken)||"-".equals(nextToken)){
            role$13();
        }
    }

    @Description("<项> → <因子>{<乘除运算符><因子>}")
    public void role$14(){
        role$15();
        String nextToken=wordsQueue.peek().getLiteral();
        while("*".equals(nextToken)||"/".equals(nextToken)){
            wordsQueue.poll().getLiteral();
            role$15();
            nextToken=wordsQueue.peek().getLiteral();
        }
    }

    @Description("<因子> → <标识符>|<无符号整数>|(<表达式>)")
    public void role$15(){
        String nextToken=wordsQueue.peek().getLiteral();
        if(nextToken.matches("^[a-zA-Z]+([a-zA-Z0-9])*")){
            //check identifier
            wordsQueue.poll();
        }else if(nextToken.matches("([0-9])*")){
            //check integer
            wordsQueue.poll();
        }else if(SystemWord.LEFT_PARENTHESIS.equals(nextToken)){
            //check 表达式 FOLLOW
            role$13();
            if(!SystemWord.RIGHT_PARENTHESIS.equals(nextToken=wordsQueue.poll().getLiteral())){
                throw new IllegalStateException("failed");
            }
        }else{
            //must choose one sub item
            throw new IllegalStateException("failed");
        }
    }

    @Description("<加减运符> → +|-")
    public void role$16(){
        String word=wordsQueue.poll().getLiteral();
        if("+".equals(word)||"-".equals(word)){
            return ;
        }else{
            throw new IllegalStateException("failed");
        }
    }

    @Description("<乘除运算符> → *|/")
    public void role$17(){
        String word=wordsQueue.poll().getLiteral();
        if("*".equals(word)||"/".equals(word)){
            return ;
        }else{
            throw new IllegalStateException("failed");
        }
    }

    @Description("<关系运算符> → =|#|<|<=|>|>=")
    public void role$18(){
        if(ArrayUtils.contains(SystemWord.LOGISTICS,wordsQueue.poll().getLiteral())){
            return ;
        }else{
            throw new IllegalStateException("failed");
        }
    }

    @Description("<条件语句> → if<条件>then<语句>")
    public void role$19(){
        if(SystemWord.IF.equals(wordsQueue.poll().getLiteral())){
            role$12();
            if(SystemWord.THEN.equals(wordsQueue.poll().getLiteral())){
                role$9();
            }else{
                throw new IllegalStateException("failed");
            }
        }else{
            throw new IllegalStateException("failed");
        }
    }

    @Description("<过程调用语句> → call<标识符>")
    public void role$20(){
        if(SystemWord.CALL.equals(wordsQueue.poll().getLiteral())){
            role$6();
        }else{
            throw new IllegalStateException("failed");
        }
    }

    @Description("<当型循环语句> → while<条件>do<语句>")
    public void role$21(){
        if(SystemWord.WHILE.equals(wordsQueue.poll().getLiteral())){
            role$12();
            if(SystemWord.DO.equals(wordsQueue.poll().getLiteral())){
                role$9();
            }else{
                throw new IllegalStateException("failed");
            }
        }else{
            throw new IllegalStateException("failed");
        }
    }

    @Description("<读语句> → read(<标识符>{,<标识符>})")
    public void role$22(){
        if(SystemWord.READ.equals(wordsQueue.poll().getLiteral())){
            if(SystemWord.LEFT_PARENTHESIS.equals(wordsQueue.poll().getLiteral())){
                role$6();
                String nextToken=wordsQueue.peek().getLiteral();
                while(SystemWord.SEPARATOR.equals(nextToken)){
                    wordsQueue.poll();
                    role$6();
                    nextToken=wordsQueue.peek().getLiteral();
                }
                if(!SystemWord.RIGHT_PARENTHESIS.equals(wordsQueue.poll().getLiteral())){
                    throw new IllegalStateException("failed");
                }
            }else{
                throw new IllegalStateException("failed");
            }
        }else{
            throw new IllegalStateException("failed");
        }
    }

    @Description("<写语句> → write(<表达式>{,<表达式>})")
    public void role$23(){
        if(SystemWord.WRITE.equals(wordsQueue.poll().getLiteral())){
            if(SystemWord.LEFT_PARENTHESIS.equals(wordsQueue.poll().getLiteral())){
                role$13();
                String nextToken=wordsQueue.peek().getLiteral();
                while(SystemWord.SEPARATOR.equals(nextToken)){
                    wordsQueue.poll();
                    role$13();
                    nextToken=wordsQueue.peek().getLiteral();
                }
                if(SystemWord.RIGHT_PARENTHESIS.equals(nextToken)){
                    wordsQueue.poll();
                }else{
                    throw new IllegalStateException("failed");
                }
            }else{
                throw new IllegalStateException("failed");
            }


        }
    }

    @Description("<字母> → a|b|c…x|y|z")
    public void role$24(){
        String word=wordsQueue.poll().getLiteral();
        for(int i=0;i<word.length();i++){
            if(word.charAt(i)<'a'||word.charAt(i)>'z'){
                throw new IllegalStateException("failed");
            }
        }
    }

    @Description("<数字> → 0|1|2…7|8|9")
    public void role$25(){
        String word=wordsQueue.poll().getLiteral();
        try {
            Integer.parseInt(word);
        }catch (NumberFormatException e){
            throw new IllegalStateException("failed");
        }
    }

}
