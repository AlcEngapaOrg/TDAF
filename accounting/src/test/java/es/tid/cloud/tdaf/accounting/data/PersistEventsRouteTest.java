package es.tid.cloud.tdaf.accounting.data;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.test.spring.CamelSpringTestSupport;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mockito;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.Mongo;
import com.mongodb.WriteResult;

public class PersistEventsRouteTest extends CamelSpringTestSupport {

    private final static String COLLECTION = "events";
    private final static String DB = "accounting";

    /** The producer template. */
    @Produce(uri = "vm:persist-events")
    private ProducerTemplate template;

    // Mock beans
    Mongo mongo;
    DB accountingDb;
    DBCollection eventsCollection;

    @Override
    public void doPreSetup() throws Exception {
        super.doPreSetup();

        mongo = getMandatoryBean(Mongo.class, "myDb");
        accountingDb = getMandatoryBean(DB.class, DB);
        eventsCollection = getMandatoryBean(DBCollection.class, COLLECTION);

        when(mongo.getDB(DB)).thenReturn(accountingDb);
        when(accountingDb.getCollection(COLLECTION)).thenReturn(eventsCollection);
    }
    @Override
    protected AbstractApplicationContext createApplicationContext() {
        return new ClassPathXmlApplicationContext(
                new String[] { 
                        "classpath:test-app-context.xml",
                        "classpath:META-INF/spring/persist-context.xml"
                });
    }

    @Test
    public void persistEvent() {

        // Given
        WriteResult wr = Mockito.mock(WriteResult.class);
        when(eventsCollection.save(Matchers.any(DBObject.class))).thenReturn(wr);

        // When
        Object o = template.requestBody(getBodyAsMap());

        // Then
        assertTrue("Result is not of type WriteResult", o instanceof WriteResult);
        verify(eventsCollection).save(Matchers.any(DBObject.class));

    }

    @Ignore
    @Test
    public void integrationPersistEvent() throws Exception {

        // Given
        mongo = new Mongo("localhost");

        // When
        Object o = template.requestBody(getBodyAsMap());

        // Then
        assertTrue("Result is not of type WriteResult", o instanceof WriteResult);

    }

    private final Map<String, Object> getBodyAsMap(){
        Map<String, Object> body = new HashMap<String, Object>();
        body.put("key1", "simpleText");
        body.put("key2", Arrays.asList(new String[] {"1","2"}));
        body.put("key3", SimpleDateFormat.getInstance().format(new Date()));
        return body;
    }
}
