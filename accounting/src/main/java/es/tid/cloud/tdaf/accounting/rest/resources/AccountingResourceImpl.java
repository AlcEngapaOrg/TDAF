package es.tid.cloud.tdaf.accounting.rest.resources;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.NotFoundException;
import javax.ws.rs.core.Response;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;

import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.Mongo;
import com.mongodb.QueryBuilder;

public class AccountingResourceImpl implements AccountingResource {

    private final static String DB = "accounting";
    private final static String COLLECTION = "events";

    private final static String SERVICE_FIELD = "serviceId";
    private final static String TIME_FIELD = "time";
    
    private final ObjectMapper mapper = new ObjectMapper();

    private Mongo mongo;

    public AccountingResourceImpl(Mongo mongo) {
        this.mongo = mongo;
    }

    public Response getAllEvents(EventQuery eventQuery) {
        Object returnedObject = getJSON(null, eventQuery);
        return Response.ok(returnedObject).build();
    }

    @Override
    public Response findEvents(String id,
            EventQuery eventQueryParam,
            EventQuery eventMatrixParam) {
        Object returnedObject = null;
        if(eventMatrixParam != null) {
            returnedObject = getJSON(id, eventMatrixParam);
        } else {
            returnedObject = getJSON(id, eventQueryParam);
        }
        return Response.ok(returnedObject).build();
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    private Object getJSON(String serviceId, EventQuery eventQuery) {
        QueryBuilder queryBuilder = QueryBuilder.start();
        if(eventQuery != null) {
            if(eventQuery.getStartDate()!=null){
                queryBuilder.and(TIME_FIELD).greaterThanEquals(eventQuery.getStartDate());
            }
            if(eventQuery.getEndDate()!=null){
                queryBuilder.and(TIME_FIELD).lessThanEquals(eventQuery.getEndDate());
            }
        }
        if(serviceId != null) {
            queryBuilder.and(SERVICE_FIELD).in(serviceId);
        }
        DBObject query = queryBuilder.get();
        DBCursor dbCursor = mongo.getDB(DB).getCollection(COLLECTION).find(query);
        if(dbCursor.count()<1){
            throw new NotFoundException();
        }
        List<DBObject> eventList = dbCursor.toArray();
        Map mapEvents = new HashMap(eventList.size());
        for (DBObject dbObject : eventList) {
            mapEvents.putAll(dbObject.toMap());
        }
        return mapper.convertValue(mapEvents, JsonNode.class);
    }

}
