package nl.bertriksikken.stofradar.luchtmeetnet;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * See <a href="https://api-docs.luchtmeetnet.nl/">Luchtmeetnet API</a>
 * Only the 'concentrations' part.
 */
public interface ILuchtmeetnetApi {

    @GET("/open_api/concentrations")
    Call<Concentrations> getConcentrations(@Query("formula") String formula,
                                           @Query("latitude") double latitude,
                                           @Query("longitude") double longitude);

}
