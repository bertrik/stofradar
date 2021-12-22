package nl.bertriksikken.luftdaten.api;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.time.Duration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nl.bertriksikken.luftdaten.config.LuftdatenConfig;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public final class LuftDatenDataApi {

    private static final Logger LOG = LoggerFactory.getLogger(LuftDatenDataApi.class);

    private final ILuftdatenRestApi api;

    /**
     * Constructor.
     * 
     * @param api the REST API
     */
    LuftDatenDataApi(ILuftdatenRestApi api) {
        this.api = api;
    }

    /**
     * Creates a REST interface.
     * 
     * @param url     the base URL
     * @param timeout timeout (ms)
     * @return REST interface
     */
    ILuftdatenRestApi newRestClient(String url, Duration timeout) {
        LOG.info("Creating new REST client for URL '{}' with timeout {}", url, timeout);
        OkHttpClient client = new OkHttpClient().newBuilder().callTimeout(timeout).build();
        Retrofit retrofit = new Retrofit.Builder().baseUrl(url).addConverterFactory(ScalarsConverterFactory.create())
                .client(client).build();
        return retrofit.create(ILuftdatenRestApi.class);
    }

    public static LuftDatenDataApi create(LuftdatenConfig config) {
        LOG.info("Creating new REST client for URL '{}' with timeout {}", config.getUrl(), config.getTimeoutSec());
        OkHttpClient client = new OkHttpClient().newBuilder().callTimeout(Duration.ofSeconds(config.getTimeoutSec()))
                .build();
        Retrofit retrofit = new Retrofit.Builder().baseUrl(config.getUrl())
                .addConverterFactory(ScalarsConverterFactory.create()).client(client).build();
        ILuftdatenRestApi restApi = retrofit.create(ILuftdatenRestApi.class);
        return new LuftDatenDataApi(restApi);
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
