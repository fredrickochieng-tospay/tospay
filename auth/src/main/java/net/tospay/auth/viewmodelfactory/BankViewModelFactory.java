package net.tospay.auth.viewmodelfactory;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import net.tospay.auth.remote.repository.BankRepository;
import net.tospay.auth.remote.repository.GatewayRepository;
import net.tospay.auth.ui.account.bank.BankViewModel;

public class BankViewModelFactory extends ViewModelProvider.NewInstanceFactory {

    private final BankRepository bankRepository;
    private final GatewayRepository gatewayRepository;

    public BankViewModelFactory(BankRepository bankRepository, GatewayRepository gatewayRepository) {
        this.bankRepository = bankRepository;
        this.gatewayRepository = gatewayRepository;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(BankViewModel.class)) {
            //noinspection unchecked
            return (T) new BankViewModel(bankRepository, gatewayRepository);
        }

        throw new IllegalArgumentException("Unknown ViewModel Class: " + modelClass.getName());
    }
}