package net.tospay.auth.ui.main;

import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import androidx.core.app.NotificationCompat;
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
import net.tospay.auth.remote.ServiceGenerator;
import net.tospay.auth.remote.Status;
import net.tospay.auth.remote.exception.TospayException;
import net.tospay.auth.remote.repository.UserRepository;
import net.tospay.auth.remote.service.UserService;
import net.tospay.auth.remote.util.AppExecutors;
import net.tospay.auth.ui.base.BaseActivity;
import net.tospay.auth.ui.dialog.TransferDialog;
import net.tospay.auth.viewmodelfactory.MainViewModelFactory;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.net.URISyntaxException;

import static net.tospay.auth.utils.Constants.KEY_TOKEN;

public class TospayActivity extends BaseActivity<ActivityTospayBinding, MainViewModel>
        implements PaymentListener {

    private static final String TAG = "TospayActivity";
    private Socket mSocket;

    private Emitter.Listener onNewMessage = args -> {
        String jsonStr = args[0].toString();
        Log.e(TAG, jsonStr);
        NotificationEvent event = new Gson().fromJson(jsonStr, NotificationEvent.class);
        EventBus.getDefault().postSticky(event);
    };

    private MainViewModel mViewModel;

    @Override
    public int getBindingVariable() {
        return BR.paymentViewModel;
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_tospay;
    }

    @Override
    public MainViewModel getViewModel() {
        UserRepository repository = new UserRepository(new AppExecutors(),
                ServiceGenerator.createService(UserService.class, this));

        MainViewModelFactory factory = new MainViewModelFactory(repository, getSharedPrefManager());
        mViewModel = ViewModelProviders.of(this, factory).get(MainViewModel.class);
        return mViewModel;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityTospayBinding binding = getViewDataBinding();
        binding.setPaymentViewModel(mViewModel);

        String paymentToken = getIntent().getStringExtra(KEY_TOKEN);
        Bundle args = new Bundle();
        args.putString(KEY_TOKEN, paymentToken);

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        navController.setGraph(R.navigation.nav_payment, args);

        if (!getSharedPrefManager().isTokenExpiredOrAlmost()) {
            connectSocket(getAccessToken());
        }

        if (getSharedPrefManager().getActiveUser() != null) {
            if (getSharedPrefManager().isTokenExpiredOrAlmost()) {
                reAuthenticateUser();
            }
        }
    }

    private void reAuthenticateUser() {
        mViewModel.login();
        mViewModel.getResponseLiveData().observe(this, resource -> {
            if (resource != null) {
                if (resource.status == Status.SUCCESS) {
                    if (resource.data != null) {
                        getSharedPrefManager().setActiveUser(resource.data);
                    }
                }
            }
        });
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
                    displayNotification(notification.getNotification().getTitle(),
                            notification.getNotification().getBody());
                    finishWithSuccess();
                }
            } else {
                TransferDialog.newInstance(notification).show(getSupportFragmentManager(), TransferDialog.TAG);
            }
        }
    }

    private void displayNotification(String title, String body) {
        String channelId = getString(R.string.default_notification_channel_id);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this, channelId)
                        .setSmallIcon(R.drawable.ic_notification)
                        .setContentTitle(title)
                        .setContentText(body)
                        .setAutoCancel(true)
                        .setSound(defaultSoundUri);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId, title, NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription(body);
            channel.enableLights(true);
            channel.setLightColor(getColor(R.color.colorPrimary));
            channel.enableVibration(true);
            channel.setShowBadge(true);
            notificationManager.createNotificationChannel(channel);
        }

        notificationManager.notify(0, notificationBuilder.build());
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
