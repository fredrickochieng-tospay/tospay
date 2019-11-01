package net.tospay.auth.ui.summary;

import android.view.View;

public interface SummaryNavigator {

    void onLogin(View view);

    void onSignUp(View view);

    void onCancel(View view);

    void onContinue(View view);
}
