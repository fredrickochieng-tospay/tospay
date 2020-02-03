package net.tospay.lib;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
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
    }

    public void pay(View view) {
        String token = "YIUOKJWOFAJANKXF";
        String url = "https://developer.android.com/guide/topics/resources/string-resource";

        Intent intent = Tospay.getInstance(this)
                .setPaymentToken(token)
                .setTermsAndConditionsUrl(url)
                .getPaymentIntent();

        /*Intent intent = Tospay.getInstance(this)
                .setPaymentToken(token)
                .setTermsAndConditionsUrl(url)
                .getAuthenticationIntent();*/

        startActivityForResult(intent, 1);
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
