package com.wukang;

import com.wukang.component.LexicalAnalyzer;
import com.wukang.component.ParserFactory;
import com.wukang.component.SyntacticParser;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import javax.swing.*;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;
import java.awt.*;

import java.lang.reflect.Method;
import java.util.*;

public class Main {
    static Scanner scanner=new Scanner(System.in);
        public static void main(String[] args) {
            JFrame jf = new JFrame("抽象语法树窗口");
            jf.setSize(800, 600);
            jf.setLocationRelativeTo(null);
            jf.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
            JPanel panel = new JPanel(new BorderLayout());
            JTree tree = new JTree(test());
            tree.setShowsRootHandles(true);
            tree.setEditable(true);
            tree.addTreeSelectionListener(new TreeSelectionListener() {
                @Override
                public void valueChanged(TreeSelectionEvent e) {
                    System.out.println("当前被选中的节点: " + e.getPath());
                }
            });
            JScrollPane scrollPane = new JScrollPane(tree);
            panel.add(scrollPane, BorderLayout.CENTER);
            jf.setContentPane(panel);
            jf.setVisible(true);

    }
    static TreeNode test(){
        LexicalAnalyzer lexicalAnalyzer=new LexicalAnalyzer("C:\\Users\\wukang\\Documents\\Tencent Files\\2900250200\\FileRecv\\test.txt");
        lexicalAnalyzer.analyze();
        ParserFactory factory=new ParserFactory();
        SyntacticParser parser=factory.getParser(lexicalAnalyzer.getWordQueue());
        parser.parse();
        TreeNode treeNode=factory.getTree();
        return treeNode;
    }
}

