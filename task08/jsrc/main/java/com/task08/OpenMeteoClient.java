package com.task08;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

public class OpenMeteoClient {
    private static final String BASE_URL = "https://api.open-meteo.com/v1/forecast";
    private static final String API_URL = "https://api.open-meteo.com/v1/forecast?latitude=50.4375&longitude=30.5&hourly=temperature_2m,relative_humidity_2m,wind_speed_10m&current_weather=true";
    private static final String PARAMS = "?latitude={lat}&longitude={lon}&current_weather=true";

    public String getWeather(double latitude, double longitude) throws Exception {
        String url = BASE_URL + PARAMS.replace("{lat}", String.valueOf(latitude))
                .replace("{lon}", String.valueOf(longitude));
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpGet request = new HttpGet(API_URL);
            try (CloseableHttpResponse response = httpClient.execute(request)) {
                return EntityUtils.toString(response.getEntity());
            }
        }
    }
}
