package net.tospay.lib;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import net.tospay.auth.TospayAuth;
import net.tospay.auth.ui.account.AccountActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        String token = "d2d75c6ef0b685a1bff1ecce4fa789552b481bf1554bc3bfd0f0113ee904a135c4ce4c787557d45682f50cf7b8f27ccf3300d9ace8d026af24f9b262edaa97b2283d2202be138299064bc184cd4a1d7bdd1c20ae14efbbd5faf289877515";
        Intent intent = TospayAuth.getInstance(this)
                .setPaymentToken(token)
                .getPaymentIntent();

        startActivityForResult(intent, 1);

        //startActivityForResult(new Intent(this, AccountActivity.class), 1);
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
