package com.yunliaoim.firechat.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;

import butterknife.BindView;
import butterknife.OnClick;
import com.yunliaoim.firechat.R;
import com.yunliaoim.firechat.smack.SmackManager;
import com.yunliaoim.firechat.utils.base.BaseActivity;
import com.yunliaoim.firechat.utils.util.UIUtil;

/**
 * 账号管理
 */
public class AccountMngActivity extends BaseActivity {
	/**
	 * 注销登陆
	 */
	@BindView(R.id.btn_logout)
	Button mBtnLogout;
	/**
	 * 用户状态修改
	 */
	@BindView(R.id.rg_user_state)
	RadioGroup mUserState;

	public static void start(Context context) {
		context.startActivity(new Intent(context, AccountMngActivity.class));
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_account_mng_layout);

		mUserState.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				switch(checkedId) {
					case R.id.rb_user_online://在线
						changeState(0);
						break;
					case R.id.rb_user_busy://忙碌
						changeState(2);
						break;
					case R.id.rb_disconnect://断开连接
						disconnect();
						break;
				}
			}
		});
	}
	
	public void changeState(int code) {
		if(SmackManager.getInstance().updateUserState(code)) {
			UIUtil.showToast(this, "修改状态成功");
		} else {
			UIUtil.showToast(this, "修改状态失败");
		}
	}
	
	public void disconnect() {
		if(SmackManager.getInstance().disconnect()) {
			finish();
		} else {
			UIUtil.showToast(this, "断开连接失败");
		}
	}
	
	/**
	 * 注销登陆
	 * @param v
	 */
	@OnClick(R.id.btn_logout)
	public void onLogoutClick(View v) {
		if(SmackManager.getInstance().logout()) {
			finish();
		} else {
			UIUtil.showToast(this, "注销失败");
		}
	}
}
