package nl.bertriksikken.stofradar.senscom;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.time.Duration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;

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

    public static SensComDataApi create(SensComConfig config) {
        LOG.info("Creating new REST client for URL '{}' with timeout {}", config.getUrl(), config.getTimeoutSec());
        OkHttpClient client = new OkHttpClient().newBuilder().callTimeout(Duration.ofSeconds(config.getTimeoutSec()))
                .build();
        Retrofit retrofit = new Retrofit.Builder().baseUrl(config.getUrl())
                .addConverterFactory(ScalarsConverterFactory.create()).client(client).build();
        ISensComRestApi restApi = retrofit.create(ISensComRestApi.class);
        return new SensComDataApi(restApi);
    }

    public void downloadDust(File file) throws IOException {
        retrofit2.Response<String> response = api.getAverageDustData().execute();
        if (response.isSuccessful()) {
            LOG.info("Writing response to file {}", file.getName());
            String json = response.body();
            try (Writer writer = new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8)) {
                writer.write(json);
            }
        } else {
            LOG.warn("Download failed!");
        }
    }

}
