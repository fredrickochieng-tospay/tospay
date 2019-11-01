package net.tospay.lib;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import net.tospay.auth.TospayAuth;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        String token = "89657914a49b2a0937c2c2abc9a4f4fa8fca271059f03c6d8cf6b6e3cf53c89ee9529a402ee6cc3d4e06f9e1f594d246ce018b684891af0448fc8c68b6f9330bf9fb6262eeb881a8580d9ce5cb6f02ca4e67d1be41c423eff49d8d881e6b";
        Intent intent = TospayAuth.getInstance(this)
                .setPaymentToken(token)
                .getPaymentIntent();

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
