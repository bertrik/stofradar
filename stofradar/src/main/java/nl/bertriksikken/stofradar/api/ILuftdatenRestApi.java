package nl.bertriksikken.stofradar.api;

import retrofit2.Call;
import retrofit2.http.GET;

/**
 * Interface towards the luftdaten API to get 5 minute dust averages. 
 */
public interface ILuftdatenRestApi {

    @GET("/static/v2/data.json")
    Call<String> getAverageDustData();

}
