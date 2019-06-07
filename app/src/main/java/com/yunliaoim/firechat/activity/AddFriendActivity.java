package com.yunliaoim.firechat.activity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.yunliaoim.firechat.R;
import com.yunliaoim.firechat.activity.base.IMBaseActivity;
import com.yunliaoim.firechat.bean.ContactEntity;
import com.yunliaoim.firechat.smack.SmackManager;
import com.yunliaoim.firechat.utils.util.UIUtil;
import com.yunliaoim.firechat.utils.util.ValueUtil;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

import org.greenrobot.eventbus.EventBus;
import org.jivesoftware.smack.roster.RosterEntry;

/**
 * 添加好友
 */
public class AddFriendActivity extends IMBaseActivity {
    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    @BindView(R.id.toolbarTitle)
    TextView mToolbarTitle;

    @BindView(R.id.til_friend_user)
    TextInputLayout mUserTextInput;

    @BindView(R.id.acet_friend_user)
    AppCompatEditText mUserEditText;

    @BindView(R.id.til_friend_nickname)
    TextInputLayout mNickNameTextInput;

    @BindView(R.id.acet_friend_nickname)
    AppCompatEditText mNickNameEditText;

    @BindView(R.id.btn_add_friend)
    Button mBtnAddFriend;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_addfriend_layout);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        ButterKnife.bind(this);

        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);//不显示ToolBar的标题
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mToolbarTitle.setText(getString(R.string.title_add_friend));
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    @SuppressLint("CheckResult")
    @OnClick(R.id.btn_add_friend)
    public void onAddFriendClick(View v) {
        final String username = mUserEditText.getText().toString();
        if (ValueUtil.isEmpty(username)) {
            mUserTextInput.setError(getString(R.string.error_input_friend_username));
            return;
        }

        final String nickname = mNickNameEditText.getText().toString();
        if (ValueUtil.isEmpty(nickname)) {
            mNickNameTextInput.setError(getString(R.string.error_input_friend_username));
            return;
        }

        Observable.create(new ObservableOnSubscribe<RosterEntry>() {
            @Override
            public void subscribe(ObservableEmitter<RosterEntry> e) throws Exception {
                boolean flag = SmackManager.getInstance().addFriend(username, nickname, null);
                if(flag) {
                    RosterEntry entry = SmackManager.getInstance().getFriend(username);
                    e.onNext(entry);
                    e.onComplete();
                } else {
                    e.onError(new IllegalArgumentException());
                }
            }
        })
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(new Observer<RosterEntry>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(RosterEntry rosterEntry) {
                EventBus.getDefault().post(new ContactEntity(rosterEntry));
            }

            @Override
            public void onError(Throwable e) {
                UIUtil.showToast(mActivity, R.string.hint_add_friend_failure);
            }

            @Override
            public void onComplete() {
                UIUtil.showToast(mActivity, R.string.hint_add_friend_success);
                finish();
            }
        });
    }
}
