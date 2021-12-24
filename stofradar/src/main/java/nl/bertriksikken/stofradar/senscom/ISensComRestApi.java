package nl.bertriksikken.stofradar.senscom;

import retrofit2.Call;
import retrofit2.http.GET;

/**
 * Interface towards the sensor.community API to get 5 minute dust averages. 
 */
public interface ISensComRestApi {

    @GET("/static/v2/data.json")
    Call<String> getAverageDustData();

}
