package com.yunliaoim.firechat.bean;

import com.yunliaoim.firechat.util.LoginHelper;
import com.yunliaoim.firechat.utils.util.DateUtil;

import org.litepal.crud.LitePalSupport;

/**
 * 聊天记录实体对象
 */
public class ChatRecord extends LitePalSupport {

    private String sessionJid;

    private String userJid;

    /**
     * 最后聊天时间
     */
    private String chatTime;

    /**
     * 朋友头像地址
     */
    private String friendAvatar;

    /**
     * 最后一条聊天记录
     */
    private String lastMessage;

    /**
     * 未读消息数
     */
    private int unReadMessageCount;

    public ChatRecord(ChatMessage chatMessage) {
        setSessionJid(chatMessage.getSessionJid());
        setUserJid(LoginHelper.getUser().getUsername());
        setChatTime(chatMessage.getDatetime());
        setLastMessage(chatMessage.getContent());
        updateUnReadMessageCount();
    }

    public String getSessionJid() {
        return sessionJid;
    }

    public void setSessionJid(String sessionJid) {
        this.sessionJid = sessionJid;
    }

    public String getUserJid() {
        return userJid;
    }

    public void setUserJid(String userJid) {
        this.userJid = userJid;
    }

    public String getFriendAvatar() {
        return friendAvatar;
    }

    public void setFriendAvatar(String friendAvatar) {
        this.friendAvatar = friendAvatar;
    }

    public String getChatTime() {
        return chatTime == null ? DateUtil.currentDatetime() : chatTime;
    }

    public void setChatTime(String chatTime) {
        this.chatTime = chatTime;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    public int getUnReadMessageCount() {
        return unReadMessageCount;
    }

    public void updateUnReadMessageCount() {
        unReadMessageCount += 1;
    }

}
