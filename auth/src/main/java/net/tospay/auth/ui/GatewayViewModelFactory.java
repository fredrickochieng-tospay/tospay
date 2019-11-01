package net.tospay.auth.ui;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import net.tospay.auth.repository.GatewayRepository;
import net.tospay.auth.ui.account.AccountViewModel;
import net.tospay.auth.ui.summary.SummaryViewModel;

public class GatewayViewModelFactory extends ViewModelProvider.NewInstanceFactory {

    private final GatewayRepository gatewayRepository;

    public GatewayViewModelFactory(GatewayRepository gatewayRepository) {
        this.gatewayRepository = gatewayRepository;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(SummaryViewModel.class)) {
            //noinspection unchecked
            return (T) new SummaryViewModel(gatewayRepository);

        } else if (modelClass.isAssignableFrom(AccountViewModel.class)) {
            //noinspection unchecked
            return (T) new AccountViewModel(gatewayRepository);

        }

        throw new IllegalArgumentException("Unknown ViewModel Class: " + modelClass.getName());
    }
}
