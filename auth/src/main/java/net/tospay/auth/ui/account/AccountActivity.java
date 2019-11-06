package net.tospay.auth.ui.account;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.View;

import net.tospay.auth.BR;
import net.tospay.auth.R;
import net.tospay.auth.databinding.ActivityAccountBinding;
import net.tospay.auth.interfaces.AccountType;
import net.tospay.auth.interfaces.PaymentListener;
import net.tospay.auth.model.Account;
import net.tospay.auth.model.Wallet;
import net.tospay.auth.ui.GatewayViewModelFactory;
import net.tospay.auth.ui.base.BaseActivity;

import static net.tospay.auth.utils.Constants.KEY_IS_FOR_RESULT;
import static net.tospay.auth.utils.Constants.KEY_SHOW_WALLET;
import static net.tospay.auth.utils.Constants.KEY_TOKEN;

public class AccountActivity extends BaseActivity<ActivityAccountBinding, AccountViewModel>
        implements PaymentListener {

    private AccountViewModel mViewModel;

    @Override
    public int getBindingVariable() {
        return BR.accountViewModel;
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_account;
    }

    @Override
    public AccountViewModel getViewModel() {
        GatewayViewModelFactory factory = new GatewayViewModelFactory(getGatewayRepository());
        mViewModel = ViewModelProviders.of(this, factory).get(AccountViewModel.class);
        return mViewModel;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityAccountBinding mBinding = getViewDataBinding();
        mBinding.setAccountViewModel(mViewModel);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        boolean isForResult = intent.getBooleanExtra(KEY_SHOW_WALLET, false);

        Bundle args = new Bundle();
        args.putBoolean(KEY_IS_FOR_RESULT, true);
        args.putBoolean(KEY_SHOW_WALLET, isForResult);

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        navController.setGraph(R.navigation.nav_account, args);
    }

    @Override
    public void onAccountSelected(AccountType accountType) {
        Log.e("TAG", "onAccountSelected: " + accountType);

        Intent returnIntent = new Intent();

        if (accountType instanceof Wallet) {
            Wallet wallet = (Wallet) accountType;
            returnIntent.putExtra("result", wallet);
        } else {
            Account account = (Account) accountType;
            returnIntent.putExtra("result", account);
        }

        returnIntent.putExtra("type", accountType.getType());
        setResult(Activity.RESULT_OK, returnIntent);
        finish();
    }
}
