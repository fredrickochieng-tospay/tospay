package net.tospay.auth.ui.dialog;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;


import net.tospay.auth.R;
import net.tospay.auth.R2;

import butterknife.ButterKnife;
import butterknife.OnClick;


public class LinkAccountDialogFragment extends BottomSheetDialogFragment {

    public static final String TAG = LinkAccountDialogFragment.class.getSimpleName();
    private LinkAccountListener mListener;

    public static LinkAccountDialogFragment newInstance() {
        return new LinkAccountDialogFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_link_account_dialog, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        ButterKnife.bind(this, view);
    }

    @OnClick(R2.id.mobileMoneyCardView)
    void onMobileClicked(View view) {
        mListener.onMobileAccount(view);
        dismiss();
    }

    @OnClick(R2.id.bankCardView)
    void onBankClicked(View view) {
        mListener.onBankAccount(view);
        dismiss();
    }

    @OnClick(R2.id.creditCardView)
    void onCreditCardClicked(View view) {
        mListener.onCreditCardAccount(view);
        dismiss();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        final Fragment parent = getParentFragment();
        if (parent != null) {
            mListener = (LinkAccountListener) parent;
        } else {
            mListener = (LinkAccountListener) context;
        }
    }

    @Override
    public void onDetach() {
        mListener = null;
        super.onDetach();
    }

    public interface LinkAccountListener {
        void onMobileAccount(View view);

        void onBankAccount(View view);

        void onCreditCardAccount(View view);
    }
}
