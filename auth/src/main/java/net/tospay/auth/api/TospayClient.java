package net.tospay.auth.api;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import net.tospay.auth.BuildConfig;
import net.tospay.auth.api.service.GatewayService;
import net.tospay.auth.api.service.UserService;
import net.tospay.auth.remote.util.RequestInterceptor;
import net.tospay.auth.utils.SharedPrefManager;
import net.tospay.auth.utils.BooleanSerializerDeserializer;

import java.util.Collections;
import java.util.concurrent.TimeUnit;

import io.reactivex.schedulers.Schedulers;
import okhttp3.CipherSuite;
import okhttp3.ConnectionSpec;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.TlsVersion;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

import static net.tospay.auth.api.ApiConstants.CONNECT_TIMEOUT;
import static net.tospay.auth.api.ApiConstants.READ_TIMEOUT;
import static net.tospay.auth.api.ApiConstants.WRITE_TIMEOUT;

public class TospayClient {

    private static BooleanSerializerDeserializer booleanSerializerDeserializer
            = new BooleanSerializerDeserializer();

    private static RxJava2CallAdapterFactory rxAdapter
            = RxJava2CallAdapterFactory.createWithScheduler(Schedulers.io());

    private static Gson gson = new GsonBuilder()
            .setDateFormat("yyyy-MM-dd'T'HH:mm:ss")
            .serializeNulls()
            .registerTypeAdapter(Boolean.class, booleanSerializerDeserializer)
            .registerTypeAdapter(boolean.class, booleanSerializerDeserializer)
            .create();

    /**
     * Get Retrofit Instance
     */
    private static Retrofit getRetrofitInstance(String url, Context context) {

        String accessToken = getAccessToken(context);

        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();

        HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor();
        httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        //Disable http logging in production
        if (BuildConfig.DEBUG) {
            httpClient.addInterceptor(chain -> {
                Request original = chain.request();

                Request.Builder requestBuilder = original.newBuilder()
                        .header("Accept", "application/json")
                        .header("Content-Type", "application/json")
                        .method(original.method(), original.body());

                Request request = requestBuilder.build();
                return chain.proceed(request);
            });
            httpClient.addInterceptor(httpLoggingInterceptor);
        }

        //Add bearer token if user is logged in
        if (accessToken != null) {
            httpClient.addInterceptor(chain -> {
                Request original = chain.request();
                Request.Builder requestBuilder = original.newBuilder()
                        .header("Authorization", "Bearer " + accessToken)
                        .method(original.method(), original.body());
                Request request = requestBuilder.build();
                return chain.proceed(request);
            });
        }

        OkHttpClient client = httpClient
                .connectTimeout(60, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                //.connectionSpecs(Collections.singletonList(spec))
                .build();

        return new Retrofit.Builder()
                .baseUrl(url + "/api/")
                .client(client)
                .addCallAdapterFactory(rxAdapter)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
    }

    /**
     * Request access token
     *
     * @param context
     * @return
     */
    private static String getAccessToken(Context context) {
        if (SharedPrefManager.getInstance(context).getActiveUser() != null) {
            return SharedPrefManager.getInstance(context).getActiveUser().getToken();
        }

        return null;
    }


    public static final String USER_URL = "https://auth.tospay.net";

    /**
     * Get User API Services
     *
     * @return User Service
     */
    public static UserService getUserService(Context context) {
        return getRetrofitInstance(USER_URL, context).create(UserService.class);
    }

    public static final String GATEWAY_URL = "https://apigw.tospay.net";

    /**
     * Get Gateway service
     *
     * @param context
     * @return
     */
    public static GatewayService getGatewayService(Context context) {
        return getRetrofitInstance(GATEWAY_URL, context).create(GatewayService.class);
    }
}
