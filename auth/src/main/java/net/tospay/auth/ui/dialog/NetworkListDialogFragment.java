package net.tospay.auth.ui.dialog;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
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
import net.tospay.auth.api.listeners.NetworkListener;
import net.tospay.auth.api.response.TospayException;
import net.tospay.auth.model.Network;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


public class NetworkListDialogFragment extends BottomSheetDialogFragment implements NetworkListener {

    public static final String TAG = NetworkListDialogFragment.class.getSimpleName();

    @BindView(R2.id.recyclerView)
    RecyclerView recyclerView;

    @BindView(R2.id.progressBar)
    ProgressBar progressBar;

    private static final String ARG_ITEM_COUNTRY_ID = "item_country_id";

    private NetworkSelectedListener mListener;
    private int countryId;
    private List<Network> networks;
    private NetworkAdapter adapter;

    public static NetworkListDialogFragment newInstance(int countryId) {
        final NetworkListDialogFragment fragment = new NetworkListDialogFragment();
        final Bundle args = new Bundle();
        args.putInt(ARG_ITEM_COUNTRY_ID, countryId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.networks = new ArrayList<>();
        this.adapter = new NetworkAdapter(networks);

        if (getArguments() != null) {
            countryId = getArguments().getInt(ARG_ITEM_COUNTRY_ID);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_network_list_dialog, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);

        recyclerView.addItemDecoration(new DividerItemDecoration(recyclerView.getContext(), DividerItemDecoration.HORIZONTAL));
        recyclerView.setAdapter(adapter);

        TospayGateway.getInstance(getContext()).fetchMobileOperators(countryId, this);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        final Fragment parent = getParentFragment();
        if (parent != null) {
            mListener = (NetworkSelectedListener) parent;
        } else {
            mListener = (NetworkSelectedListener) context;
        }
    }

    @Override
    public void onDetach() {
        mListener = null;
        super.onDetach();
    }

    @Override
    public void onNetworks(List<Network> networks) {
        progressBar.setVisibility(View.GONE);

        if (networks.isEmpty()) {
            Toast.makeText(getContext(), "No mobile operators found", Toast.LENGTH_SHORT).show();
            return;
        }

        this.networks = networks;
        adapter.setNetworks(networks);
    }

    @Override
    public void onError(TospayException error) {
        Toast.makeText(getContext(), error.getErrorMessage(), Toast.LENGTH_SHORT).show();
        progressBar.setVisibility(View.GONE);
        dismiss();
    }

    public interface NetworkSelectedListener {
        void onNetworkSelected(Network network);
    }

    private class ViewHolder extends RecyclerView.ViewHolder {

        final TextView text;

        ViewHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.fragment_network_list_dialog_item, parent, false));
            text = itemView.findViewById(R.id.text);

            itemView.setOnClickListener(v -> {
                if (mListener != null) {
                    mListener.onNetworkSelected(networks.get(getAdapterPosition()));
                    dismiss();
                }
            });
        }
    }

    private class NetworkAdapter extends RecyclerView.Adapter<ViewHolder> {

        private List<Network> networks;

        NetworkAdapter(List<Network> networks) {
            this.networks = networks;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(parent.getContext()), parent);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            Network network = networks.get(position);
            holder.text.setText(network.getOperator());
        }

        @Override
        public int getItemCount() {
            return networks == null ? 0 : networks.size();
        }

        void setNetworks(List<Network> networks) {
            this.networks = networks;
            notifyItemRangeChanged(0, networks.size());
        }
    }
}
