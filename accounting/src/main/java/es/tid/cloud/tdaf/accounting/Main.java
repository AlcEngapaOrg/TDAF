package es.tid.cloud.tdaf.accounting;



public class Main {

    public final static String LOG_DIR = "log.dir";
    public final static String SERVICE_ID = "service.id";

    public static void main(String[] args) throws Exception {
            String logDir = System.getProperty(LOG_DIR);
            String serviceId = System.getProperty(SERVICE_ID);
            if(logDir == null || serviceId == null) {
                System.err.println(String.format("Please, correct system properties are required : \n" +
                        "\t - %s : log directory.\n" +
                        "\t - %s : the service id.\n", LOG_DIR, SERVICE_ID));
                System.exit(1);
            }
            org.apache.camel.spring.Main.main(args);
        }
}
