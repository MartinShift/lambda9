package com.task09;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * A lightweight SDK for interacting with the Open-Meteo API
 */
public class WeatherSDK {
    private static final String API_URL = "https://api.open-meteo.com/v1/forecast?latitude=52.52&longitude=13.41&current=temperature_2m,wind_speed_10m&hourly=temperature_2m,relative_humidity_2m,wind_speed_10m";

    /**
     * Fetches weather forecast data from Open-Meteo API
     * @return String - Weather forecast data in JSON format
     * @throws IOException if an I/O error occurs
     */
    public String getWeatherForecast() throws IOException {
        URL url = new URL(API_URL);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        
        int responseCode = connection.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) {
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String inputLine;
            StringBuilder response = new StringBuilder();
            
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();
            
            return response.toString();
        } else {
            throw new IOException("HTTP request failed with response code: " + responseCode);
        }
    }
}