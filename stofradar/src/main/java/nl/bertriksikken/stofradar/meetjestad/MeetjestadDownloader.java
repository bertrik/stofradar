package nl.bertriksikken.stofradar.meetjestad;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nl.bertriksikken.stofradar.meetjestad.MeetjestadData.MeetjestadDataEntry;
import okhttp3.OkHttpClient;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

public final class MeetjestadDownloader {

    private static final Logger LOG = LoggerFactory.getLogger(MeetjestadDownloader.class);
    private static final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd,HH:mm")
            .withZone(ZoneOffset.UTC);

    private final IMeetjestadRestApi restApi;

    MeetjestadDownloader(IMeetjestadRestApi restApi) {
        this.restApi = restApi;
    }

    public static MeetjestadDownloader create(MeetjestadConfig config) {
        LOG.info("Creating new REST client for URL '{}' with timeout {}", config.getUrl(), config.getTimeoutSec());
        OkHttpClient client = new OkHttpClient().newBuilder().callTimeout(Duration.ofSeconds(config.getTimeoutSec()))
                .build();
        Retrofit retrofit = new Retrofit.Builder().baseUrl(config.getUrl())
                .addConverterFactory(JacksonConverterFactory.create()).client(client).build();
        IMeetjestadRestApi restApi = retrofit.create(IMeetjestadRestApi.class);
        return new MeetjestadDownloader(restApi);
    }

    public MeetjestadData download(Instant from) throws IOException {
        List<MeetjestadDataEntry> entries = new ArrayList<>();

        // build request
        Map<String, String> queryMap = new HashMap<>();
        queryMap.put("type", "sensors");
        queryMap.put("format", "json");
        queryMap.put("begin", DATETIME_FORMATTER.format(from));

        // execute
        Response<List<MeetjestadDataEntry>> response = restApi.getData(queryMap).execute();
        if (response.isSuccessful()) {
            List<MeetjestadDataEntry> data = response.body();
            entries.addAll(data);
        } else {
            LOG.warn("MJS download failed: {} - {}", response.code(), response.errorBody());
        }
        return new MeetjestadData(entries);
    }

}
