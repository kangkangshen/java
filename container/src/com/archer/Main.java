package archer;
/*
    test for archer
 */

import archer.support.ConfigurationBasedBeanContainer;
import archer.test.Student;

public class Main {

    public static void main(String[] args) throws NoSuchMethodException, ClassNotFoundException {
        BeanContainer container=new ConfigurationBasedBeanContainer("F:\\gitRepository\\container\\src\\com\\META-INF\\config.xml");
        Student student= (Student) container.getBean("wukang");
        student.print();
    }

}