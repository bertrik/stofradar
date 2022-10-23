package nl.bertriksikken.stofradar.restapi;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Path("/air")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public interface IAirRestApi {

    @GET
    @Path("/{lat}/{lon}")
    AirResult getAir(@HeaderParam("User-Agent") String userAgent, @PathParam("lat") double latitude,
            @PathParam("lon") double longitude);

}
