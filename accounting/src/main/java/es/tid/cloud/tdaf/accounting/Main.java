package es.tid.cloud.tdaf.accounting;

import java.util.concurrent.Semaphore;

import org.springframework.context.support.ClassPathXmlApplicationContext;

public class Main {
    public static void main(String[] args) throws Exception {
        if (args.length != 1) {
            System.out.println("Please, specified a directory to scan.");
        } else {
            System.setProperty("log.dir", args[0]);
            long init = System.currentTimeMillis();
            String[] configLocations = new String[] {
                    "META-INF/spring/app-context.xml",
                    "META-INF/spring/filtering-context.xml",
                    "META-INF/spring/persist-context.xml"
            };
            ClassPathXmlApplicationContext ctxt = new ClassPathXmlApplicationContext(configLocations);
            Semaphore semaphore = ctxt.getBean(Semaphore.class);
            semaphore.acquire();
            System.out.println("Finish. Elapsed time: " + (System.currentTimeMillis() - init) + "ms");
            ctxt.close();
        }
    }
}
