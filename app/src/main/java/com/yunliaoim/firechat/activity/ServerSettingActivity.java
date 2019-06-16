package com.yunliaoim.firechat.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.yunliaoim.firechat.R;
import com.yunliaoim.firechat.activity.base.IMBaseActivity;
import com.yunliaoim.firechat.smack.SmackManager;
import com.yunliaoim.firechat.util.ServerConfig;
import com.yunliaoim.firechat.util.VerifyUtil;
import com.yunliaoim.firechat.utils.util.UIUtil;

import java.util.HashMap;
import java.util.concurrent.Callable;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * 注册
 */
public class ServerSettingActivity extends IMBaseActivity {
    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    @BindView(R.id.toolbarTitle)
    TextView mToolbarTitle;

    //服务器IP
    @BindView(R.id.til_server_ip)
    TextInputLayout mTilServerIP;

    @BindView(R.id.acet_server_ip)
    AppCompatEditText mAcetServerIP;

    //服务器端口
    @BindView(R.id.til_server_port)
    TextInputLayout mTilServerPort;

    @BindView(R.id.acet_server_port)
    AppCompatEditText mAcetServerPort;

    //服务器域
    @BindView(R.id.til_server_domain)
    TextInputLayout mTilServerDomain;

    @BindView(R.id.acet_server_domain)
    AppCompatEditText mAcetServerDomain;

    public static void start(Context context) {
        Intent intent = new Intent(context, ServerSettingActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_server_setting_layout);
        ButterKnife.bind(this);

        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);//不显示ToolBar的标题
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mToolbarTitle.setText(getString(R.string.title_server_setting));
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        String serverIP = ServerConfig.getIP();
        int serverPort = ServerConfig.getPort();
        String serverDomain = ServerConfig.getDomain();
        mAcetServerIP.setText(serverIP);
        mAcetServerPort.setText(String.valueOf(serverPort));
        mAcetServerDomain.setText(serverDomain);
    }

    @OnClick(R.id.btn_server_setting)
    public void onRegisterOk(View v) {
        String serverIP = mAcetServerIP.getText().toString();
        if(!VerifyUtil.checkIP(serverIP)) {
            mTilServerIP.setError(getString(R.string.error_server_ip));
            return;
        }

        String serverPort = mAcetServerPort.getText().toString();
        if(!VerifyUtil.checkPort(serverPort)) {
            mTilServerPort.setError(getString(R.string.error_server_port));
            return;
        }

        String serverDomain = mAcetServerDomain.getText().toString();
        if(VerifyUtil.checkEmpty(serverDomain)) {
            mTilServerDomain.setError(getString(R.string.error_server_domain));
            return;
        }

        ServerConfig.setIP(serverIP);
        ServerConfig.setPort(Integer.parseInt(serverPort));
        ServerConfig.setDomain(serverDomain);

        finish();
    }

}
