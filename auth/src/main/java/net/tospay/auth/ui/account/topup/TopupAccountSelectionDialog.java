package net.tospay.auth.ui.account.topup;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.PopupMenu;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import net.tospay.auth.R;
import net.tospay.auth.databinding.DialogTopupAccountSelectionBinding;
import net.tospay.auth.interfaces.AccountType;
import net.tospay.auth.model.Account;
import net.tospay.auth.remote.ServiceGenerator;
import net.tospay.auth.remote.repository.AccountRepository;
import net.tospay.auth.remote.repository.PaymentRepository;
import net.tospay.auth.remote.service.AccountService;
import net.tospay.auth.remote.service.PaymentService;
import net.tospay.auth.remote.util.AppExecutors;
import net.tospay.auth.ui.account.AccountAdapter;
import net.tospay.auth.ui.account.AccountViewModel;
import net.tospay.auth.ui.account.OnAccountItemClickListener;
import net.tospay.auth.utils.SharedPrefManager;
import net.tospay.auth.viewmodelfactory.AccountViewModelFactory;

import java.util.ArrayList;
import java.util.List;

public class TopupAccountSelectionDialog extends BottomSheetDialogFragment
        implements OnAccountItemClickListener {

    public static final String TAG = "TopupDialog";

    private AccountViewModel mViewModel;
    private DialogTopupAccountSelectionBinding mBinding;
    private SharedPrefManager mSharedPrefManager;
    private AccountAdapter adapter;
    private OnAccountListener mListener;

    public TopupAccountSelectionDialog() {
        // Required empty public constructor
    }

    public static TopupAccountSelectionDialog newInstance() {
        return new TopupAccountSelectionDialog();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NORMAL, R.style.BottomSheetDialogTheme);
        mSharedPrefManager = SharedPrefManager.getInstance(getContext());
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.dialog_topup_account_selection, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        AppExecutors mAppExecutors = new AppExecutors();

        AccountRepository accountRepository = new AccountRepository(mAppExecutors,
                ServiceGenerator.createService(AccountService.class, getContext()));

        PaymentRepository paymentRepository = new PaymentRepository(mAppExecutors,
                ServiceGenerator.createService(PaymentService.class, getContext()));

        AccountViewModelFactory factory =
                new AccountViewModelFactory(accountRepository, paymentRepository);

        mViewModel = ViewModelProviders.of(this, factory).get(AccountViewModel.class);
        mBinding.setAccountViewModel(mViewModel);

        String bearerToken = "Bearer " + mSharedPrefManager.getAccessToken();
        mViewModel.setBearerToken(bearerToken);

        List<AccountType> accountTypes = new ArrayList<>();
        adapter = new AccountAdapter(accountTypes, this);
        mBinding.recyclerView.setItemAnimator(new DefaultItemAnimator());
        mBinding.recyclerView.setAdapter(adapter);
        mBinding.filterLayout.setOnClickListener(this::showPopup);

        fetchAccounts();
    }

    @SuppressWarnings("ConstantConditions")
    private void showPopup(View v) {
        PopupMenu popup = new PopupMenu(getContext(), v);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.menu_filter, popup.getMenu());

        popup.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.action_all) {
                mBinding.filterText.setText("All");
                filterAccounts(AccountType.ALL);
                return true;
            }

            if (item.getItemId() == R.id.action_mobile) {
                mBinding.filterText.setText("Mobile");
                filterAccounts(AccountType.MOBILE);
                return true;
            }

            if (item.getItemId() == R.id.action_card) {
                mBinding.filterText.setText("Card");
                filterAccounts(AccountType.CARD);
                return true;
            }

            return false;
        });

        popup.show();
    }

    private void filterAccounts(int accountType) {
        adapter.getFilter().filter(String.valueOf(accountType));
    }

    private void fetchAccounts() {
        mViewModel.fetchAccounts(false);
        mViewModel.getAccountsResourceLiveData().observe(this, resource -> {
            if (resource != null) {
                switch (resource.status) {
                    case LOADING:
                        mViewModel.setIsLoading(true);
                        mViewModel.setIsError(false);
                        mViewModel.setLoadingTitle("Loading accounts...");
                        break;

                    case ERROR:
                        mViewModel.setIsLoading(false);
                        mViewModel.setIsError(true);
                        mViewModel.setErrorMessage(resource.message);
                        break;

                    case SUCCESS:
                        mViewModel.setIsLoading(false);
                        mViewModel.setIsError(false);
                        if (resource.data != null && resource.data.size() > 0) {
                            mBinding.setResource(resource);
                        } else {
                            mViewModel.setIsEmpty(true);
                        }
                        break;
                }
            }
        });
    }

    @Override
    public void onAccountSelectedListener(AccountType accountType) {
        mListener.onAccount((Account) accountType);
        dismiss();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        final Fragment parent = getParentFragment();
        if (parent != null) {
            mListener = (OnAccountListener) parent;
        } else {
            mListener = (OnAccountListener) context;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnAccountListener {

        void onAccount(Account account);
    }
}
