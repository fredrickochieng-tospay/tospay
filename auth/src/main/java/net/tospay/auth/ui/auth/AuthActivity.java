package net.tospay.auth.ui.auth;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.Navigation;

import net.tospay.auth.R;
import net.tospay.auth.interfaces.PaymentListener;
import net.tospay.auth.model.TospayUser;
import net.tospay.auth.utils.SharedPrefManager;

public class AuthActivity extends AppCompatActivity implements PaymentListener, NavController.OnDestinationChangedListener {

    public static final int REQUEST_CODE_LOGIN = 100;
    private ImageView illustrationIV;
    private   Animation view_fade_in,view_fade_out;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);
        illustrationIV = findViewById(R.id.illustration_view);

        view_fade_in = AnimationUtils.loadAnimation(this,R.anim.view_fade_in);
        view_fade_out = AnimationUtils.loadAnimation(this,R.anim.view_fade_out);

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        navController.setGraph(R.navigation.nav_auth);
        navController.addOnDestinationChangedListener(this);

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
        Intent returnIntent = new Intent();
        setResult(Activity.RESULT_CANCELED, returnIntent);
        finish();
    }

    @Override
    public void onDestinationChanged(@NonNull NavController controller,
                                     @NonNull NavDestination destination,
                                     @Nullable Bundle arguments) {
        if (destination.getId() == R.id.navigation_login) {
            illustrationIV.startAnimation(view_fade_in);
            illustrationIV.setImageResource(R.drawable.ic_login);

        } else if (destination.getId() == R.id.navigation_register) {
            illustrationIV.startAnimation(view_fade_in);
            illustrationIV.setImageResource(R.drawable.ic_register);
        } else if (destination.getId() == R.id.navigation_forgot_password) {
            illustrationIV.startAnimation(view_fade_in);
            illustrationIV.setImageResource(R.drawable.ic_forgot_password);
        } else {
            illustrationIV.startAnimation(view_fade_in);
            illustrationIV.setImageResource(R.drawable.ic_login);
        }
    }
}
