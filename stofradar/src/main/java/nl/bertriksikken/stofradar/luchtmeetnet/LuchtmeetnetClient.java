package nl.bertriksikken.stofradar.luchtmeetnet;

import nl.bertriksikken.stofradar.config.HostConnectionConfig;
import okhttp3.OkHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

public final class LuchtmeetnetClient implements AutoCloseable {

    private static final Logger LOG = LoggerFactory.getLogger(LuchtmeetnetClient.class);

    private final ILuchtmeetnetApi restApi;
    private final OkHttpClient httpClient;

    LuchtmeetnetClient(OkHttpClient httpClient, ILuchtmeetnetApi restApi) {
        this.httpClient = Objects.requireNonNull(httpClient);
        this.restApi = Objects.requireNonNull(restApi);
    }

    public static LuchtmeetnetClient create(HostConnectionConfig config) {
        LOG.info("Creating new REST client for URL '{}' with timeout {}", config.url(), config.timeout());
        OkHttpClient httpClient = new OkHttpClient().newBuilder().connectTimeout(config.timeout()).writeTimeout(config.timeout())
                .readTimeout(config.timeout()).build();
        Retrofit retrofit = new Retrofit.Builder().baseUrl(config.url()).addConverterFactory(JacksonConverterFactory.create())
                .client(httpClient).build();
        ILuchtmeetnetApi restApi = retrofit.create(ILuchtmeetnetApi.class);
        return new LuchtmeetnetClient(httpClient, restApi);
    }

    @Override
    public void close() {
        httpClient.dispatcher().executorService().shutdown();
        httpClient.connectionPool().evictAll();
    }

    public double getMostRecentLki(double latitude, double longitude) throws IOException {
        Response<Concentrations> response = restApi.getConcentrations("lki", latitude, longitude).execute();
        if (response.isSuccessful()) {
            Concentrations concentrations = response.body();
            List<Concentrations.Data> data = concentrations.data();
            if (!data.isEmpty()) {
                Concentrations.Data element = data.get(data.size() - 1);
                return element.value();
            }
        }
        return Double.NaN;
    }

}
