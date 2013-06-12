package es.tid.cloud.tdaf.accounting.rest.resources;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/accounting")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public interface AccountingResource {

    @GET
    @Path("/events")
    Response getAllEvents(@QueryParam("startDate") String startDate, @QueryParam("endDate") String endDate);

    @GET
    @Path("/events/{serviceId}")
    Response findEvents(
            @PathParam("serviceId") String serviceId,
            @QueryParam("startDate") String startDate, @QueryParam("endDate") String endDate);

}
