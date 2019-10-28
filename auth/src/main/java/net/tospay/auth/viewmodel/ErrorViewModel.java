package net.tospay.auth.viewmodel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class ErrorViewModel extends ViewModel {

    private MutableLiveData<String> errorMessage = new MutableLiveData<>();

    public MutableLiveData<String> getErrorMessage() {
        return errorMessage;
    }
}
