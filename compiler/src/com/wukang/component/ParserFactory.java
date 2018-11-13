package com.wukang.component;/*
 *@author:wukang
 */

import com.wukang.Entry;
import com.wukang.NonTerminalEntry;
import com.wukang.util.Description;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreeNode;
import java.lang.reflect.Method;
import java.util.Queue;
import java.util.Stack;

public class ParserFactory implements MethodInterceptor {

    private Stack<TreeNode> nodeStack;

    private TreeNode tree;

    public ParserFactory(){
        nodeStack=new Stack<>();
    }

    public  SyntacticParser getParser(Queue<Entry> wordsQueue){
        Enhancer enhancer=new Enhancer();
        enhancer.setSuperclass(SyntacticParser.class);
        enhancer.setCallback(this);
        SyntacticParser parser= (SyntacticParser) enhancer.create(new Class[]{Queue.class},new Queue[]{wordsQueue});
        return parser;
    }

    @Override
    public Object intercept(Object o, Method method, Object[] objects, MethodProxy methodProxy) throws Throwable {
        if(method.getName().startsWith("role")){
            Description description=method.getAnnotation(Description.class);
            int index=description.value().indexOf('>');
            String left=description.value().substring(0,index+1);
            TreeNode parent,currentNode;
            if(nodeStack.isEmpty()){
                parent=null;
                currentNode= new DefaultMutableTreeNode(left);
                tree=currentNode;
            }else{
                parent=nodeStack.peek();
                currentNode=new DefaultMutableTreeNode(left);
                ((DefaultMutableTreeNode)parent).add((MutableTreeNode) currentNode);
            }
            nodeStack.push(currentNode);
            Object result=methodProxy.invokeSuper(o,objects);
            nodeStack.pop();
            return result;
        }else{
            return methodProxy.invokeSuper(o,objects);
        }
    }

    public String gernerateIndentation(int depth){
        String indentation="";
        for(int i=0;i<depth;i++){
            indentation+="\t";
        }
        return indentation;
    }

    public TreeNode getTree(){
        return tree;
    }

}
