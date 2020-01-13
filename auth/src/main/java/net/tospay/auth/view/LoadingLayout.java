package net.tospay.auth.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

import net.tospay.auth.R;

public class LoadingLayout extends LinearLayout {

    private TextView textView;

    public LoadingLayout(Context context) {
        this(context, null, 0);
    }

    public LoadingLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LoadingLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        LayoutInflater.from(context).inflate(R.layout.loading_layout, this, true);
        textView = findViewById(R.id.loading_title);
        TypedArray attributes = context.obtainStyledAttributes(attrs, R.styleable.LoadingLayout);
        String message = attributes.getString(R.styleable.LoadingLayout_loading_title);
        if (message != null) {
            textView.setText(message);
        } else {
            textView.setText(null);
        }
        attributes.recycle();
    }

    public void setLoadingTitle(String text) {
        textView.setText(text);
    }
}
