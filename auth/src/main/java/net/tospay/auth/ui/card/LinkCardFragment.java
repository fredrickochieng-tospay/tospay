package net.tospay.auth.ui.card;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.os.Message;
import android.view.InputEvent;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.ClientCertRequest;
import android.webkit.ConsoleMessage;
import android.webkit.GeolocationPermissions;
import android.webkit.HttpAuthHandler;
import android.webkit.JsPromptResult;
import android.webkit.JsResult;
import android.webkit.PermissionRequest;
import android.webkit.SslErrorHandler;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebStorage;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProviders;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import net.tospay.auth.BR;
import net.tospay.auth.R;
import net.tospay.auth.databinding.FragmentLinkCardBinding;
import net.tospay.auth.ui.base.BaseFragment;


/**
 * A simple {@link BaseFragment} subclass.
 */
public class LinkCardFragment extends BaseFragment<FragmentLinkCardBinding, LinkCardViewModel> {

    private LinkCardViewModel mViewModel;
    private FragmentLinkCardBinding mBinding;
    private String url;

    public LinkCardFragment() {
        // Required empty public constructor
    }

    @Override
    public int getBindingVariable() {
        return BR.linkCardViewModel;
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_link_card;
    }

    @Override
    public LinkCardViewModel getViewModel() {
        mViewModel = ViewModelProviders.of(this).get(LinkCardViewModel.class);
        return mViewModel;
    }

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mBinding = getViewDataBinding();
        mBinding.setLinkCardViewModel(mViewModel);

        String token = getBearerToken();
        url = "http://secure.benkinet.com/skap/" + token;


        final WebSettings webSettings = mBinding.webView.getSettings();
        webSettings.setAllowFileAccess(false);
        webSettings.setBuiltInZoomControls(false);
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);
        if (Build.VERSION.SDK_INT < 18) {
            webSettings.setRenderPriority(WebSettings.RenderPriority.HIGH);
        }

        mBinding.webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                mViewModel.setIsLoading(true);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                mViewModel.setIsLoading(false);
            }
        });

        mBinding.swipeRefreshLayout.setOnRefreshListener(this::loadUrl);

        loadUrl();
    }

    private void loadUrl() {
        mBinding.webView.loadUrl(url);
    }
}
