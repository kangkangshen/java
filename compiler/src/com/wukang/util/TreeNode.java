package com.wukang.util;/*
 *@author:wukang
 */

import com.wukang.Entry;

import java.util.LinkedList;
import java.util.List;

public class TreeNode {

    private List<TreeNode> children;
    private TreeNode parent;
    private Entry currentEntry;

    public TreeNode(TreeNode parent,Entry currentEntry){
        this.parent=parent;
        if(parent!=null){
            parent.addChild(this);
        }
        this.currentEntry=currentEntry;
        children=new LinkedList<>();
    }
    public void setParent(TreeNode parent){
        this.parent=parent;
        if(!parent.children.contains(this)){
            parent.children.add(this);
        }
    }
    public void addChild(TreeNode child){
        this.children.add(child);
    }

    @Override
    public String toString() {
        return currentEntry.getLiteral();
    }

    public List<TreeNode> getChildren() {
        return children;
    }

    public Entry getCurrentEntry() {
        return currentEntry;
    }

    public TreeNode getParent() {
        return parent;
    }
}
