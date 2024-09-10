package nl.bertriksikken.stofradar.senscom;

import com.fasterxml.jackson.databind.ObjectMapper;
import nl.bertriksikken.stofradar.config.HostConnectionConfig;
import nl.bertriksikken.stofradar.senscom.dto.DataPoint;
import okhttp3.Interceptor.Chain;
import okhttp3.OkHttpClient;
import okhttp3.Request;
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

    private static final String USER_AGENT = "github.com/bertrik/stofradar";

    private final ISensComRestApi restApi;

    /**
     * Constructor.
     *
     * @param restApi the REST API
     */
    SensComDataApi(ISensComRestApi restApi) {
        this.restApi = Objects.requireNonNull(restApi);
    }

    public static SensComDataApi create(HostConnectionConfig config, ObjectMapper mapper) {
        LOG.info("Creating new REST client for URL '{}' with timeout {}", config.getUrl(), config.getTimeout());
        Duration timeout = config.getTimeout();
        OkHttpClient client = new OkHttpClient().newBuilder().addInterceptor(SensComDataApi::addUserAgent)
                .connectTimeout(timeout).readTimeout(timeout).writeTimeout(timeout).build();
        Retrofit retrofit = new Retrofit.Builder().baseUrl(config.getUrl())
                .addConverterFactory(JacksonConverterFactory.create(mapper)).client(client).build();
        ISensComRestApi restApi = retrofit.create(ISensComRestApi.class);
        return new SensComDataApi(restApi);
    }

    private static okhttp3.Response addUserAgent(Chain chain) throws IOException {
        Request userAgentRequest = chain.request().newBuilder().header("User-Agent", USER_AGENT).build();
        return chain.proceed(userAgentRequest);
    }

    public List<DataPoint> downloadDust() throws IOException {
        List<DataPoint> entries = new ArrayList<>();
        Response<List<DataPoint>> response = restApi.getAverageDustData().execute();
        if (response.isSuccessful()) {
            entries.addAll(response.body());
        } else {
            LOG.warn("Download failed: {} - {}", response.message(), response.errorBody());
        }
        return entries;
    }

}
