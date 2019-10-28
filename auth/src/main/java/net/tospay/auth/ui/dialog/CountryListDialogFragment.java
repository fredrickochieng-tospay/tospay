package net.tospay.auth.ui.dialog;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import net.tospay.auth.R;
import net.tospay.auth.R2;
import net.tospay.auth.TospayGateway;
import net.tospay.auth.api.listeners.CountryListener;
import net.tospay.auth.api.response.TospayException;
import net.tospay.auth.model.Country;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


public class CountryListDialogFragment extends BottomSheetDialogFragment implements CountryListener {

    public static final String TAG = CountryListDialogFragment.class.getSimpleName();
    private CountrySelectedListener mListener;

    @BindView(R2.id.recyclerView)
    RecyclerView recyclerView;

    @BindView(R2.id.progressBar)
    ProgressBar progressBar;

    private List<Country> countryList;
    private CountryAdapter adapter;

    public static CountryListDialogFragment newInstance() {
        return new CountryListDialogFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.countryList = new ArrayList<>();
        this.adapter = new CountryAdapter(countryList);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_country_list_dialog, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        ButterKnife.bind(this, view);

        recyclerView.addItemDecoration(new DividerItemDecoration(recyclerView.getContext(), DividerItemDecoration.HORIZONTAL));
        recyclerView.setAdapter(adapter);

        TospayGateway.getInstance(view.getContext()).fetchCountries(this);
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

    @Override
    public void onCountries(List<Country> countries) {
        progressBar.setVisibility(View.GONE);
        this.countryList = countries;
        adapter.setCountries(countries);
    }

    @Override
    public void onError(TospayException error) {
        Toast.makeText(getContext(), error.getErrorMessage(), Toast.LENGTH_SHORT).show();
        progressBar.setVisibility(View.GONE);
        dismiss();
    }

    public interface CountrySelectedListener {
        void onCountrySelected(Country country);
    }

    private class ViewHolder extends RecyclerView.ViewHolder {

        final TextView text;

        ViewHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.fragment_country_list_dialog_item, parent, false));
            text = itemView.findViewById(R.id.text);
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
