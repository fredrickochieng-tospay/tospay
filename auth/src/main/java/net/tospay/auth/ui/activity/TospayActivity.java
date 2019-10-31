package net.tospay.auth.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import net.tospay.auth.BR;
import net.tospay.auth.R;
import net.tospay.auth.api.response.PaymentValidationResponse;
import net.tospay.auth.api.response.TospayException;
import net.tospay.auth.databinding.ActivityTospayBinding;
import net.tospay.auth.interfaces.PaymentListener;
import net.tospay.auth.ui.base.BaseActivity;
import net.tospay.auth.viewmodel.PaymentViewModel;

import javax.inject.Inject;

import static net.tospay.auth.utils.Constants.KEY_TOKEN;

@SuppressWarnings("ConstantConditions")
public class TospayActivity extends BaseActivity<ActivityTospayBinding, PaymentViewModel>
        implements PaymentListener {

    @Inject
    public ViewModelProvider.Factory factory;

    private PaymentViewModel paymentViewModel;

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
        paymentViewModel = ViewModelProviders.of(this, factory).get(PaymentViewModel.class);
        return paymentViewModel;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActivityTospayBinding binding = getViewDataBinding();
        binding.setPaymentViewModel(paymentViewModel);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Bundle args = new Bundle();
        args.putString(KEY_TOKEN, getIntent().getStringExtra(KEY_TOKEN));

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        navController.setGraph(R.navigation.nav_graph, args);
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
    public void onPaymentDetails(PaymentValidationResponse response) {
        /*this.transaction = response.getTransaction();
        this.merchant = response.getMerchant();*/
    }

    @Override
    public void onPaymentSuccess() {

    }

    @Override
    public void onPaymentFailed(TospayException exception) {
        finishActivity(exception.getErrorMessage());
    }

    /*public String getToken() {
        return token;
    }

    public Payment getTransaction() {
        return transaction;
    }

    public Merchant getMerchant() {
        return merchant;
    }*/
}
