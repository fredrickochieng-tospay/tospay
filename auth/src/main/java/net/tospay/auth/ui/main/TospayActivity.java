package net.tospay.auth.ui.main;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import net.tospay.auth.BR;
import net.tospay.auth.R;
import net.tospay.auth.api.response.PaymentResult;
import net.tospay.auth.api.response.TospayException;
import net.tospay.auth.databinding.ActivityTospayBinding;
import net.tospay.auth.interfaces.AccountType;
import net.tospay.auth.interfaces.PaymentListener;
import net.tospay.auth.model.Merchant;
import net.tospay.auth.model.PaymentTransaction;
import net.tospay.auth.ui.base.BaseActivity;

import static net.tospay.auth.utils.Constants.KEY_TOKEN;

@SuppressWarnings("ConstantConditions")
public class TospayActivity extends BaseActivity<ActivityTospayBinding, PaymentViewModel>
        implements PaymentListener {

    private PaymentViewModel mViewModel;
    private PaymentTransaction paymentTransaction;
    private Merchant merchant;
    private AccountType accountType;

    @Override
    public int getBindingVariable() {
        return BR.paymentViewModel;
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_tospay;
    }

    @Override
    public PaymentViewModel getViewModel() {
        mViewModel = ViewModelProviders.of(this).get(PaymentViewModel.class);
        return mViewModel;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityTospayBinding binding = getViewDataBinding();
        binding.setPaymentViewModel(mViewModel);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        String token = getIntent().getStringExtra(KEY_TOKEN);
        mViewModel.getPaymentTokenLiveData().setValue(token);

        Bundle args = new Bundle();
        args.putString(KEY_TOKEN, token);

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        navController.setGraph(R.navigation.nav_graph, args);

        mViewModel.getTransactionMutableLiveData().observe(this, paymentTransaction ->
                this.paymentTransaction = paymentTransaction
        );

        mViewModel.getMerchantMutableLiveData().observe(this, merchant ->
                this.merchant = merchant
        );

        mViewModel.getAccountTypeMutableLiveData().observe(this, accountType -> {
            this.accountType = accountType;
            Log.e("ACCOUNT", "onAccountSelected: " + accountType);
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.auth_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_close) {
            finishActivity(null);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void finishActivity(String message) {
        Intent returnIntent = new Intent();
        returnIntent.putExtra("result", message);
        setResult(Activity.RESULT_CANCELED, returnIntent);
        finish();
    }

    @Override
    public void onPaymentDetails(PaymentResult response) {
        mViewModel.getTransactionMutableLiveData()
                .setValue(response.getPaymentTransaction());

        mViewModel.getMerchantMutableLiveData()
                .setValue(response.getMerchant());
    }

    @Override
    public void onPaymentSuccess() {

    }

    @Override
    public void onPaymentFailed(TospayException exception) {
        finishActivity(exception.getErrorMessage());
    }

    @Override
    public void onAccountSelected(AccountType accountType) {
        mViewModel.getAccountTypeMutableLiveData().setValue(accountType);
    }
}
