package es.tid.cloud.tdaf.accounting.rest.resources;

import javax.ws.rs.GET;
import javax.ws.rs.MatrixParam;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/accounting")
@Produces(MediaType.APPLICATION_JSON)
public interface AccountingResource {

    @GET
    @Path("/events")
    Response getAllEvents(@QueryParam("") EventQuery eventQuery);

    @GET
    @Path("/events/{id}")
    Response findEvents(
            @PathParam("id") String id,
            @QueryParam("") EventQuery eventQueryParam,
            @MatrixParam("") EventQuery eventMatrixParam);

    final class EventQuery {
        String startDate;
        String endDate;
        public String getStartDate() {
            return startDate;
        }
        public void setStartDate(String startDate) {
            this.startDate = startDate;
        }
        public String getEndDate() {
            return endDate;
        }
        public void setEndDate(String endDate) {
            this.endDate = endDate;
        }
    }
}
