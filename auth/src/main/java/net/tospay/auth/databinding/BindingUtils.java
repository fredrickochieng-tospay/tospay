package net.tospay.auth.databinding;

import android.content.Context;
import android.text.TextUtils;
import android.widget.ImageView;

import androidx.databinding.BindingAdapter;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import net.tospay.auth.R;
import net.tospay.auth.view.ErrorLayout;

public final class BindingUtils {

    private BindingUtils() {
        // This class is not publicly instantiable
    }

    @BindingAdapter("imageUrl")
    public static void setImageUrl(ImageView imageView, String imageUrl) {
        Context context = imageView.getContext();

        RequestOptions options = new RequestOptions()
                .placeholder(R.drawable.ic_loading)
                .error(R.drawable.ic_placeholder);

        Glide.with(context)
                .setDefaultRequestOptions(options)
                .load(imageUrl)
                .into(imageView);
    }

    @BindingAdapter("topupImageResource")
    public static void imageTopupResource(ImageView imageView, String sourceChannel) {
        if (TextUtils.equals(sourceChannel, "mobile")) {
            imageView.setImageResource(R.drawable.ic_mobile);

        } else if (TextUtils.equals(sourceChannel, "card")) {
            imageView.setImageResource(R.drawable.ic_link_card);

        } else if (TextUtils.equals(sourceChannel, "bank")) {
            imageView.setImageResource(R.drawable.ic_bank);

        } else {
            imageView.setImageResource(R.drawable.ic_placeholder);
        }
    }

    @BindingAdapter("circular")
    public static void setCircularImage(ImageView imageView, String imageUrl) {
        Context context = imageView.getContext();

        RequestOptions options = new RequestOptions()
                .circleCrop()
                .placeholder(R.drawable.ic_profile)
                .error(R.drawable.ic_profile);

        Glide.with(context)
                .setDefaultRequestOptions(options)
                .load(imageUrl)
                .into(imageView);
    }

    @BindingAdapter("refreshing")
    public static void refreshing(SwipeRefreshLayout swipeRefreshLayout, boolean isRefreshing) {
        swipeRefreshLayout.setRefreshing(isRefreshing);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary);
    }

    @BindingAdapter("error_message")
    public static void errorMessage(ErrorLayout errorLayout, String message) {
        errorLayout.setErrorMessage(message);
    }
}
