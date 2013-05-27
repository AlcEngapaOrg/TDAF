package es.tid.cloud.tdaf.accounting.rest;

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
import java.util.Map;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.cxf.BusFactory;
import org.apache.cxf.jaxrs.client.JAXRSClientFactory;
import org.codehaus.jackson.jaxrs.JacksonJsonProvider;
import org.codehaus.jackson.map.ObjectMapper;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
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
import es.tid.cloud.tdaf.accounting.rest.resources.AccountingResourceImpl;

@ContextConfiguration(
        locations = {"classpath:test-integ-context.xml",
                     "classpath:META-INF/spring/publish-context.xml" })
public class PublishContextITest extends AbstractJUnit4SpringContextTests {

    private final static String REST_URL = "rest.baseURL";
    private final static int NUMBER_EVENTS = 20;

    @Autowired
    Mongo mongo;

    DB accountingDb;
    DBCursor dbCursor;
    DBCollection eventsCollection;

    ObjectMapper mapper = new ObjectMapper();

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
        accountingDb = mongo.getDB(TestUtil.DB);
        accountingDb.getCollection(TestUtil.COLLECTION).drop();
    }

    @Test
    public void whenGetAllEventShouldReturnAllEvents() throws Exception {

        //Given
        List<DBObject> dbObjects = getDBObjects(new String[] {"InstantServer", "VDC", "Cosmonautiko"});
        accountingDb.getCollection(TestUtil.COLLECTION).insert(dbObjects);

        //When
        AccountingResource accountingResource = JAXRSClientFactory.create(System.getProperty(REST_URL) ,
                AccountingResource.class, 
                Collections.singletonList(new JacksonJsonProvider()));

        Response response = accountingResource.getAllEvents(null, null);

        //Then
        assertEquals(Status.OK.getStatusCode(), response.getStatus());
        assertNotNull(response.getEntity());
        assertTrue(mapper.readTree((InputStream)response.getEntity()).size()==NUMBER_EVENTS);
    }

    @Test
    public void whenGetAllEventAndThereAreNotEventShouldReturnNotFoundException() throws Exception {

        //Given empty collection

        //When
        AccountingResource accountingResource = JAXRSClientFactory.create(System.getProperty(REST_URL) ,
                AccountingResource.class, 
                Collections.singletonList(new JacksonJsonProvider()));

        Response response = accountingResource.getAllEvents(null,null);

        //Then
        assertEquals(Status.NOT_FOUND.getStatusCode(), response.getStatus());
    }

    @Test
    public void whenGetEventsByServiceIdThenShouldReturnFilteredEvents() throws Exception {

        //Given
        String[] services = new String[] {"InstantServer", "VDC", "Cosmonautiko"};
        List<DBObject> dbObjects = getDBObjects(services);
        accountingDb.getCollection(TestUtil.COLLECTION).insert(dbObjects);

        //When
        AccountingResource accountingResource = JAXRSClientFactory.create(System.getProperty(REST_URL) ,
                AccountingResource.class, 
                Collections.singletonList(new JacksonJsonProvider()));

        Response response = accountingResource.findEvents("InstantServer", null, null);

        //Then
        assertEquals(Status.OK.getStatusCode(), response.getStatus());
        assertNotNull(response.getEntity());
        assertTrue(mapper.readTree((InputStream)response.getEntity()).size()==NUMBER_EVENTS/services.length);
    }

    @Test
    public void whenGetEventsByEndDateQueryParamThenShouldReturnFilteredEvents() throws Exception {

        //Given
        String[] services = new String[] {"InstantServer"};
        List<DBObject> dbObjects = getDBObjects(services);
        Date endDate = new Date(System.currentTimeMillis()+70000L);
        Map<String, Object> m = new HashMap<String, Object>();
        m.put(AccountingResource.SERVICE_FIELD, "InstantServer");
        m.put(AccountingResource.TIME_FIELD, endDate);
        dbObjects.add(new BasicDBObject(m));
        accountingDb.getCollection(TestUtil.COLLECTION).insert(dbObjects);

        //When
        AccountingResource accountingResource = JAXRSClientFactory.create(System.getProperty(REST_URL) ,
                AccountingResource.class, 
                Collections.singletonList(new JacksonJsonProvider()));

        Response response = accountingResource.findEvents("InstantServer", null, 
                new SimpleDateFormat(AccountingResourceImpl.DATE_FORMAT).format(endDate));

        //Then
        assertEquals(Status.OK.getStatusCode(), response.getStatus());
        assertNotNull(response.getEntity());
        assertTrue(mapper.readTree((InputStream)response.getEntity()).size()==NUMBER_EVENTS);
    }

    @Test
    public void whenGetEventsByStartDateQueryParamThenShouldReturnFilteredEvents() throws Exception {

        //Given
        String[] services = new String[] {"InstantServer", "VDC", "Cosmonautiko"};
        List<DBObject> dbObjects = getDBObjects(services);
        Date startDate = new Date(System.currentTimeMillis()+70000L);
        Map<String, Object> m = new HashMap<String, Object>();
        m.put(AccountingResource.SERVICE_FIELD, "InstantServer");
        m.put(AccountingResource.TIME_FIELD, startDate);
        dbObjects.add(new BasicDBObject(m));
        accountingDb.getCollection(TestUtil.COLLECTION).insert(dbObjects);

        //When
        AccountingResource accountingResource = JAXRSClientFactory.create(System.getProperty(REST_URL) ,
                AccountingResource.class, 
                Collections.singletonList(new JacksonJsonProvider()));

        Response response = accountingResource.findEvents("InstantServer", 
                new SimpleDateFormat(AccountingResourceImpl.DATE_FORMAT).format(startDate), null);

        //Then
        assertEquals(Status.OK.getStatusCode(), response.getStatus());
        assertNotNull(response.getEntity());
        assertTrue(mapper.readTree((InputStream)response.getEntity()).size()==1);
    }

    @Test
    public void whenGetEventsByStartAndEndDateQueryParamThenShouldReturnFilteredEvents() throws Exception {

        //Given
        String[] services = new String[] {"InstantServer", "VDC", "Cosmonautiko"};
        List<DBObject> dbObjects = getDBObjects(services);
        Date startAndEndDate = new Date(System.currentTimeMillis()+70000L);
        Map<String, Object> m = new HashMap<String, Object>();
        m.put(AccountingResource.SERVICE_FIELD, "InstantServer");
        m.put(AccountingResource.TIME_FIELD, startAndEndDate);
        dbObjects.add(new BasicDBObject(m));
        dbObjects.add(new BasicDBObject(m));
        accountingDb.getCollection(TestUtil.COLLECTION).insert(dbObjects);

        //When
        AccountingResource accountingResource = JAXRSClientFactory.create(System.getProperty(REST_URL) ,
                AccountingResource.class, 
                Collections.singletonList(new JacksonJsonProvider()));

        Response response = accountingResource.findEvents("InstantServer", 
                new SimpleDateFormat(AccountingResourceImpl.DATE_FORMAT).format(startAndEndDate),
                new SimpleDateFormat(AccountingResourceImpl.DATE_FORMAT).format(startAndEndDate));

        //Then
        assertEquals(Status.OK.getStatusCode(), response.getStatus());
        assertNotNull(response.getEntity());
        assertTrue(mapper.readTree((InputStream)response.getEntity()).size()==0);
    }

    private List<DBObject> getDBObjects(String[] servicesId){
        List<DBObject> dbObjects = new ArrayList<DBObject>();
        for (int i = 1; i <= NUMBER_EVENTS; i++) {
            Map<String, Object> m = new HashMap<String, Object>();
            m.put(AccountingResource.SERVICE_FIELD, servicesId[i % servicesId.length]);
            m.put(AccountingResource.TIME_FIELD, new Date());
            BasicDBObject dbObject = new BasicDBObject(m);
            dbObjects.add(dbObject);
        }
        return dbObjects;
    }
}
