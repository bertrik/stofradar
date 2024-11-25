package nl.bertriksikken.stofradar.geolocation.unwiredlabs;

import com.fasterxml.jackson.databind.ObjectMapper;
import nl.bertriksikken.stofradar.config.HostConnectionConfig;
import nl.bertriksikken.stofradar.geolocation.GeoLocationRequest;
import nl.bertriksikken.stofradar.geolocation.GeoLocationResponse;
import okhttp3.OkHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

import java.io.IOException;
import java.time.Duration;
import java.util.Objects;

public class UnwiredClient implements AutoCloseable {

    private static final Logger LOG = LoggerFactory.getLogger(UnwiredClient.class);

    private final OkHttpClient httpClient;
    private final IUnwiredApi restApi;
    private final String token;
    private final String userAgent;

    UnwiredClient(OkHttpClient httpClient, IUnwiredApi restApi, String token, String userAgent) {
        this.httpClient = Objects.requireNonNull(httpClient);
        this.restApi = Objects.requireNonNull(restApi);
        this.token = Objects.requireNonNull(token);
        this.userAgent = Objects.requireNonNull(userAgent);
    }

    public static UnwiredClient create(HostConnectionConfig config, String token, ObjectMapper mapper, String userAgent) {
        LOG.info("Creating new REST client for URL '{}' with timeout {}", config.getUrl(), config.getTimeout());
        Duration timeout = config.getTimeout();
        OkHttpClient client = new OkHttpClient().newBuilder().connectTimeout(timeout).readTimeout(timeout).writeTimeout(timeout).build();
        Retrofit retrofit = new Retrofit.Builder().baseUrl(config.getUrl()).addConverterFactory(JacksonConverterFactory.create(mapper)).client(client).build();
        IUnwiredApi restApi = retrofit.create(IUnwiredApi.class);
        return new UnwiredClient(client, restApi, token, userAgent);
    }

    @Override
    public void close() {
        httpClient.dispatcher().executorService().shutdown();
        httpClient.connectionPool().evictAll();
    }

    public GeoLocationResponse geoLocate(GeoLocationRequest request) throws IOException {
        // build request
        UnwiredGeoLocationRequest unwiredRequest = new UnwiredGeoLocationRequest(token, 0);
        request.accessPoints().forEach(r -> unwiredRequest.addWifi(r.mac(), r.signal()));

        // execute
        Response<UnwiredGeoLocationResponse> response = restApi.geoLocate(userAgent, unwiredRequest).execute();
        if (response.isSuccessful()) {
            // convert back
            UnwiredGeoLocationResponse unwiredResponse = response.body();
            if (unwiredResponse.status().equals("ok")) {
                return new GeoLocationResponse(unwiredResponse.latitude(), unwiredResponse.longitude(), unwiredResponse.accuracy());
            } else {
                LOG.warn("Received status '{}' with message: {}", unwiredResponse.status(), unwiredResponse.message());
                return GeoLocationResponse.invalid();
            }
        } else {
            LOG.warn("Got error: {}", response.errorBody().string());
            return GeoLocationResponse.invalid();
        }
    }

}
