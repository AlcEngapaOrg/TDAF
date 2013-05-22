package es.tid.cloud.tdaf.accounting;

import org.apache.camel.CamelContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class Main {
    public static void main(String[] args) throws Exception {
        if (args.length != 1) {
            System.out.println("Please, specified a directory to scan.");
        } else {
            System.setProperty("log.dir", args[0]);
            String[] configLocations = new String[] {
                    "META-INF/spring/app-context.xml",
                    "META-INF/spring/filtering-context.xml",
                    "META-INF/spring/persist-context.xml"
            };
            final ClassPathXmlApplicationContext ctxt = new ClassPathXmlApplicationContext(configLocations);
            Thread interceptor = new Thread() {
                @Override
                public void run() {
                    ctxt.stop();
                }
            };
            Runtime.getRuntime().addShutdownHook(interceptor);
            for (CamelContext camelContext: ctxt.getBeansOfType(CamelContext.class).values()) {
                waitUntilCompleted(camelContext);
            }
        }
    }

    private static void waitUntilCompleted(CamelContext camelContext) {
        boolean completed = camelContext.getStatus().isStopped();
        while (!completed) {
            try {
                Thread.sleep(1000);
                completed = camelContext.getStatus().isStopped();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
}
