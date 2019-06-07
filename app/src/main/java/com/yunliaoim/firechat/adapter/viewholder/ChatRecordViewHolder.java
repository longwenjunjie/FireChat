package com.yunliaoim.firechat.adapter.viewholder;

import butterknife.BindView;
import butterknife.ButterKnife;
import com.yunliaoim.firechat.R;
import com.yunliaoim.firechat.ui.recyclerview.ViewHolder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

/**
 *
 */
public class ChatRecordViewHolder extends ViewHolder {
    @BindView(R.id.chat_friend_avatar)
    public ImageView avatar;
    @BindView(R.id.chat_friend_nickname)
    public TextView nickName;
    @BindView(R.id.chat_message)
    public TextView message;
    @BindView(R.id.chat_time)
    public TextView chatTime;
    @BindView(R.id.chat_message_count)
    public TextView messageCount;

    public ChatRecordViewHolder(View itemView) {

        super(itemView);
        ButterKnife.bind(this, itemView);
    }
}
