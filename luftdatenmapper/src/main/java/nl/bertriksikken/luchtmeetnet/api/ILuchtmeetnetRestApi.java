package nl.bertriksikken.luchtmeetnet.api;

import nl.bertriksikken.luchtmeetnet.api.dto.Components;
import nl.bertriksikken.luchtmeetnet.api.dto.Measurements;
import nl.bertriksikken.luchtmeetnet.api.dto.Organisations;
import nl.bertriksikken.luchtmeetnet.api.dto.Station;
import nl.bertriksikken.luchtmeetnet.api.dto.Stations;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ILuchtmeetnetRestApi {

    @GET("/open_api/stations")
    Call<Stations> getStations(@Query("page") int page);

    @GET("/open_api/stations/{number}")
    Call<Station> getStation(@Path("number") String number);

    @GET("/open_api/stations/{number}/measurements")
    Call<Measurements> getStationMeasurement(@Path("number") String number, @Query("page") int page);

    @GET("/open_api/organisations")
    Call<Organisations> getOrganisations(@Query("page") int page);

    @GET("/open_api/components")
    Call<Components> getComponents(@Query("page") int page);

}
