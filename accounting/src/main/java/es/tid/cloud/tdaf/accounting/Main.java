package es.tid.cloud.tdaf.accounting;


/**
 * Main class
 * 
 * @author AlcEngapaOrg
 *
 */

public class Main {

    public static void main(String[] args) throws Exception {
            String accountingHome = System.getenv(Constants.ENV_ACCOUNTING_HOME);
            if(accountingHome != null) {
                System.out.println("## Found environment property " + Constants.ENV_ACCOUNTING_HOME + " = " + accountingHome);
            } else {
                ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
                accountingHome = classLoader.getResource(".").getPath();
                System.out.println("## Setting system property " + Constants.ENV_ACCOUNTING_HOME + " = " + accountingHome);
                System.setProperty(Constants.ENV_ACCOUNTING_HOME, accountingHome);
            }
            /*
            String logDir = System.getProperty(Constants.SYS_LOG_DIR);
            if(logDir == null) {
                throw new AccountingException(Code.AC_0001, 
                        String.format("## Please, correct system properties are required : \n" +
                        "\t - %s : log directory.\n" , Constants.SYS_LOG_DIR));
            } */
            System.out.println("## Enter Ctrl+C to quit !!");
            org.apache.camel.spring.Main.main(args);
        }
}
