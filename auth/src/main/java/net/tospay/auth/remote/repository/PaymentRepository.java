package net.tospay.auth.remote.repository;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;

import net.tospay.auth.model.transfer.Amount;
import net.tospay.auth.model.transfer.Transfer;
import net.tospay.auth.remote.Resource;
import net.tospay.auth.remote.response.ApiResponse;
import net.tospay.auth.remote.response.Result;
import net.tospay.auth.remote.response.TransferResponse;
import net.tospay.auth.remote.service.PaymentService;
import net.tospay.auth.remote.util.AppExecutors;
import net.tospay.auth.remote.util.NetworkBoundResource;
import net.tospay.auth.utils.AbsentLiveData;

public class PaymentRepository {

    private final PaymentService mPaymentService;
    private final AppExecutors mAppExecutors;

    /**
     * @param mAppExecutors   -AppExecutor
     * @param mPaymentService - PaymentService service
     */
    public PaymentRepository(AppExecutors mAppExecutors, PaymentService mPaymentService) {
        this.mPaymentService = mPaymentService;
        this.mAppExecutors = mAppExecutors;
    }

    /**
     * Fetch payment details
     *
     * @param paymentId - generated payment id
     * @return livedata Transfer object
     */
    public LiveData<Resource<Transfer>> details(String paymentId) {
        return new NetworkBoundResource<Transfer, Result<Transfer>>(mAppExecutors) {

            private Transfer resultsDb;

            @Override
            protected void saveCallResult(@NonNull Result<Transfer> item) {
                resultsDb = item.getData();
            }

            @Override
            protected boolean shouldFetch(@Nullable Transfer data) {
                return true;
            }

            @NonNull
            @Override
            protected LiveData<Transfer> loadFromDb() {
                if (resultsDb == null) {
                    return AbsentLiveData.create();
                } else {
                    return new LiveData<Transfer>() {
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
            protected LiveData<ApiResponse<Result<Transfer>>> createCall() {
                return mPaymentService.details(paymentId);

            }
        }.asLiveData();
    }

    /**
     * Execute payment
     *
     * @param bearerToken - bearer token
     * @param paymentId   - payment reference id
     * @param transfer    - Transfer payload
     * @return payment reference no.
     */
    public LiveData<Resource<TransferResponse>> pay(String bearerToken, String paymentId, Transfer transfer) {
        return new NetworkBoundResource<TransferResponse, Result<TransferResponse>>(mAppExecutors) {

            private TransferResponse resultsDb;

            @Override
            protected void saveCallResult(@NonNull Result<TransferResponse> item) {
                resultsDb = item.getData();
            }

            @Override
            protected boolean shouldFetch(@Nullable TransferResponse data) {
                return true;
            }

            @NonNull
            @Override
            protected LiveData<TransferResponse> loadFromDb() {
                if (resultsDb == null) {
                    return AbsentLiveData.create();
                } else {
                    return new LiveData<TransferResponse>() {
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
            protected LiveData<ApiResponse<Result<TransferResponse>>> createCall() {
                return mPaymentService.pay(bearerToken, paymentId, transfer);
            }
        }.asLiveData();
    }

    public LiveData<Resource<Amount>> chargeLookup(String bearerToken, Transfer transfer, String type) {
        return new NetworkBoundResource<Amount, Result<Amount>>(mAppExecutors) {

            private Amount resultsDb;

            @Override
            protected void saveCallResult(@NonNull Result<Amount> item) {
                resultsDb = item.getData();
            }

            @Override
            protected boolean shouldFetch(@Nullable Amount data) {
                return true;
            }

            @NonNull
            @Override
            protected LiveData<Amount> loadFromDb() {
                if (resultsDb == null) {
                    return AbsentLiveData.create();
                } else {
                    return new LiveData<Amount>() {
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
            protected LiveData<ApiResponse<Result<Amount>>> createCall() {
                if (type.equals(Transfer.PAYMENT)) {
                    return mPaymentService.paymentChargeLookup(bearerToken, transfer);
                } else {
                    return mPaymentService.transferChargeLookup(bearerToken, transfer);
                }
            }
        }.asLiveData();
    }

    public LiveData<Resource<TransferResponse>> transfer(String bearerToken, Transfer transfer) {
        return new NetworkBoundResource<TransferResponse, Result<TransferResponse>>(mAppExecutors) {

            private TransferResponse resultsDb;

            @Override
            protected void saveCallResult(@NonNull Result<TransferResponse> item) {
                resultsDb = item.getData();
            }

            @Override
            protected boolean shouldFetch(@Nullable TransferResponse data) {
                return true;
            }

            @NonNull
            @Override
            protected LiveData<TransferResponse> loadFromDb() {
                if (resultsDb == null) {
                    return AbsentLiveData.create();
                } else {
                    return new LiveData<TransferResponse>() {
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
            protected LiveData<ApiResponse<Result<TransferResponse>>> createCall() {
                return mPaymentService.transfer(bearerToken, transfer);
            }
        }.asLiveData();
    }
}
