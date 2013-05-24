package es.tid.cloud.tdaf.accounting.rest;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.cxf.BusFactory;
import org.apache.cxf.jaxrs.client.JAXRSClientFactory;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.jaxrs.JacksonJsonProvider;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.Mongo;

import es.tid.cloud.tdaf.accounting.persist.TestUtil;
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
    @Autowired
    DBCollection eventsCollection;

    //Mocks
    DBCursor dbCursor;
    DBCollection testDbCollection;

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
        testDbCollection = Mockito.mock(DBCollection.class);
        when(mongo.getDB(TestUtil.DB)).thenReturn(accountingDb);
        when(accountingDb.getCollection(TestUtil.COLLECTION)).thenReturn(eventsCollection);
    }

    @Test
    public void whenGetAllEventShouldRetunrAllEvents() throws Exception {

        //Given
        List<DBObject> dbObjects = getDBObjects();
        when(eventsCollection.find(Mockito.any(DBObject.class))).thenReturn(dbCursor);
        when(dbCursor.toArray()).thenReturn(dbObjects);
        when(dbCursor.count()).thenReturn(NUMBER_EVENTS);

        //When
        AccountingResource accountingResource = JAXRSClientFactory.create(System.getProperty(REST_URL) ,
                AccountingResource.class, 
                Collections.singletonList(new JacksonJsonProvider()));

        Response response = accountingResource.getAllEvents(null);

        //Then
        assertEquals(Status.OK.getStatusCode(), response.getStatus());
        assertNotNull(response.getEntity());
        assertTrue(response.getEntity() instanceof JsonNode);
        assertTrue(((JsonNode)response.getEntity()).size()==NUMBER_EVENTS);
        verify(eventsCollection).find(Matchers.any(DBObject.class));
        verify(dbCursor).toArray();
    }

    private List<DBObject> getDBObjects(){
        List<DBObject> dbObjects = new ArrayList<DBObject>();
        String[] servicesId = {"InstantServer", "VDC", "Cosmonautiko"};
        for (int i = 1; i <= NUMBER_EVENTS; i++) {
            Map<String, Object> m = new HashMap<String, Object>();
            m.put("serviceId", servicesId[i % servicesId.length]);
            m.put("time", SimpleDateFormat.getInstance().format(new Date()));
            BasicDBObject dbObject = new BasicDBObject(m);
            dbObjects.add(dbObject);
        }
        return dbObjects;
    }
}
