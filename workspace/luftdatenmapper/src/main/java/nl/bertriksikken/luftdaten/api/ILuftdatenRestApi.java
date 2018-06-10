package nl.bertriksikken.luftdaten.api;

import com.fasterxml.jackson.databind.JsonNode;

import retrofit2.Call;
import retrofit2.http.GET;

/**
 * Interface towards the luftdaten API to get 5 minute dust averages. 
 */
public interface ILuftdatenRestApi {

	@GET("/static/v2/data.dust.min.json")
	Call<JsonNode> getAverageDustData();

}
