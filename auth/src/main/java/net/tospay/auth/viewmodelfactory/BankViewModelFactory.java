package net.tospay.auth.viewmodelfactory;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import net.tospay.auth.remote.repository.BankRepository;
import net.tospay.auth.ui.account.bank.BankViewModel;

public class BankViewModelFactory extends ViewModelProvider.NewInstanceFactory {

    private final BankRepository repository;

    public BankViewModelFactory(BankRepository repository) {
        this.repository = repository;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(BankViewModel.class)) {
            //noinspection unchecked
            return (T) new BankViewModel(repository);
        }

        throw new IllegalArgumentException("Unknown ViewModel Class: " + modelClass.getName());
    }
}