package nl.bertriksikken.stofradar.samenmeten.csv;

import java.io.IOException;
import java.time.Duration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nl.bertriksikken.stofradar.config.HostConnectionConfig;
import okhttp3.OkHttpClient;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;

/**
 * Downloads data from the unofficial samenmeten CSV API and parses it into
 * lines.
 */
public final class SamenmetenCsvDownloader {

    private static final Logger LOG = LoggerFactory.getLogger(SamenmetenCsvDownloader.class);

    private final ISamenmetenCsvRestApi restApi;

    SamenmetenCsvDownloader(ISamenmetenCsvRestApi restApi) {
        this.restApi = restApi;
    }

    public static SamenmetenCsvDownloader create(HostConnectionConfig config) {
        LOG.info("Creating new REST client for URL '{}' with timeout {}", config.getUrl(), config.getTimeout());
        Duration timeout = config.getTimeout();
        OkHttpClient client = new OkHttpClient().newBuilder().connectTimeout(timeout).readTimeout(timeout)
                .writeTimeout(timeout).build();
        Retrofit retrofit = new Retrofit.Builder().baseUrl(config.getUrl())
                .addConverterFactory(ScalarsConverterFactory.create()).client(client).build();
        ISamenmetenCsvRestApi restApi = retrofit.create(ISamenmetenCsvRestApi.class);
        return new SamenmetenCsvDownloader(restApi);
    }

    public SamenmetenCsv downloadDataFromFile(String compartiment) throws IOException {
        Response<String> response = restApi.getDataFromFile(compartiment).execute();
        if (response.isSuccessful()) {
            return SamenmetenCsv.parse(response.body());
        }
        return null;
    }

}
