package nl.bertriksikken.stofradar.geolocation.beacondb;

import com.fasterxml.jackson.databind.ObjectMapper;
import nl.bertriksikken.stofradar.config.HostConnectionConfig;
import nl.bertriksikken.stofradar.geolocation.GeoLocationRequest;
import nl.bertriksikken.stofradar.geolocation.GeoLocationResponse;
import nl.bertriksikken.stofradar.geolocation.IGeoLocator;
import okhttp3.OkHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

import java.io.IOException;
import java.time.Duration;
import java.util.Objects;

public final class BeacondbClient implements AutoCloseable, IGeoLocator {

    private static final Logger LOG = LoggerFactory.getLogger(BeacondbClient.class);

    private final OkHttpClient httpClient;
    private final IBeacondbApi restApi;
    private final String userAgent;

    BeacondbClient(OkHttpClient client, IBeacondbApi restApi, String userAgent) {
        this.httpClient = Objects.requireNonNull(client);
        this.restApi = Objects.requireNonNull(restApi);
        this.userAgent = Objects.requireNonNull(userAgent);
    }

    public static BeacondbClient create(HostConnectionConfig config, ObjectMapper mapper, String userAgent) {
        LOG.info("Creating new REST client for URL '{}' with timeout {}", config.getUrl(), config.getTimeout());
        Duration timeout = config.getTimeout();
        OkHttpClient client = new OkHttpClient().newBuilder().connectTimeout(timeout).readTimeout(timeout).writeTimeout(timeout).build();
        Retrofit retrofit = new Retrofit.Builder().baseUrl(config.getUrl()).addConverterFactory(JacksonConverterFactory.create(mapper)).client(client).build();
        IBeacondbApi restApi = retrofit.create(IBeacondbApi.class);
        return new BeacondbClient(client, restApi, userAgent);
    }

    @Override
    public void close() {
        httpClient.dispatcher().executorService().shutdown();
        httpClient.connectionPool().evictAll();
    }

    @Override
    public GeoLocationResponse geoLocate(GeoLocationRequest request) throws IOException {
        Response<GeoLocationResponse> response = restApi.geoLocate(userAgent, request).execute();
        if (response.isSuccessful()) {
            return response.body();
        } else {
            LOG.warn("Got error: {}", response.errorBody().string());
            return GeoLocationResponse.invalid();
        }
    }

}
