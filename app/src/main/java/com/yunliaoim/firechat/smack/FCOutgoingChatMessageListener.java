package com.yunliaoim.firechat.smack;

import com.yunliaoim.firechat.bean.ChatMessage;
import com.yunliaoim.firechat.constant.MessageType;
import com.yunliaoim.firechat.util.LoginHelper;

import org.greenrobot.eventbus.EventBus;
import org.jivesoftware.smack.chat2.Chat;
import org.jivesoftware.smack.chat2.IncomingChatMessageListener;
import org.jivesoftware.smack.chat2.OutgoingChatMessageListener;
import org.jivesoftware.smack.packet.Message;
import org.jxmpp.jid.EntityBareJid;

public class FCOutgoingChatMessageListener implements OutgoingChatMessageListener {

    @Override
    public void newOutgoingMessage(EntityBareJid to, Message message, Chat chat) {
        String sessionJid = message.getFrom().getLocalpartOrNull().toString();
        String userJid = LoginHelper.getUser().getUsername();
//        String fromUser = message.getFrom().getResourceOrNull().toString();

        ChatMessage chatMessage = new ChatMessage(MessageType.MESSAGE_TYPE_TEXT.value(), false);
        chatMessage.setSessionJid(sessionJid);
        chatMessage.setUserJid(userJid);
        chatMessage.setFromUser(sessionJid);
        chatMessage.setContent(message.getBody());
//        chatMessage.save();

//        EventBus.getDefault().post(chatMessage);
    }
}
