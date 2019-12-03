package net.tospay.auth.remote;

import net.tospay.auth.remote.util.LiveDataCallAdapterFactory;
import net.tospay.auth.remote.interceptor.RequestInterceptor;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

import static net.tospay.auth.remote.ApiConstants.CONNECT_TIMEOUT;
import static net.tospay.auth.remote.ApiConstants.READ_TIMEOUT;
import static net.tospay.auth.remote.ApiConstants.WRITE_TIMEOUT;

public class ServiceGenerator {

    /**
     * Logs network requests
     *
     * @return HttpLoggingInterceptor
     */
    private static HttpLoggingInterceptor loggingInterceptor() {
        /*return new HttpLoggingInterceptor().
                setLevel(BuildConfig.DEBUG ? HttpLoggingInterceptor.Level.BODY
                        : HttpLoggingInterceptor.Level.NONE);*/

        return new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY);
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
     * @return Retrofit
     */
    private static Retrofit retrofitClient() {
        return new Retrofit.Builder()
                .baseUrl(ApiConstants.BASE_URL)
                .client(okHttpClient(loggingInterceptor()))
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(new LiveDataCallAdapterFactory())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();
    }

    /**
     * Creates the client instance
     *
     * @param serviceClass - interface class
     * @param <S>          -interface class
     * @return S
     */
    public static <S> S createService(Class<S> serviceClass) {
        return retrofitClient().create(serviceClass);
    }
}
