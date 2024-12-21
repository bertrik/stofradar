package nl.bertriksikken.stofradar.luchtmeetnet;

import nl.bertriksikken.stofradar.config.HostConnectionConfig;

import java.io.IOException;

public final class RunLuchtmeetnetClient {

    public static void main(String[] args) throws IOException {
        RunLuchtmeetnetClient runner = new RunLuchtmeetnetClient();
        runner.run();
    }

    private void run() throws IOException {
        HostConnectionConfig config = new HostConnectionConfig("https://api.luchtmeetnet.nl", 10);
        try (LuchtmeetnetClient client = LuchtmeetnetClient.create(config)) {
            double lki = client.getMostRecentLki(52.01807539597756, 4.707293008605676);
            System.out.println("LKI is " + lki);
        }
    }

}
