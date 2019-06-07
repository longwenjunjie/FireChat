package com.yunliaoim.firechat.smack;

import com.yunliaoim.firechat.util.LoginHelper;

import com.orhanobut.logger.Logger;

import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smackx.muc.InvitationListener;
import org.jivesoftware.smackx.muc.MultiUserChat;
import org.jivesoftware.smackx.muc.packet.MUCUser;
import org.jxmpp.jid.EntityJid;
import org.jxmpp.jid.parts.Resourcepart;

/**
 * 多人聊天邀请监听
 */
public class MultiChatInvitationListener implements InvitationListener {

    @Override
    public void invitationReceived(XMPPConnection conn, MultiUserChat room, EntityJid inviter, String reason, String password, Message message, MUCUser.Invite invitation) {
        try {
            room.join(Resourcepart.from(LoginHelper.getUser().getUsername()));
            SmackListenerManager.addMultiChatMessageListener(room);
        } catch (Exception e) {
            Logger.e(e, "join multiChat failure on invitationReceived");
        }
    }
}
