package es.tid.cloud.tdaf.accounting.publish;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.cxf.BusFactory;
import org.apache.cxf.jaxrs.client.JAXRSClientFactory;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.Mongo;
import com.mongodb.util.JSON;

import es.tid.cloud.tdaf.accounting.Constants;
import es.tid.cloud.tdaf.accounting.model.EventBase.Mode;
import es.tid.cloud.tdaf.accounting.model.EventEntry;
import es.tid.cloud.tdaf.accounting.rest.resources.AccountingResource;

@ContextConfiguration(
        locations = {"classpath:test-context.xml",
                     "classpath:META-INF/spring/publish-context.xml" })
public class PublishContextTest extends AbstractJUnit4SpringContextTests {

    private final static String REST_URL = "rest.baseURL";
    private final static int NUMBER_EVENTS = 20;

    @Autowired
    Mongo mongo;
    @Autowired
    DB accountingDb;

    //Extra Mocks
    DBCursor dbCursor;
    DBCollection eventsCollection;

    @Autowired
    ObjectMapper mapper;
    @Autowired
    JacksonJsonProvider jacksonJsonProvider;

    @BeforeClass
    public static void setupUrls() {
        System.setProperty(REST_URL, "http://localhost:8888/rest");
    }

    @AfterClass
    public static void tearDown() throws Exception {
        BusFactory.setDefaultBus(null);
        BusFactory.setThreadDefaultBus(null);
    }

    @Before
    public void setUp() throws Exception {
        dbCursor = Mockito.mock(DBCursor.class);
        eventsCollection = Mockito.mock(DBCollection.class);
        when(mongo.getDB(Constants.MONGO_DB)).thenReturn(accountingDb);
        when(accountingDb.getCollection(Constants.MONGO_COLLECTION)).thenReturn(eventsCollection);
    }

    @Test
    public void whenGetAllEventShouldReturnAllEvents() throws Exception {

        //Given
        List<DBObject> dbObjects = getDBObjects(new String[] {"InstantServer", "VDC", "Cosmonautiko"});
        when(eventsCollection.find()).thenReturn(dbCursor);
        when(dbCursor.toArray()).thenReturn(dbObjects);
        when(dbCursor.count()).thenReturn(NUMBER_EVENTS);

        //When
        AccountingResource accountingResource = JAXRSClientFactory.create(System.getProperty(REST_URL) ,
                AccountingResource.class, 
                Collections.singletonList(jacksonJsonProvider));

        Response response = accountingResource.getAllEvents(null, null);

        //Then
        assertEquals(Status.OK.getStatusCode(), response.getStatus());
        assertNotNull(response.getEntity());
        assertTrue(mapper.readTree((InputStream)response.getEntity()).size()==NUMBER_EVENTS);
        verify(eventsCollection).find();
        verify(dbCursor).toArray();
    }

    @Test
    public void whenGetAllEventAndThereAreNotEventShouldReturnNotFoundException() throws Exception {

        //Given
        when(eventsCollection.find()).thenReturn(dbCursor);
        when(dbCursor.count()).thenReturn(0);

        //When
        AccountingResource accountingResource = JAXRSClientFactory.create(System.getProperty(REST_URL) ,
                AccountingResource.class, 
                Collections.singletonList(jacksonJsonProvider));

        Response response = accountingResource.getAllEvents(null, null);

        //Then
        assertEquals(Status.NOT_FOUND.getStatusCode(), response.getStatus());
        verify(eventsCollection).find();
        verify(dbCursor, never()).toArray();
    }

    @Test
    public void whenGetEventsByServiceIdThenShouldReturnFilteredEvents() throws Exception {

        //Given
        List<DBObject> dbObjects = getDBObjects(new String[] {"InstantServer", "VDC", "Cosmonautiko","Pepe"});
        when(eventsCollection.find(Mockito.any(DBObject.class))).thenReturn(dbCursor);
        when(dbCursor.toArray()).thenReturn(dbObjects);
        when(dbCursor.count()).thenReturn(NUMBER_EVENTS);

        //When
        AccountingResource accountingResource = JAXRSClientFactory.create(System.getProperty(REST_URL) ,
                AccountingResource.class, 
                Collections.singletonList(jacksonJsonProvider));

        Response response = accountingResource.findEvents("InstantServer", null, null);

        //Then
        assertEquals(Status.OK.getStatusCode(), response.getStatus());
        assertNotNull(response.getEntity());
        assertTrue(mapper.readTree((InputStream)response.getEntity()).size()==NUMBER_EVENTS);
        verify(eventsCollection).find(Matchers.any(DBObject.class));
        verify(dbCursor).toArray();
    }

    private List<DBObject> getDBObjects(String[] servicesId){
        List<DBObject> dbObjects = new ArrayList<DBObject>();
        for (int i = 1; i <= NUMBER_EVENTS; i++) {
            EventEntry eventEntry = new EventEntry(
                    servicesId[i % servicesId.length],
                    servicesId[i % servicesId.length]+"EventId",
                    servicesId[i % servicesId.length]+"Concept",
                    servicesId[i % servicesId.length]+"Event",
                    Mode.OFFLINE,
                    new HashMap<String, Object>(),
                    new HashMap<String, Object>(),
                    new Date());
            DBObject dbObject;
            try {
                dbObject = (DBObject)JSON.parse(mapper.writeValueAsString(eventEntry));
                dbObjects.add(dbObject);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        }
        return dbObjects;
    }
}
