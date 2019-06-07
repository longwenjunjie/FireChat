package com.yunliaoim.firechat.activity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.yunliaoim.firechat.R;
import com.yunliaoim.firechat.activity.base.IMBaseActivity;
import com.yunliaoim.firechat.bean.User;
import com.yunliaoim.firechat.smack.SmackManager;
import com.yunliaoim.firechat.ui.ClearEditText;
import com.yunliaoim.firechat.util.LoginHelper;
import com.yunliaoim.firechat.utils.util.UIUtil;
import com.yunliaoim.firechat.utils.util.ValueUtil;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.AppCompatCheckBox;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;

import java.util.concurrent.Callable;

/**
 * 登陆openfire服务器
 *
 */
public class LoginActivity extends IMBaseActivity {
    /**
     * 登陆用户
     */
    @BindView(R.id.et_login_username)
    ClearEditText mEditTextUser;
    /**
     * 登陆密码
     */
    @BindView(R.id.et_login_password)
    ClearEditText mEditTextPwd;
    /**
     * 登陆按钮
     */
    @BindView(R.id.btn_login)
    Button mBtnLogin;
    /**
     * 记住密码
     */
    @BindView(R.id.cb_remember_password)
    AppCompatCheckBox mCbRememberPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login_layout);
        ButterKnife.bind(this);

        initViews();
        initUserInfo();
    }

    private void initViews() {
        mEditTextUser.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                mEditTextPwd.setText("");
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void initUserInfo() {
        boolean isRemember = LoginHelper.isRememberPassword();
        if (isRemember) {
            User user = LoginHelper.getUser();
            mEditTextUser.setText(user.getUsername());
            mEditTextPwd.setText(user.getPassword());
        }
        mCbRememberPassword.setChecked(isRemember);
    }

    /**
     * 登陆响应
     */
    @SuppressLint("CheckResult")
    @OnClick(R.id.btn_login)
    public void onLoginClick(View v) {
        final String username = mEditTextUser.getText().toString();
        final String password = mEditTextPwd.getText().toString();
        if (ValueUtil.isEmpty(username)) {
            UIUtil.showToast(this, getString(R.string.login_error_user));
            return;
        }
        if (ValueUtil.isEmpty(password)) {
            UIUtil.showToast(this, getString(R.string.login_error_password));
            return;
        }

        mBtnLogin.setEnabled(false);
        mBtnLogin.setText(getString(R.string.login_button_login_loading));

        Observable.fromCallable(new Callable<User>() {
            @Override
            public User call() throws Exception {
                return SmackManager.getInstance().login(username, password);
            }
        })
        .subscribeOn(Schedulers.io())//指定下面的flatMap线程
        .observeOn(AndroidSchedulers.mainThread())//给下面的subscribe设定线程
        .subscribe(new Consumer <User> () {
            @Override
            public void accept(User user) throws Exception {
                if (user != null) {
                    LoginHelper.rememberRassword(mCbRememberPassword.isChecked());
                    if (!mCbRememberPassword.isChecked()) {
                        user.setPassword("");
                    }
                    LoginHelper.saveUser(user);
                    LoginActivity.this.startActivity(new Intent(LoginActivity.this, MainActivity.class));
                    LoginActivity.this.finish();
                } else {
                    mBtnLogin.setEnabled(true);
                    mBtnLogin.setText(getString(R.string.login_button_unlogin_text));
                    UIUtil.showToast(LoginActivity.this, getString(R.string.net_error));
                }
            }
        });
    }

    /**
     * 用户注册
     *
     * @param v
     */
    @OnClick(R.id.tv_login_register)
    public void onRegisterClick(View v) {
        this.startActivity(new Intent(this, RegisterActivity.class));
    }
}
