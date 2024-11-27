package nl.bertriksikken.stofradar.senscom;

import com.fasterxml.jackson.databind.ObjectMapper;
import nl.bertriksikken.stofradar.config.HostConnectionConfig;
import nl.bertriksikken.stofradar.senscom.dto.DataPoint;
import okhttp3.OkHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class SensComDataApi {

    private static final Logger LOG = LoggerFactory.getLogger(SensComDataApi.class);

    private final ISensComRestApi restApi;
    private final String userAgent;

    /**
     * Constructor.
     *
     * @param restApi the REST API
     */
    SensComDataApi(ISensComRestApi restApi, String userAgent) {
        this.restApi = Objects.requireNonNull(restApi);
        this.userAgent = Objects.requireNonNull(userAgent);
    }

    public static SensComDataApi create(HostConnectionConfig config, ObjectMapper mapper, String userAgent) {
        LOG.info("Creating new REST client for URL '{}' with timeout {}", config.getUrl(), config.getTimeout());
        Duration timeout = config.getTimeout();
        OkHttpClient client = new OkHttpClient().newBuilder()
                .connectTimeout(timeout).readTimeout(timeout).writeTimeout(timeout).build();
        Retrofit retrofit = new Retrofit.Builder().baseUrl(config.getUrl())
                .addConverterFactory(JacksonConverterFactory.create(mapper)).client(client).build();
        ISensComRestApi restApi = retrofit.create(ISensComRestApi.class);
        return new SensComDataApi(restApi, userAgent);
    }

    public List<DataPoint> downloadDust() throws IOException {
        List<DataPoint> entries = new ArrayList<>();
        Response<List<DataPoint>> response = restApi.getAverageDustData(userAgent).execute();
        if (response.isSuccessful()) {
            entries.addAll(response.body());
        } else {
            LOG.warn("Download failed: {} - {}", response.message(), response.errorBody());
        }
        return entries;
    }

}
