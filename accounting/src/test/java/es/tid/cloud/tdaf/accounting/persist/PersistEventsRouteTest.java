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

import es.tid.cloud.tdaf.accounting.Constants;

public class PersistEventsRouteTest extends PersistEventsRouteTestSupport {

    // Mock beans
    Mongo mongo;
    DB accountingDb;
    DBCollection eventsCollection;

    @Override
    public void doPreSetup() throws Exception {
        super.doPreSetup();

        mongo = getMandatoryBean(Mongo.class, Constants.MONGO_DB_CONNECTION);
        accountingDb = getMandatoryBean(DB.class, Constants.MONGO_DB);
        eventsCollection = getMandatoryBean(DBCollection.class, Constants.MONGO_COLLECTION);

        when(mongo.getDB(Constants.MONGO_DB)).thenReturn(accountingDb);
        when(accountingDb.getCollection(Constants.MONGO_COLLECTION)).thenReturn(eventsCollection);
    }

    @Test
    public void persistEvent() {

        // Given
        WriteResult wr = Mockito.mock(WriteResult.class);
        when(eventsCollection.save(Matchers.any(DBObject.class))).thenReturn(wr);

        // When
        Object o = template.requestBody(getBodyAsEventEntry());

        // Then
        assertTrue("Result is not of type WriteResult", o instanceof WriteResult);
        verify(eventsCollection).save(Matchers.any(DBObject.class));

    }

    @Override
    public String appContext() {
        return "classpath:test-context.xml";
    }

}
