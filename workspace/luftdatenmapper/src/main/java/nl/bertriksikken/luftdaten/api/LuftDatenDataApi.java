package nl.bertriksikken.luftdaten.api;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

public class LuftDatenDataApi {

    private static final Logger LOG = LoggerFactory.getLogger(LuftDatenDataApi.class);

    private final ObjectMapper mapper = new ObjectMapper();
    private final ILuftdatenRestApi api;

    /**
     * Creates a REST interface.
     * 
     * @param url the base URL
     * @param timeout timeout (ms)
     * @return REST interface
     */
    public static ILuftdatenRestApi newRestClient(String url, int timeout) {
        LOG.info("Creating new REST client for URL '{}' with timeout {}", url, timeout);
        OkHttpClient client = new OkHttpClient().newBuilder().connectTimeout(timeout, TimeUnit.MILLISECONDS)
                .writeTimeout(timeout, TimeUnit.MILLISECONDS).readTimeout(timeout, TimeUnit.MILLISECONDS).build();
        Retrofit retrofit = new Retrofit.Builder().baseUrl(url).addConverterFactory(JacksonConverterFactory.create())
                .client(client).build();
        return retrofit.create(ILuftdatenRestApi.class);
    }

    /**
     * Constructor.
     * 
     * @param api the REST API
     */
    public LuftDatenDataApi(ILuftdatenRestApi api) {
        this.api = api;
    }

    public void downloadDust(File file) throws IOException {
        retrofit2.Response<JsonNode> response = api.getAverageDustData().execute();
        if (response.isSuccessful()) {
            JsonNode node = response.body();
            mapper.writeValue(file, node);
            LOG.info("Wrote to file {}", file.getName());
        } else {
            LOG.warn("Download failed!");
        }
    }

}
