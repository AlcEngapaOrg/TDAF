package es.tid.cloud.tdaf.accounting.data;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.test.spring.CamelSpringTestSupport;
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
import com.mongodb.util.JSON;

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
        Object o = template.requestBody(JSON.parse("{name:'enrique',lastName:'garcia'}"));

        // Then
        assertTrue("Result is not of type WriteResult", o instanceof WriteResult);
        verify(eventsCollection).save(Matchers.any(DBObject.class));

    }

}
