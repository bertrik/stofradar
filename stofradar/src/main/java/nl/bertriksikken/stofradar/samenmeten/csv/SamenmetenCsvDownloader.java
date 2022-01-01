package nl.bertriksikken.stofradar.samenmeten.csv;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import okhttp3.OkHttpClient;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;

/**
 * Downloads data from the unofficial samenmeten CSV API and parses it into lines.
 */
public final class SamenmetenCsvDownloader {
    
    private static final Logger LOG = LoggerFactory.getLogger(SamenmetenCsvDownloader.class);

    private final ISamenmetenCsvRestApi restApi;

    SamenmetenCsvDownloader(ISamenmetenCsvRestApi restApi) {
        this.restApi = restApi;
    }
    
    public static SamenmetenCsvDownloader create(SamenmetenCsvConfig config) {
        LOG.info("Creating new REST client for URL '{}' with timeout {}", config.getUrl(), config.getTimeout());
        OkHttpClient client = new OkHttpClient().newBuilder().callTimeout(config.getTimeout()).build();
        Retrofit retrofit = new Retrofit.Builder().baseUrl(config.getUrl())
                .addConverterFactory(ScalarsConverterFactory.create())
                .client(client).build();
        ISamenmetenCsvRestApi restApi = retrofit.create(ISamenmetenCsvRestApi.class);
        return new SamenmetenCsvDownloader(restApi);
    }

    public List<String> downloadDataFromFile(String compartiment) throws IOException {
        List<String> result = new ArrayList<>();
        Response<String> response = restApi.getDataFromFile(compartiment).execute();
        if (response.isSuccessful()) {
            try (Scanner scanner = new Scanner(response.body()).useDelimiter(";")) {
                scanner.forEachRemaining(result::add);
            }
        }
        return result;
    }

}
