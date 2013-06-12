package es.tid.cloud.tdaf.accounting.persist;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.test.spring.CamelSpringTestSupport;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import es.tid.cloud.tdaf.accounting.model.EventEntry;

public abstract class PersistEventsRouteTestSupport extends CamelSpringTestSupport {

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

    protected final EventEntry getBodyAsEventEntry(){

        EventEntry eventEntry = new EventEntry();
        eventEntry.setServiceId("InstantServer");
        eventEntry.setId("anEvent");
        eventEntry.setConcept("Net");
        eventEntry.setEvent("Traffic");
        eventEntry.setTime(new Date());

        Map<String, Object> eventInfo = new HashMap<String, Object>();
        eventInfo.put("key1", "simpleText");
        eventInfo.put("key2", Arrays.asList(new String[] {"1","2"}));
        eventInfo.put("key3", SimpleDateFormat.getInstance().format(new Date()));
        eventEntry.setEventInfo(eventInfo);

        Map<String, Object> logDetails = new HashMap<String, Object>();
        logDetails.put("logCalendar", Calendar.getInstance());
        eventEntry.setLogDetails(logDetails);

        return eventEntry;
    }
}
