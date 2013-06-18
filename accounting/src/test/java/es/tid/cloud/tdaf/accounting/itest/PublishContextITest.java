package es.tid.cloud.tdaf.accounting.itest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.InputStream;
import java.text.SimpleDateFormat;
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
        locations = {"classpath:test-integ-context.xml",
                     "classpath:META-INF/spring/publish-context.xml" })
public class PublishContextITest extends AbstractJUnit4SpringContextTests {

    private final static String REST_URL = "rest.baseURL";

    @Autowired
    Mongo mongo;

    DB accountingDb;
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
        accountingDb = mongo.getDB(Constants.MONGO_DB);
        accountingDb.getCollection(Constants.MONGO_COLLECTION).drop();
    }

    @Test
    public void whenGetAllEventShouldReturnAllEvents() throws Exception {

        //Given
        int size = 10;
        List<DBObject> dbObjects = getDBObjects(new String[] {"InstantServer", "VDC", "Cosmonautiko"}, size);
        accountingDb.getCollection(Constants.MONGO_COLLECTION).insert(dbObjects);

        //When
        AccountingResource accountingResource = JAXRSClientFactory.create(System.getProperty(REST_URL) ,
                AccountingResource.class, 
                Collections.singletonList(jacksonJsonProvider));

        Response response = accountingResource.getAllEvents(null, null);

        //Then
        assertEquals(Status.OK.getStatusCode(), response.getStatus());
        assertNotNull(response.getEntity());
        assertTrue(mapper.readTree((InputStream)response.getEntity()).size()==size);
    }

    @Test
    public void whenGetAllEventAndThereAreNotEventShouldReturnNotFoundException() throws Exception {

        //Given empty collection

        //When
        AccountingResource accountingResource = JAXRSClientFactory.create(System.getProperty(REST_URL) ,
                AccountingResource.class, 
                Collections.singletonList(jacksonJsonProvider));

        Response response = accountingResource.getAllEvents(null,null);

        //Then
        assertEquals(Status.NOT_FOUND.getStatusCode(), response.getStatus());
    }

    @Test
    public void whenGetEventsByServiceIdThenShouldReturnFilteredEvents() throws Exception {

        //Given
        int size = 10;
        String[] services = new String[] {"InstantServer", "VDC", "Cosmonautiko"};
        List<DBObject> dbObjects = getDBObjects(services, size);
        accountingDb.getCollection(Constants.MONGO_COLLECTION).insert(dbObjects);

        //When
        AccountingResource accountingResource = JAXRSClientFactory.create(System.getProperty(REST_URL) ,
                AccountingResource.class, 
                Collections.singletonList(jacksonJsonProvider));

        Response response = accountingResource.findEvents("InstantServer", null, null);

        //Then
        assertEquals(Status.OK.getStatusCode(), response.getStatus());
        assertNotNull(response.getEntity());
        assertTrue(mapper.readTree((InputStream)response.getEntity()).size()==size/services.length);
    }

    @Test
    public void whenGetEventsByEndDateQueryParamThenShouldReturnFilteredEvents() throws Exception {

        //Given
        String[] services = new String[] {"InstantServer", "VDC", "Cosmonautiko"};
        int size = 20;
        List<DBObject> dbObjects = getDBObjects(services, size);
        EventEntry eventEntryExtra = mapper.readValue(JSON.serialize(dbObjects.get(size-1)).getBytes(), EventEntry.class);
        eventEntryExtra.setServiceId("InstantServer");
        Date endDate = new Date(System.currentTimeMillis()+1000L);
        eventEntryExtra.setTime(endDate);
        dbObjects.add((DBObject)JSON.parse(mapper.writeValueAsString(eventEntryExtra)));
        accountingDb.getCollection(Constants.MONGO_COLLECTION).insert(dbObjects);

        //When
        AccountingResource accountingResource = JAXRSClientFactory.create(System.getProperty(REST_URL) ,
                AccountingResource.class, 
                Collections.singletonList(jacksonJsonProvider));

        Response response = accountingResource.findEvents("InstantServer", null, 
                new SimpleDateFormat(Constants.DATE_FORMAT).format(new Date(endDate.getTime()-1L)));

        //Then
        assertEquals(Status.OK.getStatusCode(), response.getStatus());
        assertNotNull(response.getEntity());
        assertTrue(mapper.readTree((InputStream)response.getEntity()).size()==size/services.length);
    }

    @Test
    public void whenGetEventsByStartDateQueryParamThenShouldReturnFilteredEvents() throws Exception {

        //Given
        String[] services = new String[] {"InstantServer", "VDC", "Cosmonautiko"};
        int size = 20;
        List<DBObject> dbObjects = getDBObjects(services, size);
        EventEntry eventEntryExtra = mapper.readValue(JSON.serialize(dbObjects.get(size-1)).getBytes(), EventEntry.class);
        eventEntryExtra.setServiceId("InstantServer");
        Date startDate = new Date(eventEntryExtra.getTime().getTime()-1000L);
        eventEntryExtra.setTime(startDate);
        dbObjects.add((DBObject)JSON.parse(mapper.writeValueAsString(eventEntryExtra)));
        accountingDb.getCollection(Constants.MONGO_COLLECTION).insert(dbObjects);

        //When
        AccountingResource accountingResource = JAXRSClientFactory.create(System.getProperty(REST_URL) ,
                AccountingResource.class, 
                Collections.singletonList(jacksonJsonProvider));

        Response response = accountingResource.findEvents("InstantServer", 
                new SimpleDateFormat(Constants.DATE_FORMAT).format(startDate.getTime()+1L), null);

        //Then
        assertEquals(Status.OK.getStatusCode(), response.getStatus());
        assertNotNull(response.getEntity());
        assertTrue(mapper.readTree((InputStream)response.getEntity()).size()==size/services.length);
    }

    @Test
    public void whenGetEventsByStartAndEndDateQueryParamThenShouldReturnFilteredEvents() throws Exception {

        //Given
        String[] services = new String[] {"InstantServer", "VDC", "Cosmonautiko"};
        int size = 20;
        List<DBObject> dbObjects = getDBObjects(services, size);
        EventEntry eventEntryExtra = mapper.readValue(JSON.serialize(dbObjects.get(size-1)).getBytes(), EventEntry.class);
        eventEntryExtra.setServiceId("InstantServer");
        Date startDate = new Date(eventEntryExtra.getTime().getTime()-10000L);
        eventEntryExtra.setTime(startDate);
        dbObjects.add((DBObject)JSON.parse(mapper.writeValueAsString(eventEntryExtra)));
        Date endDate = new Date(eventEntryExtra.getTime().getTime()+10000L);
        eventEntryExtra.setTime(endDate);
        dbObjects.add((DBObject)JSON.parse(mapper.writeValueAsString(eventEntryExtra)));
        accountingDb.getCollection(Constants.MONGO_COLLECTION).insert(dbObjects);

        //When
        AccountingResource accountingResource = JAXRSClientFactory.create(System.getProperty(REST_URL) ,
                AccountingResource.class, 
                Collections.singletonList(jacksonJsonProvider));

        Response response = accountingResource.findEvents("InstantServer", 
                new SimpleDateFormat(Constants.DATE_FORMAT).format(startDate),
                new SimpleDateFormat(Constants.DATE_FORMAT).format(endDate));

        //Then
        assertEquals(Status.OK.getStatusCode(), response.getStatus());
        assertNotNull(response.getEntity());
        assertTrue(mapper.readTree((InputStream)response.getEntity()).size()==size/services.length+2);
    }

    @Test
    public void whenGetEventsWithoutServiceIdByStartAndEndDateQueryParamThenShouldReturnFilteredEvents() throws Exception {

        //Given
        String[] services = new String[] {"InstantServer", "VDC", "Cosmonautiko"};
        int size = 20;
        List<DBObject> dbObjects = getDBObjects(services, size);
        EventEntry eventEntryExtra = mapper.readValue(JSON.serialize(dbObjects.get(size-1)).getBytes(), EventEntry.class);
        Date startDate = new Date(eventEntryExtra.getTime().getTime()-10000L);
        eventEntryExtra.setTime(startDate);
        dbObjects.add((DBObject)JSON.parse(mapper.writeValueAsString(eventEntryExtra)));
        Date endDate = new Date(eventEntryExtra.getTime().getTime()+10000L);
        eventEntryExtra.setTime(endDate);
        dbObjects.add((DBObject)JSON.parse(mapper.writeValueAsString(eventEntryExtra)));
        accountingDb.getCollection(Constants.MONGO_COLLECTION).insert(dbObjects);

        //When
        AccountingResource accountingResource = JAXRSClientFactory.create(System.getProperty(REST_URL) ,
                AccountingResource.class, 
                Collections.singletonList(jacksonJsonProvider));

        Response response = accountingResource.findEvents(null, 
                new SimpleDateFormat(Constants.DATE_FORMAT).format(startDate),
                new SimpleDateFormat(Constants.DATE_FORMAT).format(endDate));

        //Then
        assertEquals(Status.OK.getStatusCode(), response.getStatus());
        assertNotNull(response.getEntity());
        assertTrue(mapper.readTree((InputStream)response.getEntity()).size()==size+2);
    }

    private List<DBObject> getDBObjects(String[] servicesId, final int size){
        List<DBObject> dbObjects = new ArrayList<DBObject>();
        for (int i = 1; i <= size; i++) {
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
