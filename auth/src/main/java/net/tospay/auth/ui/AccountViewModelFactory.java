package net.tospay.auth.ui;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import net.tospay.auth.remote.repository.AccountRepository;
import net.tospay.auth.ui.account.AccountViewModel;

public class AccountViewModelFactory extends ViewModelProvider.NewInstanceFactory {

    private final AccountRepository repository;

    public AccountViewModelFactory(AccountRepository repository) {
        this.repository = repository;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(AccountViewModel.class)) {
            //noinspection unchecked
            return (T) new AccountViewModel(repository);

        }

        throw new IllegalArgumentException("Unknown ViewModel Class: " + modelClass.getName());
    }
}
