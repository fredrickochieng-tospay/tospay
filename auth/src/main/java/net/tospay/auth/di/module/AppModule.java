package net.tospay.auth.di.module;

import android.content.Context;

import net.tospay.auth.AuthApp;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module(includes = {
        ViewModelModule.class,
        NetworkModule.class,
})
public class AppModule {

    @Provides
    @Singleton
    Context provideContext(AuthApp application) {
        return application.getApplicationContext();
    }
}
