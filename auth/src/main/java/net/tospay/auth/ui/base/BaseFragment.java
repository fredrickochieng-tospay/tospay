package net.tospay.auth.ui.base;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.fragment.app.Fragment;

import net.tospay.auth.api.GatewayApiClient;
import net.tospay.auth.interfaces.PaymentListener;
import net.tospay.auth.remote.util.AppExecutors;
import net.tospay.auth.repository.GatewayRepository;
import net.tospay.auth.utils.SharedPrefManager;


public abstract class BaseFragment<DB extends ViewDataBinding, VM extends BaseViewModel>
        extends Fragment {

    private BaseActivity mActivity;
    private DB mViewDataBinding;
    private VM mViewModel;

    private GatewayRepository mGatewayRepository;
    private SharedPrefManager mSharedPrefManager;
    public PaymentListener mListener;

    /**
     * Override for set binding variable
     *
     * @return variable id
     */
    public abstract int getBindingVariable();

    /**
     * @return layout resource id
     */
    @LayoutRes
    public abstract int getLayoutId();

    /**
     * Override for set view model
     *
     * @return view model instance
     */
    public abstract VM getViewModel();

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof BaseActivity) {
            this.mActivity = (BaseActivity) context;
        }

        if (context instanceof PaymentListener) {
            mListener = (PaymentListener) context;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppExecutors mAppExecutors = new AppExecutors();
        mGatewayRepository = new GatewayRepository(mAppExecutors, GatewayApiClient.getInstance());
        mSharedPrefManager = SharedPrefManager.getInstance(getContext());
        mViewModel = getViewModel();
        setBearerToken(mSharedPrefManager.getAccessToken());
    }

    public void setBearerToken(String token) {
        String bearerToken = "Bearer " + token;
        mViewModel.setBearerToken(bearerToken);
    }

    public String getBearerToken() {
        return mSharedPrefManager.getAccessToken();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mViewDataBinding = DataBindingUtil.inflate(inflater, getLayoutId(), container, false);
        return mViewDataBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mViewDataBinding.setVariable(getBindingVariable(), mViewModel);
        mViewDataBinding.setLifecycleOwner(this);
        mViewDataBinding.executePendingBindings();
    }

    public BaseActivity getBaseActivity() {
        return mActivity;
    }

    public DB getViewDataBinding() {
        return mViewDataBinding;
    }

    public GatewayRepository getGatewayRepository() {
        return mGatewayRepository;
    }

    public SharedPrefManager getSharedPrefManager() {
        return mSharedPrefManager;
    }

    public void openActivityOnTokenExpire() {
        if (mActivity != null) {
            mActivity.openActivityOnTokenExpire();
        }
    }
}
