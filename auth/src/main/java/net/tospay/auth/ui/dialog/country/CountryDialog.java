package net.tospay.auth.ui.dialog.country;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import net.tospay.auth.R;
import net.tospay.auth.remote.ApiConstants;
import net.tospay.auth.remote.ServiceGenerator;
import net.tospay.auth.databinding.DialogCountryBinding;
import net.tospay.auth.model.Country;
import net.tospay.auth.remote.service.GatewayService;
import net.tospay.auth.remote.util.AppExecutors;
import net.tospay.auth.remote.repository.GatewayRepository;
import net.tospay.auth.ui.GatewayViewModelFactory;
import net.tospay.auth.utils.CountryFlagUtils;
import net.tospay.auth.utils.SharedPrefManager;

import java.util.ArrayList;
import java.util.List;

public class CountryDialog extends BottomSheetDialogFragment {

    public static final String TAG = CountryDialog.class.getSimpleName();
    private static final String KEY_MOBILE_OPERATORS = "mobile_operators";

    private DialogCountryBinding mBinding;
    private CountrySelectedListener mListener;
    private List<Country> countryList;
    private CountryAdapter adapter;
    private boolean isMobileOperators = true;
    private CountryViewModel mViewModel;

    public static CountryDialog newInstance(boolean isMobileOperators) {
        CountryDialog fragment = new CountryDialog();
        Bundle args = new Bundle();
        args.putBoolean(KEY_MOBILE_OPERATORS, isMobileOperators);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NORMAL, R.style.BaseBottomSheetDialog);
        this.countryList = new ArrayList<>();
        this.adapter = new CountryAdapter(countryList);

        if (getArguments() != null) {
            isMobileOperators = getArguments().getBoolean(KEY_MOBILE_OPERATORS);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.dialog_country, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        AppExecutors mAppExecutors = new AppExecutors();
        GatewayService gatewayService = ServiceGenerator.createService(GatewayService.class);
        GatewayRepository mGatewayRepository = new GatewayRepository(mAppExecutors, gatewayService);
        GatewayViewModelFactory factory = new GatewayViewModelFactory(mGatewayRepository);
        mViewModel = ViewModelProviders.of(this, factory).get(CountryViewModel.class);

        mBinding.setCountryViewModel(mViewModel);
        mBinding.recyclerView.addItemDecoration(new DividerItemDecoration(view.getContext(), DividerItemDecoration.HORIZONTAL));
        mBinding.recyclerView.setAdapter(adapter);

        SharedPrefManager mSharedPrefManager = SharedPrefManager.getInstance(view.getContext());
        setBearerToken(mSharedPrefManager.getAccessToken());

        mViewModel.countries(isMobileOperators);
        mViewModel.getResourceLiveData().observe(this, resource -> {
            if (resource != null) {
                switch (resource.status) {
                    case ERROR:
                        Toast.makeText(getContext(), resource.message, Toast.LENGTH_SHORT).show();
                        mViewModel.setIsLoading(false);
                        dismiss();
                        break;

                    case SUCCESS:
                        mViewModel.setIsLoading(false);
                        if (resource.data != null) {
                            this.countryList = resource.data;
                            adapter.setCountries(countryList);
                            adapter.notifyDataSetChanged();
                        }
                        break;
                }
            }
        });
    }

    public void setBearerToken(String token) {
        String bearerToken = "Bearer " + token;
        mViewModel.setBearerToken(bearerToken);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        final Fragment parent = getParentFragment();
        if (parent != null) {
            mListener = (CountrySelectedListener) parent;
        } else {
            mListener = (CountrySelectedListener) context;
        }
    }

    @Override
    public void onDetach() {
        mListener = null;
        super.onDetach();
    }

    public interface CountrySelectedListener {
        void onCountrySelected(Country country);
    }

    private class ViewHolder extends RecyclerView.ViewHolder {

        final TextView text;
        final TextView countryFlag;

        ViewHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.list_item_country_view, parent, false));
            text = itemView.findViewById(R.id.text);
            countryFlag = itemView.findViewById(R.id.countryFlag);
            itemView.setOnClickListener(v -> {
                if (mListener != null) {
                    mListener.onCountrySelected(countryList.get(getAdapterPosition()));
                    dismiss();
                }
            });
        }
    }

    private class CountryAdapter extends RecyclerView.Adapter<ViewHolder> {

        private List<Country> countryList;

        CountryAdapter(List<Country> countryList) {
            this.countryList = countryList;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(parent.getContext()), parent);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            Country country = countryList.get(position);
            holder.text.setText(country.getName());
            holder.countryFlag.setText(CountryFlagUtils.flag(country.getIso()));
        }

        @Override
        public int getItemCount() {
            return countryList != null ? countryList.size() : 0;
        }

        void setCountries(List<Country> countries) {
            this.countryList = countries;
            notifyItemRangeChanged(0, countryList.size());
        }
    }
}
