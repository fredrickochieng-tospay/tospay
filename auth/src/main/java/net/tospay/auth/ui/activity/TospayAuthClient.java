package net.tospay.auth.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import net.tospay.auth.R;
import net.tospay.auth.api.response.PaymentValidationResponse;
import net.tospay.auth.api.response.TospayException;
import net.tospay.auth.interfaces.PaymentListener;
import net.tospay.auth.model.Merchant;
import net.tospay.auth.model.Payment;

import static net.tospay.auth.utils.Constants.KEY_TOKEN;


@SuppressWarnings("ConstantConditions")
public class TospayAuthClient extends AppCompatActivity implements PaymentListener {

    private Payment transaction;
    private Merchant merchant;
    private String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.auth_activity);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        token = getIntent().getStringExtra(KEY_TOKEN);

        Bundle args = new Bundle();
        args.putString(KEY_TOKEN, token);

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        navController.setGraph(R.navigation.nav_graph, args);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.auth_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_close) {
            finishActivity(null);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void finishActivity(String message) {
        Intent returnIntent = new Intent();
        returnIntent.putExtra("result", message);
        setResult(Activity.RESULT_CANCELED, returnIntent);
        finish();
    }


    @Override
    public void onPaymentDetails(PaymentValidationResponse response) {
        this.transaction = response.getTransaction();
        this.merchant = response.getMerchant();
    }

    @Override
    public void onPaymentSuccess() {

    }

    @Override
    public void onPaymentFailed(TospayException exception) {
        finishActivity(exception.getErrorMessage());
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        /*AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Cancel Payment");
        builder.setMessage("Are you sure you want to cancel this payment?");
        builder.setCancelable(false);
        builder.setPositiveButton("No", (dialogInterface, i) -> dialogInterface.cancel());
        builder.setNegativeButton("Yes", (dialogInterface, i) -> {
            dialogInterface.dismiss();
            finishActivity("Payment Canceled");
        });

        builder.show();*/
    }

    public String getToken() {
        return token;
    }

    public Payment getTransaction() {
        return transaction;
    }

    public Merchant getMerchant() {
        return merchant;
    }
}
