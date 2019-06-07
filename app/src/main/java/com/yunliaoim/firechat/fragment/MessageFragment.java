package com.yunliaoim.firechat.fragment;

import butterknife.BindView;
import butterknife.ButterKnife;
import com.yunliaoim.firechat.R;
import com.yunliaoim.firechat.adapter.ChatRecordAdapter;
import com.yunliaoim.firechat.decoration.CommonItemDecoration;
import com.yunliaoim.firechat.bean.ChatMessage;
import com.yunliaoim.firechat.bean.ChatRecord;
import com.yunliaoim.firechat.ui.recyclerview.CommonRecyclerView;
import com.yunliaoim.firechat.ui.recyclerview.HeaderAndFooterAdapter;
import com.yunliaoim.firechat.util.IMUtil;
import com.yunliaoim.firechat.util.LoginHelper;
import com.yunliaoim.firechat.utils.base.BaseFragment;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

import com.orhanobut.logger.Logger;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.jxmpp.jid.parts.Localpart;
import org.litepal.LitePal;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;

import java.util.HashMap;
import java.util.List;

/**
 * 聊天消息列表
 */
public class MessageFragment extends BaseFragment implements CommonRecyclerView.OnItemClickListener {
    @BindView(R.id.recycler_message_record)
    CommonRecyclerView mRecyclerView;

    private LinearLayoutManager mLayoutManager;
    private ChatRecordAdapter mAdapter;
    private HashMap<String, Integer> mMap = new HashMap<>();//聊天用户的用户名与用户聊天记录Position的映射关系

    @Override
    public View getContentView(LayoutInflater inflater, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_message, null);
        ButterKnife.bind(this, view);
        mLayoutManager = new LinearLayoutManager(mContext);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.addItemDecoration(new CommonItemDecoration());
        mRecyclerView.setOnItemClickListener(this);
        return view;
    }

    @SuppressLint("CheckResult")
    @Override
    public void refreshData() {
        Observable.create(new ObservableOnSubscribe<List<ChatRecord>>() {
            @Override
            public void subscribe(ObservableEmitter<List<ChatRecord>> emitter) throws Exception {
                List<ChatRecord> list = LitePal.where("userJid=?", LoginHelper.getUser().getUsername()).find(ChatRecord.class);
                emitter.onNext(list);
                emitter.onComplete();
            }
        })
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .doOnError(new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {
                refreshFailed();
                Logger.e(throwable, "get chat record failure");
            }
        })
        .subscribe(new Consumer<List<ChatRecord>>() {
            @Override
            public void accept(List<ChatRecord> chatRecords) throws Exception {
                mAdapter = new ChatRecordAdapter(mContext, chatRecords);
                mRecyclerView.setAdapter(mAdapter);
                refreshSuccess();
            }
        });
    }

    @Override
    public void onItemClick(HeaderAndFooterAdapter adapter, int position, View itemView) {
        IMUtil.startP2PChatActivity(mContext, mAdapter.getItem(position).getSessionJid());
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        EventBus.getDefault().register(this);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        EventBus.getDefault().unregister(this);
        mAdapter = null;
        mMap.clear();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onChatRecordEvent(ChatRecord event) {
        //向其他人发起聊天时接收到的事件
        if(isRemoving() || mAdapter == null) {
            return;
        }
        if(mAdapter.getData().indexOf(event) > -1) {
            return;//已经存在此人的聊天窗口记录
        }
        addChatRecord(event);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onReceiveChatMessageEvent(ChatMessage message) {
        //收到发送的消息时接收到的事件(包括别人发送的和自己发送的消息)
        if(isRemoving() || mAdapter == null) {
            return;
        }

        if (message.isMulti()) {
            return;
        }

        ChatRecord chatRecord = getChatRecord(message);
        if(chatRecord == null) {//还没有创建此朋友的聊天记录
            chatRecord = new ChatRecord(message);
            addChatRecord(chatRecord);
        } else {
            chatRecord.setChatTime(message.getDatetime());
            chatRecord.setLastMessage(message.getContent());
            chatRecord.updateUnReadMessageCount();
            mAdapter.update(chatRecord);
            chatRecord.updateAll("sessionJid=? and userJid=?", chatRecord.getSessionJid(), chatRecord.getUserJid());
        }
    }

    private void addChatRecord(ChatRecord chatRecord) {
        mAdapter.add(chatRecord, 0);
        chatRecord.save();
        mLayoutManager.scrollToPosition(0);
        for(String key : mMap.keySet()) {//创建新的聊天记录之后，需要将之前的映射关系进行更新
            mMap.put(key, mMap.get(key) + 1);
        }
    }

    /**
     * 根据消息获取聊天记录窗口对象
     *
     */
    private ChatRecord getChatRecord(ChatMessage message) {

        ChatRecord chatRecord = null;
        if(mMap.containsKey(message.getSessionJid())) {
            chatRecord = mAdapter.getData().get(mMap.get(message.getSessionJid()));
        } else {
            for(int i = 0; i < mAdapter.getData().size(); i++) {
                chatRecord = mAdapter.getData().get(i);
                if(chatRecord.getSessionJid().equals(message.getSessionJid()) &&
                        chatRecord.getUserJid().equals(message.getUserJid())) {
                    mMap.put(message.getSessionJid(), i);
                    break;
                } else {
                    chatRecord = null;
                }
            }
        }
        return chatRecord;
    }

    @Override
    public String getTitle() {
        return getString(R.string.text_message);
    }
}
