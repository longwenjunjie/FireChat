package com.yunliaoim.firechat.smack;

import com.yunliaoim.firechat.bean.ChatMessage;
import com.yunliaoim.firechat.constant.MessageType;
import com.yunliaoim.firechat.util.LoginHelper;

import com.orhanobut.logger.Logger;

import org.greenrobot.eventbus.EventBus;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.packet.Message;
import org.jxmpp.jid.parts.Resourcepart;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 多人聊天消息监听
 */
public class MultiChatMessageListener implements MessageListener {

    @Override
    public void processMessage(Message message) {
        Logger.d(message.toString());

        if (message.getStanzaId() == null || message.getSubjects().size() > 0) {
            return;
        }

        String sessionJid = message.getFrom().getLocalpartOrNull().toString();
        String userJid = LoginHelper.getUser().getUsername();
        Resourcepart part = message.getFrom().getResourceOrNull();
        String fromUser = part!=null?part.toString():"未知";

        ChatMessage chatMessage = new ChatMessage(MessageType.MESSAGE_TYPE_TEXT.value(), true);
        chatMessage.setSessionJid(sessionJid);
        chatMessage.setUserJid(userJid);
        chatMessage.setFromUser(fromUser);
        chatMessage.setContent(message.getBody());
        chatMessage.setMeSend(fromUser.equals(userJid));
        chatMessage.setMulti(true);
//        chatMessage.save();

        EventBus.getDefault().post(chatMessage);
    }
}
