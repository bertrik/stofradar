package nl.bertriksikken.stofradar.samenmeten.csv;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ISamenmetenCsvRestApi {

    @GET("/dataportaal/php/getData-fromfile.php")
    Call<String> getDataFromFile(@Query("compartiment") String compartiment);
    
}
