package nl.bertriksikken.stofradar.samenmeten.csv;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * REST interface for internal data API used on the samenmeten data portal.<br>
 * <br>
 * This API provides an easy-to-use CSV-like snapshot of the current samenmeten data.
 */
public interface ISamenmetenCsvRestApi {

    @GET("/dataportaal/php/getData-fromfile.php")
    Call<String> getDataFromFile(@Query("compartiment") String compartiment);
    
}
