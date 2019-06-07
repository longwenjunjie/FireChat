package com.yunliaoim.firechat.ui;

import com.yunliaoim.firechat.R;
import com.yunliaoim.firechat.bean.ContactEntity;
import com.yunliaoim.firechat.util.IMUtil;
import com.yunliaoim.firechat.util.IntentHelper;

import org.greenrobot.eventbus.EventBus;
import org.jxmpp.jid.BareJid;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;

/**
 * 联系人列表
 */
public class ChatPromptDialog implements DialogInterface.OnClickListener {

    private AlertDialog mDialog;
    private Context mContext;
    private ContactEntity mContactEntity;

    public ChatPromptDialog(Context context) {
        mContext = context;
    }

    public void show(ContactEntity contactEntity) {
        mContactEntity = contactEntity;
        String message = String.format(mContext.getString(R.string.chat_dialog_message), contactEntity.getRosterEntry().getName());

        if(mDialog == null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
            builder.setTitle(mContext.getString(R.string.chat_dialog_title))
                    .setCancelable(true)
                    .setNegativeButton(mContext.getString(R.string.cancel), this)
                    .setPositiveButton(mContext.getString(R.string.ok), this);
            mDialog = builder.create();
        }
        mDialog.setMessage(message);
        mDialog.show();
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        if(which == DialogInterface.BUTTON_POSITIVE) {
            dialog.dismiss();
//            BareJid jid = mContactEntity.getRosterEntry().getJid();
            IMUtil.startP2PChatActivity(mContext, mContactEntity.getRosterEntry().getName());
            EventBus.getDefault().post(Integer.valueOf(IntentHelper.MESSAGE_TAB_INDEX));
            mContactEntity = null;
        } else if(which == DialogInterface.BUTTON_NEGATIVE) {
            dialog.dismiss();
        }
    }
}
