package net.tospay.auth.ui.base;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.LayoutRes;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;

import net.tospay.auth.ui.main.TospayActivity;


public abstract class BaseActivity<DB extends ViewDataBinding,
        VM extends BaseViewModel> extends AppCompatActivity {

    private VM mViewModel;
    private DB mDataBinding;

    public static int LOGIN_RESULT_CODE = 100;

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

    public DB getViewDataBinding() {
        return mDataBinding;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        performDataBinding();
    }

    /**
     * Performs data binding
     */
    private void performDataBinding() {
        mDataBinding = DataBindingUtil.setContentView(this, getLayoutId());
        mViewModel = mViewModel == null ? getViewModel() : mViewModel;
        mDataBinding.setVariable(getBindingVariable(), mViewModel);
        mDataBinding.setLifecycleOwner(this);
        mDataBinding.executePendingBindings();
    }

    @Override
    protected void onDestroy() {
        mDataBinding.unbind();
        super.onDestroy();
    }

    /**
     * Starts MainActivity if Token has Expired
     */
    public void openActivityOnTokenExpire() {
        //todo change activity
        //startActivityForResult(new Intent(this, TospayActivity.class), LOGIN_RESULT_CODE);
    }
}
