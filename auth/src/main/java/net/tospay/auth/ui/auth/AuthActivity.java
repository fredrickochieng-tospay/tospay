package net.tospay.auth.ui.auth;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import net.tospay.auth.R;
import net.tospay.auth.interfaces.PaymentListener;
import net.tospay.auth.model.TospayUser;
import net.tospay.auth.utils.SharedPrefManager;

public class AuthActivity extends AppCompatActivity implements PaymentListener {

    public static final int REQUEST_CODE_LOGIN = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        navController.setGraph(R.navigation.nav_auth);

        TospayUser tospayUser = SharedPrefManager.getInstance(this).getActiveUser();
        if (tospayUser != null) {
            if (!tospayUser.isEmailVerified()) {
                navController.navigate(R.id.navigation_email_verification);

            } else if (!tospayUser.isPhoneVerified()) {
                navController.navigate(R.id.navigation_phone_verification);
            }
        }
    }

    @Override
    public void onLoginSuccess(TospayUser user) {
        Intent returnIntent = new Intent();
        returnIntent.putExtra("result", user);
        setResult(Activity.RESULT_OK, returnIntent);
        finish();
    }

    @Override
    public void onLoginFailed() {
        Log.e("TAG", "onLoginFailed: ");
        Intent returnIntent = new Intent();
        setResult(Activity.RESULT_CANCELED, returnIntent);
        finish();
    }
}
