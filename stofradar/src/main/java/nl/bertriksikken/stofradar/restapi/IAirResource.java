package nl.bertriksikken.stofradar.restapi;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;

import java.io.InputStream;

@Path("/")
public interface IAirResource {

    @GET
    @Path("/favicon.ico")
    @Produces("image/x-icon")
    InputStream getFavicon();

    @GET
    @Path("/air/{lat}/{lon}")
    @Produces(MediaType.APPLICATION_JSON)
    AirResult getAirLegacy(@HeaderParam("User-Agent") String userAgent, @PathParam("lat") double latitude,
                           @PathParam("lon") double longitude);

    @GET
    @Path("/air")
    @Produces(MediaType.APPLICATION_JSON)
    AirResult getAir(@HeaderParam("User-Agent") String userAgent, @QueryParam("lat") Double latitude,
                     @QueryParam("lon") Double longitude);

}
