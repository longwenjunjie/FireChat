package com.yunliaoim.firechat.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.yunliaoim.firechat.R;
import com.yunliaoim.firechat.activity.base.IMBaseActivity;
import com.yunliaoim.firechat.bean.ContactEntity;
import com.yunliaoim.firechat.smack.SmackManager;
import com.yunliaoim.firechat.utils.util.UIUtil;
import com.yunliaoim.firechat.utils.util.ValueUtil;

import org.greenrobot.eventbus.EventBus;
import org.jivesoftware.smack.roster.RosterEntry;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * 添加好友
 */
public class UserSearchActivity extends IMBaseActivity {
    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    @BindView(R.id.toolbarTitle)
    TextView mToolbarTitle;

    @BindView(R.id.til_friend_user)
    TextInputLayout mUserTextInput;

    @BindView(R.id.acet_friend_user)
    AppCompatEditText mUserEditText;

    @BindView(R.id.btn_user_search)
    Button mBtnUserSearch;

    public static void start(Context context) {
        Intent intent = new Intent(context, UserSearchActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_user_search);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        ButterKnife.bind(this);

        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);//不显示ToolBar的标题
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mToolbarTitle.setText(getString(R.string.title_user_search));
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    @SuppressLint("CheckResult")
    @OnClick(R.id.btn_user_search)
    public void onUserSearchClick(View v) {
        final String username = mUserEditText.getText().toString();
        if (ValueUtil.isEmpty(username)) {
            mUserTextInput.setError(getString(R.string.error_input_friend_username));
            return;
        }

        finish();
        UserSearchResultActivity.start(UserSearchActivity.this, username);
    }
}
