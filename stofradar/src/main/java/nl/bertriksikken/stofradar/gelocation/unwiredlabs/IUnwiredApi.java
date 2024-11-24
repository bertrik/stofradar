package nl.bertriksikken.stofradar.gelocation.unwiredlabs;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;

/**
 * See <a href="https://unwiredlabs.com/docs">Unwired labs docs</a>
 */
public interface IUnwiredApi {

    @POST("/v2/process")
    Call<UnwiredGeoLocationResponse> geoLocate(@Header("User-Agent") String userAgent, @Body UnwiredGeoLocationRequest request);
}
