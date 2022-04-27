package nl.bertriksikken.stofradar.senscom;

import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import nl.bertriksikken.stofradar.senscom.dto.DataPoint;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public final class SensComDataApi {

    private static final Logger LOG = LoggerFactory.getLogger(SensComDataApi.class);

    private final ISensComRestApi api;
    private final ObjectMapper mapper;

    /**
     * Constructor.
     * 
     * @param api the REST API
     */
    SensComDataApi(ISensComRestApi api, ObjectMapper mapper) {
        this.api = api;
        this.mapper = mapper;
    }

    /**
     * Creates a REST interface.
     * 
     * @param url     the base URL
     * @param timeout timeout (ms)
     * @return REST interface
     */
    ISensComRestApi newRestClient(String url, Duration timeout) {
        LOG.info("Creating new REST client for URL '{}' with timeout {}", url, timeout);
        OkHttpClient client = new OkHttpClient().newBuilder().callTimeout(timeout).build();
        Retrofit retrofit = new Retrofit.Builder().baseUrl(url).addConverterFactory(ScalarsConverterFactory.create())
                .client(client).build();
        return retrofit.create(ISensComRestApi.class);
    }

    public static SensComDataApi create(SensComConfig config, ObjectMapper mapper) {
        LOG.info("Creating new REST client for URL '{}' with timeout {}", config.getUrl(), config.getTimeoutSec());
        OkHttpClient client = new OkHttpClient().newBuilder().callTimeout(Duration.ofSeconds(config.getTimeoutSec()))
                .build();
        Retrofit retrofit = new Retrofit.Builder().baseUrl(config.getUrl())
                .addConverterFactory(ScalarsConverterFactory.create()).client(client).build();
        ISensComRestApi restApi = retrofit.create(ISensComRestApi.class);
        return new SensComDataApi(restApi, mapper);
    }

    public List<DataPoint> downloadDust() throws IOException {
        retrofit2.Response<String> response = api.getAverageDustData().execute();
        if (response.isSuccessful()) {
            String json = response.body();
            return mapper.readValue(json, new TypeReference<List<DataPoint>>() {});
        } else {
            LOG.warn("Download failed!");
            return new ArrayList<>();
        }
    }

}
