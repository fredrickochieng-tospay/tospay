package net.tospay.auth.di.module;

import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import net.tospay.auth.di.ProjectViewModelFactory;
import net.tospay.auth.di.qualifires.ViewModelKey;
import net.tospay.auth.viewmodel.AccountViewModel;
import net.tospay.auth.viewmodel.PaymentViewModel;

import dagger.Binds;
import dagger.Module;
import dagger.multibindings.IntoMap;

@Module
public abstract class ViewModelModule {

    @Binds
    @IntoMap
    @ViewModelKey(PaymentViewModel.class)
    abstract ViewModel bindMainPaymentViewModel(PaymentViewModel paymentViewModel);

    @Binds
    @IntoMap
    @ViewModelKey(AccountViewModel.class)
    abstract ViewModel bindMainAccountViewModel(AccountViewModel accountViewModel);

    @Binds
    abstract ViewModelProvider.Factory bindViewModelFactory(ProjectViewModelFactory projectViewModelFactory);
}
