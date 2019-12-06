package net.tospay.auth.ui.main;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;

import net.tospay.auth.BR;
import net.tospay.auth.R;
import net.tospay.auth.databinding.ActivityTospayBinding;
import net.tospay.auth.interfaces.AccountType;
import net.tospay.auth.interfaces.PaymentListener;
import net.tospay.auth.model.Merchant;
import net.tospay.auth.model.PaymentTransaction;
import net.tospay.auth.remote.ApiConstants;
import net.tospay.auth.remote.Resource;
import net.tospay.auth.remote.response.PaymentValidationResponse;
import net.tospay.auth.remote.response.TospayException;
import net.tospay.auth.ui.GatewayViewModelFactory;
import net.tospay.auth.ui.base.BaseActivity;
import net.tospay.auth.ui_listener.AppBarStateChangeListener;

import java.net.URISyntaxException;

import static net.tospay.auth.utils.Constants.KEY_TOKEN;

@SuppressWarnings("ConstantConditions")
public class TospayActivity extends BaseActivity<ActivityTospayBinding, PaymentViewModel>
        implements PaymentListener {

    private static final String TAG = "TospayActivity";
    private Socket mSocket;
    private AppBarLayout collapsingToolbarLayout;

    private MenuItem closeMenuItem;

    private Emitter.Listener onNewMessage = args -> {
        String jsonStr = args[0].toString();
        Log.e(TAG, ": " + jsonStr);
    };

    private PaymentViewModel mViewModel;
    private PaymentTransaction paymentTransaction;
    private Merchant merchant;
    private String paymentToken;

    private final Handler handler = new Handler();
    private Runnable runnable;
    private int count = 0;
    private ProgressDialog progressDialog;
    private Animation animation;

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
        GatewayViewModelFactory factory = new GatewayViewModelFactory(getGatewayRepository());
        mViewModel = ViewModelProviders.of(this, factory).get(PaymentViewModel.class);
        return mViewModel;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityTospayBinding binding = getViewDataBinding();
        binding.setPaymentViewModel(mViewModel);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        animation = AnimationUtils.loadAnimation(this,R.anim.view_fade_in);


        collapsingToolbarLayout = findViewById(R.id.app_bar);
        collapsingToolbarLayout.addOnOffsetChangedListener(new AppBarStateChangeListener() {
            @Override
            public void onStateChanged(AppBarLayout appBarLayout, State state) {
                Log.d("STATE", state.name());
                Log.d(TAG, "onStateChanged: "+appBarLayout.getScaleY());

                if (state.name().equalsIgnoreCase("COLLAPSED")) {
                    toolbar.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                    closeMenuItem.setVisible(true);

                } else if (state.name().equalsIgnoreCase("EXPANDED")) {
                    toolbar.setBackgroundColor(getResources().getColor(R.color.transparent));
                    getSupportActionBar().setDisplayHomeAsUpEnabled(false);

                } else if (state.name().equalsIgnoreCase("IDLE")) {
                    toolbar.setBackgroundColor(getResources().getColor(R.color.transparent));
                    getSupportActionBar().setDisplayHomeAsUpEnabled(false);
                }
            }
        });

        paymentToken = getIntent().getStringExtra(KEY_TOKEN);
        mViewModel.getPaymentTokenLiveData().setValue(paymentToken);

        Bundle args = new Bundle();
        args.putString(KEY_TOKEN, paymentToken);

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        navController.setGraph(R.navigation.nav_payment, args);

        mViewModel.getTransactionMutableLiveData().observe(this, paymentTransaction ->
                this.paymentTransaction = paymentTransaction
        );

        mViewModel.getMerchantMutableLiveData().observe(this, merchant ->
                this.merchant = merchant
        );

        progressDialog = new ProgressDialog(this);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Processing payment. Please wait...");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setOnCancelListener(dialogInterface -> {
            dialogInterface.dismiss();
            finishWithError("Transaction canceled");
        });

        try {
            IO.Options mOptions = new IO.Options();
            if (getAccessToken() != null) {
                mOptions.query = "token=" + getAccessToken();
                mSocket = IO.socket(ApiConstants.NOTIFICATION_URL, mOptions);
                mSocket.on("notify", onNewMessage);
                mSocket.connect();
            }
        } catch (URISyntaxException e) {
            Log.e(TAG, "sockets: ", e);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.auth_menu, menu);
        super.onCreateOptionsMenu(menu);
        closeMenuItem = menu.findItem(R.id.action_close);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (item.getItemId() == R.id.action_close) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Cancel Transaction");
            builder.setMessage("Are you sure you want to cancel this transaction?");
            builder.setPositiveButton("Close", (dialogInterface, i) -> dialogInterface.dismiss());
            builder.setNegativeButton("Continue", (dialogInterface, i) -> finishWithError(null));
            builder.show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void finishWithError(String message) {
        Intent returnIntent = new Intent();
        returnIntent.putExtra("result", message);
        setResult(Activity.RESULT_CANCELED, returnIntent);
        finish();
    }

    private void finishWithSuccess(PaymentTransaction transaction) {
        Intent returnIntent = new Intent();
        returnIntent.putExtra("result", transaction);
        setResult(Activity.RESULT_OK, returnIntent);
        finish();
    }

    @Override
    public void onPaymentDetails(PaymentValidationResponse response) {
        mViewModel.getTransactionMutableLiveData()
                .setValue(response.getPaymentTransaction());

        mViewModel.getMerchantMutableLiveData()
                .setValue(response.getMerchant());
    }

    @Override
    public void onPaymentSuccess() {
        progressDialog.show();
        checkPaymentStatus();
    }

    private void checkPaymentStatus() {
        runnable = () -> {
            try {
                if (count <= 10) {
                    mViewModel.checkTransactionStatus(paymentToken);
                    mViewModel.getResponseLiveData().observe(TospayActivity.this, this::handleResponse);
                } else {
                    if (progressDialog != null) {
                        progressDialog.cancel();
                    }

                    finishWithError("Transaction timed out");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        };

        handler.post(runnable);
    }

    private void handleResponse(Resource<PaymentValidationResponse> resource) {
        if (resource != null) {
            switch (resource.status) {
                case SUCCESS:
                    if (resource.data != null) {
                        paymentTransaction = resource.data.getPaymentTransaction();
                        switch (paymentTransaction.getStatus()) {
                            case "PROCESSING":
                            case "CREATED":
                                handler.postDelayed(runnable, 5000);
                                count++;
                                break;

                            case "SUCCESS":
                                if (progressDialog != null) {
                                    progressDialog.cancel();
                                }
                                finishWithSuccess(resource.data.getPaymentTransaction());
                                break;

                            case "FAILED":
                                if (progressDialog != null) {
                                    progressDialog.cancel();
                                }
                                handler.removeCallbacks(runnable);
                                finishWithError(paymentTransaction.getReason());
                                break;
                        }
                    }
                    break;

                case ERROR:
                    finishWithError(resource.message);
                    break;
            }
        }
    }

    @Override
    public void onPaymentFailed(TospayException exception) {
        finishWithError(exception.getErrorMessage());
    }

    @Override
    public void onAccountSelected(AccountType accountType) {
        mViewModel.getAccountTypeMutableLiveData().setValue(accountType);
    }

    public PaymentTransaction getPaymentTransaction() {
        return paymentTransaction;
    }

    public Merchant getMerchant() {
        return merchant;
    }

    public String getPaymentToken() {
        return paymentToken;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mSocket != null) {
            mSocket.disconnect();
            mSocket.off("notify", onNewMessage);
        }
    }
}
