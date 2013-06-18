package es.tid.cloud.tdaf.accounting;

/**
 * Accounting constants.
 * 
 * @author AlcEngapaOrg
 *
 */
public final class Constants {

    //Env properties
    public final static String ENV_ACCOUNTING_HOME = "ACCOUNTING_HOME";

    //System properties
    public final static String SYS_LOG_DIR = "log.dir";

    //Common Props
    public final static String SERVICE_FIELD = "serviceId";
    public final static String TIME_FIELD = "time";
    public final static String DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSSZ";

    //Mongo connection properties
    public final static String MONGO_DB_CONNECTION = "myDb";
    public final static String MONGO_DB = "accounting";
    public final static String MONGO_COLLECTION = "events";

    private Constants(){} //No instances

}
