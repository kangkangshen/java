package archer.container;
/*
    test for archer
 */

import archer.container.BeanContainer;
import archer.container.support.ConfigurationBasedBeanContainer;
import archer.container.test.Student;

public class Main {

    public static void main(String[] args) throws NoSuchMethodException, ClassNotFoundException {
        BeanContainer container=new ConfigurationBasedBeanContainer("F:\\gitRepository\\container\\src\\com\\META-INF\\config.xml");
        Student student= (Student) container.getBean("wukang");
        student.print();
        System.out.println("hello,world");
        System.out.println("hello,world");

    }

}