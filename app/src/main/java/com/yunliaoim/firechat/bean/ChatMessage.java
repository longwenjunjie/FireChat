package com.yunliaoim.firechat.bean;

import com.yunliaoim.firechat.constant.FileLoadState;
import com.yunliaoim.firechat.utils.util.DateUtil;

import org.litepal.crud.LitePalSupport;

import java.util.Date;

/**
 * 聊天发送的消息
 */
public class ChatMessage extends LitePalSupport {

    private String sessionJid;

    private String userJid;

    private String fromUser;//群聊，是那个用户发的

    private String content;

    private int type;

    /**
     * 消息发送接收的时间
     */
    private String datetime;

    /**
     * 当前消息是否是自己发出的
     */
    private boolean isMeSend;

    /**
     * 接收的图片或语音路径
     */
    private String filePath;

    /**
     * 文件加载状态 {@link FileLoadState}
     */
    private int fileLoadState = FileLoadState.STATE_LOAD_START.value();

    /**
     * 是否为群聊记录
     */
    private boolean isMulti = false;

    public ChatMessage() {

    }

    public ChatMessage(int messageType, boolean isMeSend) {
        type = messageType;
        this.isMeSend = isMeSend;
        this.datetime = DateUtil.formatDatetime(new Date());
    }

    public String getSessionJid() {
        return sessionJid;
    }

    public void setSessionJid(String sessionJid) {
        this.sessionJid = sessionJid;
    }

    public String getFromUser() {
        return fromUser;
    }

    public void setFromUser(String fromUser) {
        this.fromUser = fromUser;
    }

    public String getUserJid() {
        return userJid;
    }

    public void setUserJid(String userJid) {
        this.userJid = userJid;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getMessageType() {
        return type;
    }

    public void setMessageType(int messageType) {

        type = messageType;
    }

    public String getDatetime() {
        return datetime;
    }

    public void setDatetime(String datetime) {
        this.datetime = datetime;
    }

    public boolean isMeSend() {
        return isMeSend;
    }

    public void setMeSend(boolean meSend) {
        isMeSend = meSend;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public int getFileLoadState() {
        return fileLoadState;
    }

    public void setFileLoadState(int fileLoadState) {
        this.fileLoadState = fileLoadState;
    }

    public boolean isMulti() {
        return isMulti;
    }

    public void setMulti(boolean multi) {
        isMulti = multi;
    }

}
