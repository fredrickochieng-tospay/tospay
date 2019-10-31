package net.tospay.auth.di.builder;

import net.tospay.auth.ui.activity.TospayActivity;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;


@Module
public abstract class ActivityBuilderModule {

    @ContributesAndroidInjector(modules = FragmentBuilderModule.class)
    abstract TospayActivity contributeTospayAuthClient();

}
