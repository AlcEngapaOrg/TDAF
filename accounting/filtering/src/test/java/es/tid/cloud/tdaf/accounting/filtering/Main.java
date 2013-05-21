package es.tid.cloud.tdaf.accounting.filtering;

import java.util.concurrent.Semaphore;

import org.springframework.context.support.ClassPathXmlApplicationContext;

public class Main {
    public static void main(String[] args) throws Exception {
        if (args.length != 1) {
            System.out.println("Please, specified a directory to scan.");
        } else {
            System.setProperty("log.dir", args[0]);
            ClassPathXmlApplicationContext ctxt = new ClassPathXmlApplicationContext("META-INF/spring/application-context.xml");
            Semaphore semaphore = ctxt.getBean(Semaphore.class);
            semaphore.acquire();
            ctxt.close();
        }
    }
}
