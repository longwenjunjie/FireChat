package com.yunliaoim.firechat.activity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.yunliaoim.firechat.R;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

/**
 * 关于
 */
public class AboutActivity extends AppCompatActivity {
    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    @BindView(R.id.collapsing_toolbar)
    CollapsingToolbarLayout mCollapsingToolbarLayout;

    @BindView(R.id.about_nestedScrollView)
    NestedScrollView mNestedScrollView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_about);
        ButterKnife.bind(this);

        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);//不显示ToolBar的标题
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mCollapsingToolbarLayout.setTitle(getString(R.string.nav_about));
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    @OnClick({R.id.tv_about_blog, R.id.tv_about_contact, R.id.tv_about_github})
    public void onClick(View view) {

        String url = null;
        switch (view.getId()) {
            case R.id.tv_about_blog:
                url = "http://www.yunliaoim.com/";
                break;
            case R.id.tv_about_github:
                url = "https://github.com/longwenjunjie/FireChat";
                break;
            case R.id.tv_about_contact:
                ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                clipboard.setPrimaryClip(ClipData.newPlainText("text", "757949247"));
                Snackbar.make(mNestedScrollView, "复制成功", Snackbar.LENGTH_SHORT).show();
                break;
        }
        if(url != null) {
            Intent intent = new Intent(this, WebPageActivity.class);
            Uri content_url = Uri.parse(url);
            intent.setData(content_url);
            this.startActivity(intent);
        }
    }
}
