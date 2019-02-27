package nl.bertriksikken.luchtmeetnet.api;

import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nl.bertriksikken.luchtmeetnet.api.dto.Components;
import nl.bertriksikken.luchtmeetnet.api.dto.ComponentsData;
import nl.bertriksikken.luchtmeetnet.api.dto.MeasurementData;
import nl.bertriksikken.luchtmeetnet.api.dto.Measurements;
import nl.bertriksikken.luchtmeetnet.api.dto.OrganisationData;
import nl.bertriksikken.luchtmeetnet.api.dto.Organisations;
import nl.bertriksikken.luchtmeetnet.api.dto.PagedResponse;
import nl.bertriksikken.luchtmeetnet.api.dto.Station;
import nl.bertriksikken.luchtmeetnet.api.dto.StationData;
import nl.bertriksikken.luchtmeetnet.api.dto.Stations;
import okhttp3.OkHttpClient;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

public final class LuchtmeetnetApi {

    private static final Logger LOG = LoggerFactory.getLogger(LuchtmeetnetApi.class);

    private final ILuchtmeetnetRestApi api;

    public LuchtmeetnetApi(ILuchtmeetnetRestApi api) {
        this.api = api;
    }

    public static ILuchtmeetnetRestApi newRestClient(String url, Duration timeout) {
        LOG.info("Creating new REST client for URL '{}' with timeout {}", url, timeout);
        OkHttpClient client = new OkHttpClient().newBuilder().connectTimeout(timeout).writeTimeout(timeout)
                .readTimeout(timeout).build();
        Retrofit retrofit = new Retrofit.Builder().baseUrl(url).addConverterFactory(JacksonConverterFactory.create())
                .client(client).build();
        return retrofit.create(ILuchtmeetnetRestApi.class);
    }

    /**
     * Retrieves all station numbers.
     * 
     * @return a list of station numbers
     * @throws IOException
     */
    public List<String> getStationNumbers() throws IOException {
        int page = 1;
        List<String> list = new ArrayList<>();
        while (true) {
            // iterate over all pages
            Response<Stations> response = api.getStations(page).execute();
            if (!response.isSuccessful()) {
                throw new IOException("Request failed for page " + page);
            }
            Stations stations = response.body();
            stations.getData().forEach(s -> list.add(s.getNumber()));
            // next page?
            if (page < stations.getPagination().getLastPage()) {
                page++;
            } else {
                return list;
            }
        }
    }
    
    public List<OrganisationData> getOrganisations() throws IOException {
        int page = 1;
        List<OrganisationData> list = new ArrayList<>();
        while (true) {
            // iterate over all pages
            Response<Organisations> response = api.getOrganisations(page).execute();
            if (!response.isSuccessful()) {
                throw new IOException("Request failed for page " + page);
            }
            PagedResponse<OrganisationData> body = response.body();
            list.addAll(body.getData());
            // next page?
            if (page < body.getPagination().getLastPage()) {
                page++;
            } else {
                return list;
            }
        }
    }

    public List<ComponentsData> getComponents() throws IOException {
        int page = 1;
        List<ComponentsData> list = new ArrayList<>();
        while (true) {
            // iterate over all pages
            Response<Components> response = api.getComponents(page).execute();
            if (!response.isSuccessful()) {
                throw new IOException("Request failed for page " + page);
            }
            PagedResponse<ComponentsData> body = response.body();
            list.addAll(body.getData());
            // next page?
            if (page < body.getPagination().getLastPage()) {
                page++;
            } else {
                return list;
            }
        }
    }

    /**
     * Retrieves the station data for a specific station.
     * 
     * @param number the station number
     * @return the station data
     * @throws IOException
     */
    public StationData getStationData(String number) throws IOException {
        Response<Station> response = api.getStation(number).execute();
        if (!response.isSuccessful()) {
            throw new IOException("Request failed for station " + number);
        }
        Station station = response.body();
        return station.getData();
    }

    public List<MeasurementData> getStationMeasurements(String number) throws IOException {
        int page = 1;
        Response<Measurements> response = api.getStationMeasurement(number, 1).execute();
        if (!response.isSuccessful()) {
            throw new IOException("Request failed for page " + page);
        }
        Measurements measurements = response.body();
        return measurements.getData();
    }

}
