package com.yunliaoim.firechat.smack;

import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smackx.muc.MultiUserChat;

import java.util.HashMap;

/**
 * Smack全局监听器管理
 */
public class SmackListenerManager {
    private static volatile SmackListenerManager sSmackListenerManager;
    /**
     * 单聊消息监听管理器
     */
    private FCIncomingChatMessageListener mFCIncomingChatMessageListener;
    private FCOutgoingChatMessageListener mFCOutgoingChatMessageListener;

    /**
     * 群聊邀请监听
     */
    private MultiChatInvitationListener mInvitationListener;
    /**
     * 群聊消息监听
     */
    private MultiChatMessageListener mMultiChatMessageListener;
    /**
     * 群聊信息
     */
    private HashMap<String, MultiUserChat> mMultiUserChatHashMap = new HashMap<>();

    private SmackListenerManager() {
        mFCIncomingChatMessageListener = new FCIncomingChatMessageListener();
        mFCOutgoingChatMessageListener = new FCOutgoingChatMessageListener();
        mInvitationListener = new MultiChatInvitationListener();
        mMultiChatMessageListener = new MultiChatMessageListener();
    }

    public static SmackListenerManager getInstance() {

        if(sSmackListenerManager == null) {
            synchronized (SmackListenerManager.class) {
                if(sSmackListenerManager == null) {
                    sSmackListenerManager = new SmackListenerManager();
                }
            }
        }
        return sSmackListenerManager;
    }

    public static void addGlobalListener() {
        //不设置FromMode OutgoingChatMessageListener会监听不到
        SmackManager.getInstance().getConnection().setFromMode(XMPPConnection.FromMode.USER);

        addMessageListener();
        addInvitationListener();
    }

    /**
     * 添加单聊消息全局监听
     */
    static void addMessageListener() {
        SmackManager.getInstance().getChatManager().addIncomingListener(getInstance().mFCIncomingChatMessageListener);
        SmackManager.getInstance().getChatManager().addOutgoingListener(getInstance().mFCOutgoingChatMessageListener);
    }

    /**
     * 添加群聊邀请监听
     */
    static void addInvitationListener() {
        SmackManager.getInstance().getMultiUserChatManager().addInvitationListener(getInstance().mInvitationListener);
    }

    /**
     * 为指定群聊添加消息监听
     *
     * @param multiUserChat
     */
    public static void addMultiChatMessageListener(MultiUserChat multiUserChat) {
        if(multiUserChat == null) {
            return;
        }

        getInstance().mMultiUserChatHashMap.put(multiUserChat.getRoom().toString(), multiUserChat);
        multiUserChat.addMessageListener(getInstance().mMultiChatMessageListener);
    }

    public void destroy() {
        SmackManager.getInstance().getChatManager().removeIncomingListener(mFCIncomingChatMessageListener);
        SmackManager.getInstance().getChatManager().removeOutgoingListener(mFCOutgoingChatMessageListener);
        SmackManager.getInstance().getMultiUserChatManager().removeInvitationListener(mInvitationListener);

        for(MultiUserChat multiUserChat : mMultiUserChatHashMap.values()) {
            multiUserChat.removeMessageListener(mMultiChatMessageListener);
        }

        mFCIncomingChatMessageListener = null;
        mFCOutgoingChatMessageListener = null;
        mInvitationListener = null;
        mMultiChatMessageListener = null;
        mMultiUserChatHashMap.clear();
    }
}
