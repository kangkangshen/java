package archer;

import archer.config.XmlReader;
import archer.definition.BaseBeanDefinitionHolder;
import archer.definition.BeanDefinition;
import archer.support.ConfigurationBasedBeanContainer;
import archer.support.DefaultParamNameDiscover;
import archer.support.ParamNameMethodDiscover;
import archer.test.Student;
import archer.test.SubStudent;
import archer.util.ArrayUtils;
import archer.util.ObjectUtils;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.LocalVariableNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.ParameterNode;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Main {

    public static void main(String[] args) throws NoSuchMethodException, ClassNotFoundException {
        BeanContainer container=new ConfigurationBasedBeanContainer("F:\\container\\src\\com\\archer\\config.xml");
        Student student= (Student) container.getBean("wukang");
        student.print();
        /*
        Object s="wukang";
        BaseBeanDefinitionHolder holder=new BaseBeanDefinitionHolder(Student.class);
        ObjectUtils.setter(holder,"id","wukang");
        System.out.println(holder.getId());
        */



    }

}