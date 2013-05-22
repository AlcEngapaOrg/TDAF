package es.tid.cloud.tdaf.accounting.persist;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.test.spring.CamelSpringTestSupport;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public abstract class PersistEventsRouteTestSupport extends CamelSpringTestSupport {

    protected final static String COLLECTION = "events";
    protected final static String DB = "accounting";

    /** The producer template. */
    @Produce(uri = "vm:persist-events")
    protected ProducerTemplate template;

    @Override
    protected AbstractApplicationContext createApplicationContext() {
        return new ClassPathXmlApplicationContext(
                new String[] {
                        "classpath:META-INF/spring/persist-context.xml",
                        appContext()
                });
    }

    /**
     * Indicates the application context file
     * @return
     */
    public abstract String appContext();

    protected final Map<String, Object> getBodyAsMap(){
        Map<String, Object> body = new HashMap<String, Object>();
        body.put("key1", "simpleText");
        body.put("key2", Arrays.asList(new String[] {"1","2"}));
        body.put("key3", SimpleDateFormat.getInstance().format(new Date()));
        return body;
    }
}