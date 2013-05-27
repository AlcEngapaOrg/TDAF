package es.tid.cloud.tdaf.accounting.rest.resources;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/accounting")
@Produces(MediaType.APPLICATION_JSON)
public interface AccountingResource {

    public final static String SERVICE_FIELD = "serviceId";
    public final static String TIME_FIELD = "time";

    @GET
    @Path("/events")
    Response getAllEvents(@QueryParam("startDate") String startDate, @QueryParam("endDate") String endDate);

    @GET
    @Path("/events/{id}")
    Response findEvents(
            @PathParam("id") String id,
            @QueryParam("startDate") String startDate, @QueryParam("endDate") String endDate);

}
