package nl.bertriksikken.stofradar.senscom;

import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nl.bertriksikken.stofradar.config.HostConnectionConfig;
import nl.bertriksikken.stofradar.senscom.dto.DataPoint;
import okhttp3.OkHttpClient;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

public final class SensComDataApi {

    private static final Logger LOG = LoggerFactory.getLogger(SensComDataApi.class);

    private final ISensComRestApi api;

    /**
     * Constructor.
     * 
     * @param api the REST API
     */
    SensComDataApi(ISensComRestApi api) {
        this.api = api;
    }

    public static SensComDataApi create(HostConnectionConfig config) {
        LOG.info("Creating new REST client for URL '{}' with timeout {}", config.getUrl(), config.getTimeout());
        Duration timeout = config.getTimeout();
        OkHttpClient client = new OkHttpClient().newBuilder().connectTimeout(timeout).readTimeout(timeout)
                .writeTimeout(timeout).build();
        Retrofit retrofit = new Retrofit.Builder().baseUrl(config.getUrl())
                .addConverterFactory(JacksonConverterFactory.create()).client(client).build();
        ISensComRestApi restApi = retrofit.create(ISensComRestApi.class);
        return new SensComDataApi(restApi);
    }

    public List<DataPoint> downloadDust() throws IOException {
        List<DataPoint> entries = new ArrayList<>();
        Response<List<DataPoint>> response = api.getAverageDustData().execute();
        if (response.isSuccessful()) {
            entries.addAll(response.body());
        } else {
            LOG.warn("Download failed: {}", response.errorBody());
        }
        return entries;
    }

}
