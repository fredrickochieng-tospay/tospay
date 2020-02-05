package net.tospay.auth.ui.auth.pin;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;

import net.tospay.auth.R;
import net.tospay.auth.remote.ServiceGenerator;
import net.tospay.auth.remote.repository.UserRepository;
import net.tospay.auth.remote.service.UserService;
import net.tospay.auth.remote.util.AppExecutors;
import net.tospay.auth.ui.auth.AuthActivity;
import net.tospay.auth.ui.auth.login.LoginViewModel;
import net.tospay.auth.utils.Constants;
import net.tospay.auth.utils.SharedPrefManager;
import net.tospay.auth.view.LoadingLayout;
import net.tospay.auth.viewmodelfactory.UserViewModelFactory;

public class PinActivity extends AppCompatActivity {

    public static final int REQUEST_PIN = 200;
    private SharedPrefManager sharedPrefManager;
    private LoginViewModel mViewModel;
    private LoadingLayout loadingLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pin);
        loadingLayout = findViewById(R.id.loadingLayout);

        UserRepository repository = new UserRepository(new AppExecutors(),
                ServiceGenerator.createService(UserService.class, this));
        UserViewModelFactory factory = new UserViewModelFactory(repository);
        mViewModel = ViewModelProviders.of(this, factory).get(LoginViewModel.class);

        sharedPrefManager = SharedPrefManager.getInstance(this);
        showLockScreenFragment();
    }

    private final PFLockScreenFragment.OnPFLockScreenCodeCreateListener mCodeCreateListener =
            new PFLockScreenFragment.OnPFLockScreenCodeCreateListener() {

                @Override
                public void onCodeCreated(String encodedCode) {
                    sharedPrefManager.save(Constants.KEY_PIN, encodedCode);
                    sharedPrefManager.save(Constants.KEY_PIN_SET, true);

                    AlertDialog.Builder builder = new AlertDialog.Builder(PinActivity.this);
                    builder.setTitle("Pin setup success");
                    builder.setMessage("Congratulations! You have successfully setup pin.");
                    builder.setPositiveButton("Continue", (dialogInterface, i) -> {
                        dialogInterface.dismiss();
                        finishWithSuccess();
                    });

                    builder.show();
                }

                @Override
                public void onNewCodeValidationFailed() {
                    Toast.makeText(PinActivity.this, "Validation failed", Toast.LENGTH_SHORT).show();
                }
            };

    private final PFLockScreenFragment.OnPFLockScreenLoginListener mLoginListener =
            new PFLockScreenFragment.OnPFLockScreenLoginListener() {

                @Override
                public void onCodeInputSuccessful() {
                    loadingLayout.setVisibility(View.VISIBLE);
                    reAuthenticateUser();
                }

                @Override
                public void onFingerprintSuccessful() {
                    loadingLayout.setVisibility(View.VISIBLE);
                    reAuthenticateUser();
                }

                @Override
                public void onPinLoginFailed() {

                }

                @Override
                public void onFingerprintLoginFailed() {
                }
            };

    private void reAuthenticateUser() {
        String email = sharedPrefManager.read(SharedPrefManager.KEY_EMAIL, null);
        String password = sharedPrefManager.read(SharedPrefManager.KEY_PASSWORD, null);
        mViewModel.login(email, password);
        mViewModel.getResponseLiveData().observe(this, resource -> {
            if (resource != null) {
                switch (resource.status) {
                    case SUCCESS:
                        loadingLayout.setVisibility(View.GONE);
                        sharedPrefManager.setActiveUser(resource.data);
                        finishWithSuccess();
                        break;

                    case ERROR:
                        loadingLayout.setVisibility(View.GONE);
                        finishWithError();
                        break;
                }
            }
        });
    }

    private void showLockScreenFragment() {
        new PFPinCodeViewModel().isPinCodeEncryptionKeyExist().observe(
                this, result -> {
                    if (result == null) {
                        return;
                    }
                    if (result.getError() != null) {
                        Toast.makeText(PinActivity.this, "Can not get pin code info", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    showLockScreenFragment(result.getResult());
                }
        );
    }

    private void showLockScreenFragment(boolean isPinExist) {
        final PFFLockScreenConfiguration.Builder builder = new PFFLockScreenConfiguration.Builder(this)
                .setTitle(isPinExist ? "Unlock with your pin code or fingerprint" : "Setup Pin Authentication")
                .setCodeLength(4)
                .setLeftButton("Can't remember")
                .setUseFingerprint(true);

        final PFLockScreenFragment fragment = new PFLockScreenFragment();

        fragment.setOnLeftButtonClickListener(view ->
                startActivityForResult(new Intent(PinActivity.this, AuthActivity.class),
                        AuthActivity.REQUEST_CODE_LOGIN));

        builder.setMode(isPinExist
                ? PFFLockScreenConfiguration.MODE_AUTH
                : PFFLockScreenConfiguration.MODE_CREATE);
        if (isPinExist) {
            fragment.setEncodedPinCode(sharedPrefManager.read(Constants.KEY_PIN, null));
            fragment.setLoginListener(mLoginListener);
        }

        fragment.setConfiguration(builder.build());
        fragment.setCodeCreateListener(mCodeCreateListener);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container_view, fragment).commit();

    }

    public void finishWithSuccess() {
        Intent returnIntent = new Intent();
        setResult(Activity.RESULT_OK, returnIntent);
        finish();
    }

    public void finishWithError() {
        Intent returnIntent = new Intent();
        setResult(Activity.RESULT_CANCELED, returnIntent);
        finish();
    }
}
