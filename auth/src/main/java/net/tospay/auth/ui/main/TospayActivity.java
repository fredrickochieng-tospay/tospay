package net.tospay.auth.ui.main;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;
import com.google.gson.Gson;

import net.tospay.auth.BR;
import net.tospay.auth.R;
import net.tospay.auth.databinding.ActivityTospayBinding;
import net.tospay.auth.event.NotificationEvent;
import net.tospay.auth.interfaces.PaymentListener;
import net.tospay.auth.model.TospayUser;
import net.tospay.auth.remote.ApiConstants;
import net.tospay.auth.remote.response.TospayException;
import net.tospay.auth.ui.base.BaseActivity;
import net.tospay.auth.ui.dialog.TransferDialog;
import net.tospay.auth.viewmodelfactory.GatewayViewModelFactory;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.net.URISyntaxException;

import static net.tospay.auth.utils.Constants.KEY_TOKEN;

public class TospayActivity extends BaseActivity<ActivityTospayBinding, PaymentViewModel>
        implements PaymentListener {

    private static final String TAG = "TospayActivity";
    private Socket mSocket;

    private Emitter.Listener onNewMessage = args -> {
        String jsonStr = args[0].toString();
        Log.e(TAG, jsonStr);
        NotificationEvent event = new Gson().fromJson(jsonStr, NotificationEvent.class);
        EventBus.getDefault().postSticky(event);
    };

    private PaymentViewModel mViewModel;

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

        String paymentToken = getIntent().getStringExtra(KEY_TOKEN);
        mViewModel.getPaymentTokenLiveData().setValue(paymentToken);

        Bundle args = new Bundle();
        args.putString(KEY_TOKEN, paymentToken);

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        navController.setGraph(R.navigation.nav_payment, args);

        if (!getSharedPrefManager().isTokenExpiredOrAlmost()) {
            connectSocket(getAccessToken());
        }
    }

    private void connectSocket(String bearerToken) {
        try {
            IO.Options mOptions = new IO.Options();
            mOptions.forceNew = true;
            mOptions.reconnection = false;
            mOptions.query = "token=" + bearerToken;
            mSocket = IO.socket(ApiConstants.NOTIFICATION_URL, mOptions);
            mSocket.on(ApiConstants.NOTIFICATION_CHANNEL, onNewMessage);
            mSocket.connect();
        } catch (URISyntaxException e) {
            Log.e(TAG, "sockets error: ", e);
        }
    }

    private void finishWithError(String message) {
        if (mSocket != null) {
            mSocket.disconnect();
        }

        Intent returnIntent = new Intent();
        returnIntent.putExtra("result", message);
        setResult(Activity.RESULT_CANCELED, returnIntent);
        finish();
    }

    private void finishWithSuccess() {
        if (mSocket != null) {
            mSocket.disconnect();
        }

        Intent returnIntent = new Intent();
        //returnIntent.putExtra("result", transaction);
        setResult(Activity.RESULT_OK, returnIntent);
        finish();
    }

    @Override
    public void onPaymentSuccess() {
        finishWithSuccess();
    }

    @Override
    public void onPaymentFailed(TospayException exception) {
        finishWithError(exception.getErrorMessage());
    }

    @Override
    public void onLoginSuccess(TospayUser user) {
        reloadBearerToken();
        if (mSocket != null) {
            mSocket.disconnect();
            connectSocket(user.getToken());
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mSocket != null) {
            mSocket.disconnect();
            mSocket.off("notify", onNewMessage);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onNotification(NotificationEvent notification) {
        if (notification != null) {
            if (notification.getData().getTopic().equals("PAYMENT")) {
                if (notification.getData().getStatus().equals("FAILED")) {
                    finishWithError(notification.getData().getMessage());
                } else {
                    finishWithSuccess();
                }
            } else {
                TransferDialog.newInstance(notification).show(getSupportFragmentManager(), TransferDialog.TAG);
            }
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }
}
