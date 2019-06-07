package com.yunliaoim.firechat.util;

import com.yunliaoim.firechat.constant.MessageType;
import com.yunliaoim.firechat.utils.app.AppContext;
import com.yunliaoim.firechat.utils.util.ValueUtil;

import java.io.File;

/**
 * 应用相关文件帮助类
 *
 */
public class AppFileHelper {

    public static String getAppRoot() {

        return AppContext.getInstance().getExternalCacheDir().getAbsolutePath();
    }

    public static String getAppImageCacheDir() {

        return getAppRoot() + "/image";
    }

    public static String getAppDBDir() {

        return getAppRoot() + "/db";
    }

    public static String getAppCrashDir() {

        return getAppRoot() + "/crash";
    }

    public static String getAppChatDir() {

        return getAppRoot() + "/chat";
    }

    public static File getAppChatMessageDir(int type) {

        String root = getAppChatDir();
        String child = "";
        if(type == MessageType.MESSAGE_TYPE_IMAGE.value()) {
            child = "recv_image";
        } else if(type == MessageType.MESSAGE_TYPE_VOICE.value()) {
            child = "recv_voice";
        }
        File file;
        if(ValueUtil.isEmpty(child)) {
            file = new File(root);
        } else {
            file = new File(root, child);
        }
        if(!file.exists()) {
            file.mkdirs();
        }
        return file;
    }
}
