package es.tid.cloud.tdaf.accounting.filtering;

import org.springframework.context.support.ClassPathXmlApplicationContext;

public class Main {
    public static void main(String[] args) throws Exception {
        ClassPathXmlApplicationContext ctxt = new ClassPathXmlApplicationContext("META-INF/spring/application-context.xml");
        Thread.currentThread().sleep(600000);
    }
}
