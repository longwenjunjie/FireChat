package com.yunliaoim.firechat.activity;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.jivesoftware.smackx.muc.MultiUserChat;

import android.os.Bundle;

import com.yunliaoim.firechat.R;
import com.yunliaoim.firechat.activity.base.BaseChatActivity;
import com.yunliaoim.firechat.bean.ChatMessage;
import com.yunliaoim.firechat.constant.KeyBoardMoreFunType;
import com.yunliaoim.firechat.smack.SmackManager;
import com.yunliaoim.firechat.utils.util.ValueUtil;

import com.orhanobut.logger.Logger;

import java.io.File;

/**
 * 多人聊天
 */
public class ChatRoomActivity extends BaseChatActivity {
    /**
     * 多人聊天对象
     */
    private MultiUserChat mMultiUserChat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_multichat_layout);
        mMultiUserChat = SmackManager.getInstance().getMultiChat(mSessionJid + "@conference.yunliaoim.com");
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onReceiveChatMessageEvent(ChatMessage message) {
//        if(mChatUser.getFriendUsername().equals(message.getFriendUsername()) && message.isMulti()) {
//            addChatMessageView(message);
//        }

        if (mSessionJid.equals(message.getSessionJid()) && message != null) {
            addChatMessageView(message);
        }
    }

    @Override
    public void send(String message) {
        if (ValueUtil.isEmpty(message)) {
            return;
        }

        try {
            mMultiUserChat.sendMessage(message);
        } catch (Exception e) {
            Logger.e(e, "send message failure");
        }
    }

    @Override
    public void sendVoice(File audioFile) {

    }

    @Override
    public void functionClick(KeyBoardMoreFunType funType) {

    }
}
