package net.tospay.lib;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import net.tospay.auth.Tospay;
import net.tospay.auth.biometric.BiometricCallback;
import net.tospay.auth.ui.auth.AuthActivity;

public class MainActivity extends AppCompatActivity implements BiometricCallback {

    //BiometricManager mBiometricManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /*mBiometricManager = new BiometricManager.BiometricBuilder(MainActivity.this)
                .setTitle(getString(R.string.biometric_title))
                .setSubtitle(getString(R.string.biometric_subtitle))
                .setDescription(getString(R.string.biometric_description))
                .setNegativeButtonText(getString(R.string.biometric_negative_button_text))
                .build();*/

        //start authentication
        //mBiometricManager.authenticate(MainActivity.this);

        /*String token = "543001b407b1f8aee898b32fb5e26b564fb03224a977d0d62bc292f26ac0a765367e6c1d97dfe983b2a0b620312f952e1153c809b02b72aed97b6f5e161fca1a69994737a8fcaeb350dfb6602e6feab2733470e20890c78cb55ef3ffb5d9";
        Intent intent = Tospay.getInstance(this)
                .setPaymentToken(token)
                .getPaymentIntent();

        startActivityForResult(intent, 1);*/

        startActivityForResult(new Intent(this, AuthActivity.class), 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {

            if (resultCode == Activity.RESULT_OK) {

            }

            if (resultCode == Activity.RESULT_CANCELED) {
                if (data != null) {
                    String result = data.getStringExtra("result");
                    if (result != null) {
                        Toast.makeText(this, result, Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }
    }

    @Override
    public void onSdkVersionNotSupported() {

    }

    @Override
    public void onBiometricAuthenticationNotSupported() {

    }

    @Override
    public void onBiometricAuthenticationNotAvailable() {

    }

    @Override
    public void onBiometricAuthenticationPermissionNotGranted() {

    }

    @Override
    public void onBiometricAuthenticationInternalError(String error) {

    }

    @Override
    public void onAuthenticationFailed() {

    }

    @Override
    public void onAuthenticationCancelled() {

    }

    @Override
    public void onAuthenticationSuccessful() {

    }

    @Override
    public void onAuthenticationHelp(int helpCode, CharSequence helpString) {

    }

    @Override
    public void onAuthenticationError(int errorCode, CharSequence errString) {

    }
}
