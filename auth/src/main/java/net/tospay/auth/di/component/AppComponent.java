package net.tospay.auth.di.component;

import net.tospay.auth.AuthApp;
import net.tospay.auth.di.module.AppModule;
import net.tospay.auth.di.builder.ActivityBuilderModule;

import javax.inject.Singleton;

import dagger.Component;
import dagger.android.AndroidInjectionModule;
import dagger.android.AndroidInjector;

@Singleton
@Component(modules = {
        AndroidInjectionModule.class,
        AppModule.class,
        ActivityBuilderModule.class
})
public interface AppComponent extends AndroidInjector<AuthApp> {

    @Component.Builder
    abstract class Builder extends AndroidInjector.Builder<AuthApp> {

    }
}
