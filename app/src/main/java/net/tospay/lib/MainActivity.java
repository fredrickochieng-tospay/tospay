package net.tospay.lib;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import net.tospay.auth.Tospay;
import net.tospay.auth.ui.auth.AuthActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        String token = "2e4048ce36b2030754599d72ab015dbac4d532e3d0afcc28f84c071859ba3c797bcd67b20b6eeff1af82f62c766cf5c2bc19b05b4e6905e6846e474b99965991e5232dbb5e2aa3ebfde3df21184311f7478215891d9f66b2e26aef104fb0";
        Intent intent = Tospay.getInstance(this)
                .setPaymentToken(token)
                .getPaymentIntent();

        startActivityForResult(intent, 1);

        //startActivityForResult(new Intent(this, AuthActivity.class), 1);
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
}
