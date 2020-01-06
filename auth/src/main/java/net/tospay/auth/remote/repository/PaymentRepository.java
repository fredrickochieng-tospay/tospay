package net.tospay.auth.remote.repository;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;

import net.tospay.auth.model.transfer.Transfer;
import net.tospay.auth.remote.Resource;
import net.tospay.auth.remote.response.ApiResponse;
import net.tospay.auth.remote.response.Result;
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
}
