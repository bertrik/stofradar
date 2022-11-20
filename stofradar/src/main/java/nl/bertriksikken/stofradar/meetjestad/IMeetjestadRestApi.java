package nl.bertriksikken.stofradar.meetjestad;

import java.util.List;
import java.util.Map;

import nl.bertriksikken.stofradar.meetjestad.MeetjestadData.MeetjestadDataEntry;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.QueryMap;

/**
 * Example URL:
 * https://meetjestad.net/data/?type=sensors&format=json&begin=2022-05-12,14:46
 */
public interface IMeetjestadRestApi {

    @GET("/data")
    Call<List<MeetjestadDataEntry>> getData(@QueryMap Map<String, String> queryMap);

}
