package com.yunliaoim.firechat.activity.base;

import com.yunliaoim.firechat.R;
import com.yunliaoim.firechat.utils.base.BaseActivity;

import android.os.Build;
import android.os.Bundle;

/**
 *
 */
public class IMBaseActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initNavigationBarColor();
    }

    private void initNavigationBarColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setNavigationBarColor(getResources().getColor(R.color.main_color));
        }
    }

    @Override
    public boolean isLceActivity() {
        return false;
    }
}
