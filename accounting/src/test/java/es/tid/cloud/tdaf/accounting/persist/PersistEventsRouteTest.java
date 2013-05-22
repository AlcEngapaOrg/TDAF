package es.tid.cloud.tdaf.accounting.persist;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mockito;

import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.Mongo;
import com.mongodb.WriteResult;

public class PersistEventsRouteTest extends PersistEventsRouteTestSupport {

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

    @Override
    public String appContext() {
        return "classpath:test-context.xml";
    }

}