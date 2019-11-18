package net.tospay.auth.ui.dialog.country;

import androidx.lifecycle.LiveData;

import net.tospay.auth.model.Country;
import net.tospay.auth.remote.Resource;
import net.tospay.auth.remote.repository.GatewayRepository;
import net.tospay.auth.ui.base.BaseViewModel;

import java.util.List;

public class CountryViewModel extends BaseViewModel {

    private GatewayRepository gatewayRepository;
    private LiveData<Resource<List<Country>>> resourceLiveData;

    public CountryViewModel(GatewayRepository gatewayRepository) {
        this.gatewayRepository = gatewayRepository;
    }

    public LiveData<Resource<List<Country>>> getResourceLiveData() {
        return resourceLiveData;
    }

    public void countries(boolean isOperators) {
        setIsLoading(true);
        resourceLiveData = gatewayRepository.countries((String) getBearerToken().get(), isOperators);
    }
}
