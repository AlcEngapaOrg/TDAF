package es.tid.cloud.tdaf.accounting.filtering;

import java.util.concurrent.Semaphore;

import org.springframework.context.support.ClassPathXmlApplicationContext;

public class Main {
    public static void main(String[] args) throws Exception {
        ClassPathXmlApplicationContext ctxt = new ClassPathXmlApplicationContext("META-INF/spring/application-context.xml");
        Semaphore semaphore = ctxt.getBean(Semaphore.class);
        semaphore.acquire();
        ctxt.close();
    }
}
