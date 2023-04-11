package org.devgateway.viz.commons.io;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;


public abstract class BaseCKANImporter<T> extends BaseImport<T,HashMap> {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    protected ObjectMapper mapper = new ObjectMapper();

    public Integer start(final String host, final String qs) throws MalformedURLException, URISyntaxException {
        beforeStart();

        int[] count = new int[1];
        count[0]++;
        try {
            logger.info("................ Getting data from " + host + qs + " ....................");

            URI targetURI = new URI(host + qs);
            final Integer[] current = new Integer[1];
            current[0] = 0;
            int currentPage = 1;
            int totalPages = -1;
            boolean hasData = true;
            while (hasData) {
                HttpRequest request = null;
                HttpClient client = HttpClient.newHttpClient();
                request = HttpRequest.newBuilder(targetURI).header("Accept", "application/json").build();
                String body = client.send(request, HttpResponse.BodyHandlers.ofString()).body();
                CKANResponse response = mapper.readValue(body, CKANResponse.class);
                response.getResult().getRecords().stream().forEach(hashMap -> {
                    try {
                        T record = read(hashMap);
                        logger.info("Record counts " + (count[0]++));
                        save(record);
                    } catch (NullPointerException e) {
                        logger.error("Got null value", e);
                    }
                });

                targetURI = new URI(host + response.getResult().get_links().get("next"));

                totalPages = response.getResult().getTotal() / response.getResult().getLimit();
                if (response.getResult().getTotal() % response.getResult().getLimit() > 0) {
                    totalPages++;
                }

                logger.info(" page ->" + currentPage);
                hasData = currentPage < totalPages;
                currentPage++;

            }
            logger.info("Data has been imported");
            return 0;

        } catch (Exception e) {
            logger.warn("Wasn't able to update data");
            logger.error("Error", e);
        }
        return -1;
    }
}
