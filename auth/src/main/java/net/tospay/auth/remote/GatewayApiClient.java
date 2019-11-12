package net.tospay.auth.remote;

import androidx.annotation.NonNull;

import net.tospay.auth.BuildConfig;
import net.tospay.auth.remote.service.GatewayService;
import net.tospay.auth.remote.util.LiveDataCallAdapterFactory;
import net.tospay.auth.remote.util.RequestInterceptor;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

import static net.tospay.auth.remote.ApiConstants.CONNECT_TIMEOUT;
import static net.tospay.auth.remote.ApiConstants.GATEWAY_BASE_URL;
import static net.tospay.auth.remote.ApiConstants.READ_TIMEOUT;
import static net.tospay.auth.remote.ApiConstants.WRITE_TIMEOUT;

/**
 * @author Fredrick Ochieng
 */
public class GatewayApiClient {

    /**
     * Logs network requests
     *
     * @return HttpLoggingInterceptor
     */
    private static HttpLoggingInterceptor loggingInterceptor() {
        return new HttpLoggingInterceptor().
                setLevel(BuildConfig.DEBUG ? HttpLoggingInterceptor.Level.BODY
                        : HttpLoggingInterceptor.Level.NONE);
    }

    /**
     * Configure OkHttpClient. This helps us override some of the default configuration. Like the
     * connection timeout.
     *
     * @return OkHttpClient
     */
    private static OkHttpClient okHttpClient(HttpLoggingInterceptor httpLoggingInterceptor) {
        return new OkHttpClient.Builder()
                .addInterceptor(httpLoggingInterceptor)
                .connectTimeout(CONNECT_TIMEOUT, TimeUnit.SECONDS)
                .writeTimeout(WRITE_TIMEOUT, TimeUnit.SECONDS)
                .readTimeout(READ_TIMEOUT, TimeUnit.SECONDS)
                .addInterceptor(new RequestInterceptor())
                .build();
    }

    /**
     * Creates an instance of retrofit
     *
     * @param okHttpClient - OkHttpClient
     * @return Retrofit
     */
    private static Retrofit retrofitClient(@NonNull OkHttpClient okHttpClient) {
        return new Retrofit.Builder()
                .baseUrl(GATEWAY_BASE_URL)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(new LiveDataCallAdapterFactory())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();
    }


    /**
     * Creates the client instance
     *
     * @return GatewayService
     */
    public static GatewayService getInstance() {
        return retrofitClient(okHttpClient(loggingInterceptor())).create(GatewayService.class);
    }

}
