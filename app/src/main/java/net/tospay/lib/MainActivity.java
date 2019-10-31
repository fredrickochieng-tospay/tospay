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

        String token = "fff86bdc61b374a47d4d5c3317a369213eae7cc797ce4b55bef89c4e9a255667ccb27ba63dd6ada268e53ea67df501ff5f2f6c05a81ecf02f605079a8398378d8018864ff992d450bd078e341118172400556ca159d9e10d32211fb339bd";
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
