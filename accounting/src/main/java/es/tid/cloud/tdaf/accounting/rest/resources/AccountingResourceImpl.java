package es.tid.cloud.tdaf.accounting.rest.resources;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.Mongo;
import com.mongodb.QueryBuilder;

import es.tid.cloud.tdaf.accounting.Constants;

public class AccountingResourceImpl implements AccountingResource {

    private ObjectMapper mapper;
    private Mongo mongo;

    public AccountingResourceImpl(Mongo mongo, ObjectMapper mapper) {
        this.mongo = mongo;
        this.mapper = mapper;
    }

    public Response getAllEvents(String startDate, String endDate) throws WebApplicationException{
        Object returnedObject = getJSON(null, startDate, endDate);
        if(returnedObject == null){
            return Response.status(Status.NOT_FOUND).build();
        }
        return Response.ok(returnedObject).build();
    }

    @Override
    public Response findEvents(String serviceId, String startDate, String endDate) throws WebApplicationException{
        Object returnedObject = getJSON(serviceId, startDate, endDate);
        if(returnedObject == null){
            return Response.status(Status.NOT_FOUND).build();
        }
        return Response.ok(returnedObject).build();
    }

    @SuppressWarnings("rawtypes")
    private Object getJSON(String serviceId, String startDate, String endDate) throws WebApplicationException {
        List<DBObject> andObjects = new ArrayList<DBObject>();
        try {
            if(startDate != null) {
                andObjects.add(QueryBuilder.start(Constants.TIME_FIELD)
                        .greaterThanEquals(new SimpleDateFormat(Constants.DATE_FORMAT).parse(startDate)).get());
            }
            if(endDate != null){
                andObjects.add(QueryBuilder.start(Constants.TIME_FIELD)
                        .lessThanEquals(new SimpleDateFormat(Constants.DATE_FORMAT).parse(endDate)).get());
            }
        } catch (ParseException e) {
            throw new WebApplicationException(e, Status.BAD_REQUEST);
        }
        if(serviceId != null) {
            andObjects.add(new BasicDBObject(Constants.SERVICE_FIELD ,serviceId));
        }
        DBCursor dbCursor = null;
        if(andObjects.size()>0) {
            DBObject query = QueryBuilder.start().and(andObjects.toArray(new BasicDBObject[andObjects.size()])).get();
            dbCursor = mongo.getDB(Constants.MONGO_DB)
                            .getCollection(Constants.MONGO_COLLECTION)
                            .find(query);
        }
        else {
            dbCursor = mongo.getDB(Constants.MONGO_DB)
                    .getCollection(Constants.MONGO_COLLECTION)
                    .find();
        }
        if(dbCursor.count()<1){
            return null;
        }
        List<DBObject> eventList = dbCursor.toArray();
        List<Map> events = new ArrayList<Map>(eventList.size());
        for (DBObject dbObject : eventList) {
            events.add(dbObject.toMap());
        }
        return mapper.convertValue(events, JsonNode.class);
    }

}
