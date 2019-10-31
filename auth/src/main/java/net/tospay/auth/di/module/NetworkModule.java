package net.tospay.auth.di.module;

import net.tospay.auth.BuildConfig;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import okhttp3.logging.HttpLoggingInterceptor;

/**
 * This class is responsible for setting up Retrofit and anything related to network calls.
 */

@Module
public class NetworkModule {


    @Provides
    @Singleton
    HttpLoggingInterceptor provideOkHttpInterceptors() {
        return new HttpLoggingInterceptor().
                setLevel(BuildConfig.DEBUG ? HttpLoggingInterceptor.Level.BODY : HttpLoggingInterceptor.Level.NONE);
    }

    /**
     * Configure OkHttpClient. This helps us override some of the default configuration. Like the
     * connection timeout.
     *
     * @return OkHttpClient
     */
    /*@Provides
    @Singleton
    OkHttpClient okHttpClient(HttpLoggingInterceptor httpLoggingInterceptor) {
        return new OkHttpClient.Builder()
                .addInterceptor(httpLoggingInterceptor)
                .connectTimeout(CONNECT_TIMEOUT, TimeUnit.SECONDS)
                .writeTimeout(WRITE_TIMEOUT, TimeUnit.SECONDS)
                .readTimeout(READ_TIMEOUT, TimeUnit.SECONDS)
                .addInterceptor(new RequestInterceptor())
                .build();
    }*/


    /*@Provides
    @Singleton
    Retrofit provideRetrofitClient(@NonNull OkHttpClient okHttpClient) {
        return new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create()) // Serialize Objects
                .addCallAdapterFactory(new LiveDataCallAdapterFactory())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create()) //Set call to return {@link Observable}
                .build();
    }

    @Provides
    @Singleton
    BookService provideBookService(Retrofit retrofit) {
        return retrofit.create(BookService.class);
    }*/
}
