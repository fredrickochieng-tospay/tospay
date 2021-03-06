package net.tospay.auth.ui;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import net.tospay.auth.remote.repository.GatewayRepository;
import net.tospay.auth.ui.account.AccountViewModel;
import net.tospay.auth.ui.account.verify.MobileMoneyViewModel;
import net.tospay.auth.ui.confirm.ConfirmViewModel;
import net.tospay.auth.ui.dialog.country.CountryViewModel;
import net.tospay.auth.ui.dialog.network.NetworkViewModel;
import net.tospay.auth.ui.main.PaymentViewModel;
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

        } else if (modelClass.isAssignableFrom(ConfirmViewModel.class)) {
            //noinspection unchecked
            return (T) new ConfirmViewModel(gatewayRepository);

        } else if (modelClass.isAssignableFrom(PaymentViewModel.class)) {
            //noinspection unchecked
            return (T) new PaymentViewModel(gatewayRepository);

        } else if (modelClass.isAssignableFrom(MobileMoneyViewModel.class)) {
            //noinspection unchecked
            return (T) new MobileMoneyViewModel(gatewayRepository);

        } else if (modelClass.isAssignableFrom(CountryViewModel.class)) {
            //noinspection unchecked
            return (T) new CountryViewModel(gatewayRepository);

        } else if (modelClass.isAssignableFrom(NetworkViewModel.class)) {
            //noinspection unchecked
            return (T) new NetworkViewModel(gatewayRepository);
        }

        throw new IllegalArgumentException("Unknown ViewModel Class: " + modelClass.getName());
    }
}
