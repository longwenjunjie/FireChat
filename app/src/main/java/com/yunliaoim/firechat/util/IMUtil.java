package com.yunliaoim.firechat.util;

import com.yunliaoim.firechat.activity.P2PChatActivity;
import com.yunliaoim.firechat.activity.ChatRoomActivity;
import com.yunliaoim.firechat.utils.app.AppContext;
import com.yunliaoim.firechat.utils.util.PreferenceHelper;

import android.content.Context;
import android.content.Intent;

import java.lang.reflect.Field;

/**
 *
 */
public final class IMUtil {

    private static final String KEY_STORE_KEYBOARD_HEIGHT = "_key_store_keyboard_height";
    private static int sKeyboardHeight = 0;
    private static int sStatusBarHeight = 0;

    public static int getStatusBarHeight() {

        if(sStatusBarHeight == 0) {
            try {
                Class<?> c = Class.forName("com.android.internal.R$dimen");
                Object obj = c.newInstance();
                Field field = c.getField("status_bar_height");
                int x = Integer.parseInt(field.get(obj).toString());
                sStatusBarHeight = AppContext.getInstance().getResources().getDimensionPixelSize(x);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return sStatusBarHeight;
    }

    public static boolean isKeyboardHeightStored() {
        return getKeyboardHeight() > 0;
    }

    public static void storeKeyboardHeight(int keyboardHeight) {
        if(!isKeyboardHeightStored()) {
            PreferenceHelper.putInt(KEY_STORE_KEYBOARD_HEIGHT, keyboardHeight);
        }
    }

    public static int getKeyboardHeight() {
        if(sKeyboardHeight == 0) {
            sKeyboardHeight = PreferenceHelper.getInt(KEY_STORE_KEYBOARD_HEIGHT);
        }
        return sKeyboardHeight;
    }

    public static void startP2PChatActivity(Context context, String sessionJid) {
        Intent intent = new Intent(context, P2PChatActivity.class);
        intent.putExtra(IntentHelper.KEY_SESSION_JID, sessionJid);
        context.startActivity(intent);
    }

    public static void startChatRoomActivity(Context context, String sessionJid) {
        Intent intent = new Intent(context, ChatRoomActivity.class);
        intent.putExtra(IntentHelper.KEY_SESSION_JID, sessionJid);
        context.startActivity(intent);
    }

}
