package dev.diona.southside.util.authentication;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import net.minecraft.util.text.TextFormatting;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class WebUtil {
    public static final ExecutorService EXECUTOR_SERVICE = Executors.newFixedThreadPool(2);
    public static final Gson gson = new GsonBuilder()
            .serializeNulls()
            .setPrettyPrinting()
            .create();

    public static String get(final String url, final Map<String, String> params) throws Exception {
        final var httpClient = HttpClients.custom().setSSLSocketFactory(SSLConnectionSocketFactory.getSocketFactory()).build();
        final var uriBuilder = new URIBuilder(url);
        if (params != null) params.forEach(uriBuilder::addParameter);
        HttpGet request = new HttpGet(uriBuilder.build());
        CloseableHttpResponse response = httpClient.execute(request);
        final var res = EntityUtils.toString(response.getEntity());
        httpClient.close();
        response.close();
        return res;
    }

    public static void api(final String url, final Map<String, String> params, final Callback<JsonObject> callback) {
        EXECUTOR_SERVICE.submit(() -> {
            try {
                callback.onSuccess(gson.fromJson(get(AuthenticationStatus.BACKEND_ENDPOINT + url, params), JsonObject.class));
            } catch (Exception e) {
                callback.onFail(e);
            }
        });
    }

    public static void api(final String url, final CallbackWithParams<JsonObject> callback) {
        EXECUTOR_SERVICE.submit(() -> {
            try {
                callback.onSuccess(gson.fromJson(get(AuthenticationStatus.BACKEND_ENDPOINT + url, callback.params(new HashMap<>())), JsonObject.class));
            } catch (Exception e) {
                callback.onFail(e);
            }
        });
    }

    public static void rawApi(final String url, final CallbackWithParams<String> callback) {
        EXECUTOR_SERVICE.submit(() -> {
            try {
                callback.onSuccess(get(AuthenticationStatus.BACKEND_ENDPOINT + url, callback.params(new HashMap<>())));
            } catch (Exception e) {
                callback.onFail(e);
            }
        });
    }

    public interface Callback<T> {
        void onSuccess(T e);
        void onFail(Exception e);

    }

    public interface CallbackWithParams<T> {
        HashMap<String, String> params(HashMap<String, String> params);
        void onSuccess(T e);
        void onFail(Exception e);

    }
}
