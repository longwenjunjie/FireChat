package com.yunliaoim.firechat.activity.base;

import butterknife.BindView;
import butterknife.ButterKnife;
import com.yunliaoim.firechat.R;
import com.yunliaoim.firechat.adapter.ChatAdapter;
import com.yunliaoim.firechat.bean.ChatMessage;
import com.yunliaoim.firechat.ui.keyboard.ChatKeyboard;
import com.yunliaoim.firechat.ui.recyclerview.CommonRecyclerView;
import com.yunliaoim.firechat.util.IntentHelper;
import com.yunliaoim.firechat.util.LoginHelper;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

import org.greenrobot.eventbus.EventBus;
import org.litepal.LitePal;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * 单人和多人聊天窗口基类
 */
public abstract class BaseChatActivity extends IMBaseActivity implements ChatKeyboard.KeyboardOperateListener {
    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    @BindView(R.id.toolbarTitle)
    TextView mToolbarTitle;

    /**
     * 聊天内容展示列表
     */
    @BindView(R.id.chat_content)
    CommonRecyclerView mChatMessageRecyclerView;

    /**
     * 聊天输入控件
     */
    @BindView(R.id.ckb_chat_board)
    ChatKeyboard mChatKyboard;

    /**
     * 聊天信息实体类
     */
    protected String mSessionJid;

    /**
     * 聊天记录展示适配器
     */
    protected ChatAdapter mAdapter;

    /**
     * 消息列表布局管理器
     */
    protected LinearLayoutManager mLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mSessionJid = getIntent().getStringExtra(IntentHelper.KEY_SESSION_JID);
        super.onCreate(savedInstanceState);
    }

    @Override
    public void setContentView(@LayoutRes int layoutResID) {
        super.setContentView(layoutResID);

        ButterKnife.bind(this);

        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);//不显示ToolBar的标题
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mToolbarTitle.setText(mSessionJid);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        mChatKyboard.setKeyboardOperateListener(this);

        mLayoutManager = new LinearLayoutManager(this);
        mChatMessageRecyclerView.setLayoutManager(mLayoutManager);
        mChatMessageRecyclerView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                mChatKyboard.hideKeyBoardView();
                return false;
            }
        });

        initData();
    }

    @SuppressLint("CheckResult")
    private void initData() {
        mAdapter = new ChatAdapter(mActivity, new ArrayList<ChatMessage>());
        mChatMessageRecyclerView.setAdapter(mAdapter);

        Observable.create(new ObservableOnSubscribe<List<ChatMessage>>() {
            @Override
            public void subscribe(ObservableEmitter<List<ChatMessage>> emitter) throws Exception {
                List<ChatMessage> messages = LitePal.where("userJid=? and sessionJid=?", LoginHelper.getUser().getUsername(), mSessionJid).find(ChatMessage.class);
                emitter.onNext(messages);
                emitter.onComplete();
            }
        })
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(new Consumer<List<ChatMessage>>() {
            @Override
            public void accept(List<ChatMessage> chatMessages) throws Exception {
                mAdapter.reset(chatMessages);
                mLayoutManager.scrollToPosition(mAdapter.getItemCount() - 1);
            }
        });
    }

    protected void addChatMessageView(ChatMessage message) {
        mAdapter.add(message);
        mLayoutManager.scrollToPosition(mAdapter.getItemCount() - 1);
    }

    @Override
    protected void onStart() {
        super.onStart();

        if(!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onBackPressed() {
        if(mChatKyboard.onInterceptBackPressed()) {
            return;
        }

        super.onBackPressed();
    }
}
