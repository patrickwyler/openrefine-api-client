package ch.wyler.client;

import java.io.IOException;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import ch.wyler.client.dtos.GetCsrfTokenResponse;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Call;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class OpenRefineApiServiceGenerator {

    private static final OkHttpClient.Builder httpClient = new OkHttpClient.Builder();

    public static OpenRefineApiService createService(final String baseUrl) {
        httpClient.addInterceptor(chain -> {
            final Request original = chain.request();
            final HttpUrl originalHttpUrl = original.url();

            if ("GET".equalsIgnoreCase(original.method())) {
                // generate no csrf token
                return chain.proceed(original);
            }

            final HttpUrl url = originalHttpUrl.newBuilder()
                    .addQueryParameter("csrf_token", getCsrfToken(baseUrl))
                    .build();

            final Request.Builder requestBuilder = original.newBuilder()
                    .url(url);

            final Request request = requestBuilder.build();
            return chain.proceed(request);
        });

        final Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        final Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(httpClient.build())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        return retrofit.create(OpenRefineApiService.class);
    }

    private static String getCsrfToken(final String baseUrl) {
        try {
            final ObjectMapper objectMapper = new ObjectMapper();
            final OkHttpClient client = new OkHttpClient();
            final Request request = new Request.Builder()
                    .url(baseUrl + "/command/core/get-csrf-token")
                    .build();

            final Call call = client.newCall(request);
            final Response response = call.execute();
            final GetCsrfTokenResponse csrfTokenResponse = objectMapper.readValue(response.body().string(), GetCsrfTokenResponse.class);
            return csrfTokenResponse.getToken();
        } catch (final IOException e) {
            log.error("Can't get csrf token.", e);
            return "";
        }
    }
}
