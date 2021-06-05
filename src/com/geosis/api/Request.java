package com.geosis.api;

import com.geosis.api.response.ApiResponse;
import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public class Request {

    public static String readJsonFromUrl(String url, ApiResponse response){
        String json = "";
        HttpClient client = HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_1_1)
                .followRedirects(HttpClient.Redirect.NORMAL)
                .connectTimeout(Duration.ofSeconds(20))
                .build();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .timeout(Duration.ofMinutes(2))
                .header("Content-Type","application/json")
                .GET()
                .build();
        try{
            json = client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                    .thenApplyAsync(resp -> {
                        response.setCode(resp.statusCode());
                        return resp;
                    }).thenApply(HttpResponse::body).get(10, TimeUnit.SECONDS);

        }catch (Exception e){
            e.printStackTrace();
            response.setMessage(e.getMessage());
        }
        return json;
    }
}
