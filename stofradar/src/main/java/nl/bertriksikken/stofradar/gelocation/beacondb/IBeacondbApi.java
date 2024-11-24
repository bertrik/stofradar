package nl.bertriksikken.stofradar.gelocation.beacondb;

import nl.bertriksikken.stofradar.gelocation.GeoLocationRequest;
import nl.bertriksikken.stofradar.gelocation.GeoLocationResponse;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface IBeacondbApi {

    @POST("/v1/geolocate")
    Call<GeoLocationResponse> geoLocate(@Header("User-agent") String userAgent, @Body GeoLocationRequest request);

}
