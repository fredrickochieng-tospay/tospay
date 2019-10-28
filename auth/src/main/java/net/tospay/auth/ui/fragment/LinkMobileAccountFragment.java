package net.tospay.auth.ui.fragment;


import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import net.tospay.auth.R;
import net.tospay.auth.R2;
import net.tospay.auth.TospayGateway;
import net.tospay.auth.api.listeners.MobileAccountListener;
import net.tospay.auth.api.listeners.ResponseListener;
import net.tospay.auth.api.response.TospayException;
import net.tospay.auth.model.Country;
import net.tospay.auth.model.Network;
import net.tospay.auth.ui.dialog.CountryListDialogFragment;
import net.tospay.auth.ui.dialog.NetworkListDialogFragment;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class LinkMobileAccountFragment extends Fragment implements CountryListDialogFragment.CountrySelectedListener,
        NetworkListDialogFragment.NetworkSelectedListener, MobileAccountListener {

    private Country country = null;
    private Network network = null;

    @BindView(R2.id.selectCountryTextView)
    TextView selectCountryTextView;

    @BindView(R2.id.selectNetworkTextView)
    TextView selectNetworkTextView;

    @BindView(R2.id.phoneEditText)
    TextView phoneEditText;

    @BindView(R2.id.nameEditText)
    TextView nameEditText;

    @BindView(R2.id.warning_layout)
    ConstraintLayout warning_layout;

    @BindView(R2.id.warning_text_view)
    TextView warning_text_view;

    @BindView(R2.id.loader)
    FrameLayout loadingProgressBar;

    public LinkMobileAccountFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_link_mobile_account, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
    }


    @OnClick(R2.id.btn_save)
    void save() {
        if (country == null) {
            Toast.makeText(getContext(), "Country is required", Toast.LENGTH_SHORT).show();
            return;
        }

        if (network == null) {
            Toast.makeText(getContext(), "Network is required", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(phoneEditText.getText())) {
            Toast.makeText(getContext(), "Phone is required", Toast.LENGTH_SHORT).show();
            return;
        }

        warning_layout.setVisibility(View.GONE);
        loadingProgressBar.setVisibility(View.VISIBLE);

        TospayGateway.getInstance(getContext())
                .linkMobile(country, network, phoneEditText.getText().toString(),
                        nameEditText.getText().toString(), this);

    }

    @OnClick(R2.id.selectCountryTextView)
    void onSelectCountry() {
        CountryListDialogFragment.newInstance()
                .show(getChildFragmentManager(), CountryListDialogFragment.TAG);
    }

    @OnClick(R2.id.selectNetworkTextView)
    void onSelectNetwork() {
        if (country == null) {
            Toast.makeText(getContext(), "Please select country", Toast.LENGTH_SHORT).show();
            return;
        }

        NetworkListDialogFragment.newInstance(Integer.valueOf(country.getId()))
                .show(getChildFragmentManager(), NetworkListDialogFragment.TAG);
    }

    @Override
    public void onCountrySelected(Country country) {
        this.country = country;
        selectCountryTextView.setText(country.getName());
    }

    @Override
    public void onNetworkSelected(Network network) {
        this.network = network;
        selectNetworkTextView.setText(network.getOperator());
    }

    @Override
    public void onError(TospayException error) {
        loadingProgressBar.setVisibility(View.GONE);
        warning_layout.setVisibility(View.VISIBLE);
        warning_text_view.setText(error.getErrorMessage());
    }

    @Override
    public void onAccountAdded(String accountId) {
        loadingProgressBar.setVisibility(View.GONE);
        NavHostFragment.findNavController(this).navigateUp();
    }
}
