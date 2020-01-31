package net.tospay.auth.ui.account;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import net.tospay.auth.R;
import net.tospay.auth.event.NotificationEvent;
import net.tospay.auth.ui.dialog.TransferDialog;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class MpesaLoadingDialog extends BottomSheetDialogFragment {

    public static final String TAG = "MpesaLoadingDialog";

    public MpesaLoadingDialog() {
        // Required empty public constructor
    }

    public static MpesaLoadingDialog newInstance() {
        return new MpesaLoadingDialog();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NORMAL, R.style.Tospay_BaseBottomSheetDialog);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_mpesa_loading, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onNotification(NotificationEvent notification) {
        if (notification != null) {
            if (notification.getData().getTopic().equals("TOPUP")) {
                dismiss();
            }
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }
}
