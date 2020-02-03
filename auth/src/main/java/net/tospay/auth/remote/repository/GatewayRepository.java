package net.tospay.auth.remote.repository;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;

import net.tospay.auth.model.Country;
import net.tospay.auth.model.Network;
import net.tospay.auth.remote.Resource;
import net.tospay.auth.remote.response.ApiResponse;
import net.tospay.auth.remote.response.Result;
import net.tospay.auth.remote.service.GatewayService;
import net.tospay.auth.remote.util.AppExecutors;
import net.tospay.auth.remote.util.NetworkBoundResource;
import net.tospay.auth.utils.AbsentLiveData;

import java.util.List;

/**
 * <p>Handles all the calls to Tospay Gateway Service</p>
 */
public class GatewayRepository {

    private final GatewayService mGatewayService;
    private final AppExecutors mAppExecutors;

    public enum CountryType {
        DEFAULT,
        BANK,
        MOBILE
    }

    /**
     * @param mAppExecutors   -AppExecutor
     * @param mGatewayService - Gateway service
     */
    public GatewayRepository(AppExecutors mAppExecutors, GatewayService mGatewayService) {
        this.mGatewayService = mGatewayService;
        this.mAppExecutors = mAppExecutors;
    }

    public LiveData<Resource<List<Country>>> countries(String bearerToken, CountryType type) {
        return new NetworkBoundResource<List<Country>, Result<List<Country>>>(mAppExecutors) {

            private List<Country> resultsDb;

            @Override
            protected void saveCallResult(@NonNull Result<List<Country>> item) {
                resultsDb = item.getData();
            }

            @Override
            protected boolean shouldFetch(@Nullable List<Country> data) {
                return true;
            }

            @NonNull
            @Override
            protected LiveData<List<Country>> loadFromDb() {
                if (resultsDb == null) {
                    return AbsentLiveData.create();
                } else {
                    return new LiveData<List<Country>>() {
                        @Override
                        protected void onActive() {
                            super.onActive();
                            setValue(resultsDb);
                        }
                    };
                }
            }

            @NonNull
            @Override
            protected LiveData<ApiResponse<Result<List<Country>>>> createCall() {
                switch (type) {
                    case MOBILE:
                        return mGatewayService.mobileCountries(bearerToken);
                    case BANK:
                        return mGatewayService.bankCountries(bearerToken);
                    default:
                        return mGatewayService.countries();
                }
            }
        }.asLiveData();
    }

    public LiveData<Resource<List<Network>>> networks(String bearerToken, String iso) {
        return new NetworkBoundResource<List<Network>, Result<List<Network>>>(mAppExecutors) {

            private List<Network> resultsDb;

            @Override
            protected void saveCallResult(@NonNull Result<List<Network>> item) {
                resultsDb = item.getData();
            }

            @Override
            protected boolean shouldFetch(@Nullable List<Network> data) {
                return true;
            }

            @NonNull
            @Override
            protected LiveData<List<Network>> loadFromDb() {
                if (resultsDb == null) {
                    return AbsentLiveData.create();
                } else {
                    return new LiveData<List<Network>>() {
                        @Override
                        protected void onActive() {
                            super.onActive();
                            setValue(resultsDb);
                        }
                    };
                }
            }

            @NonNull
            @Override
            protected LiveData<ApiResponse<Result<List<Network>>>> createCall() {
                return mGatewayService.networks(bearerToken, iso);
            }
        }.asLiveData();
    }
}
