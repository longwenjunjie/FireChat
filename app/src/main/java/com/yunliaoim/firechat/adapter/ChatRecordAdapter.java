package com.yunliaoim.firechat.adapter;

import com.yunliaoim.firechat.R;
import com.yunliaoim.firechat.adapter.viewholder.ChatRecordViewHolder;
import com.yunliaoim.firechat.bean.ChatRecord;
import com.yunliaoim.firechat.constant.EmotionType;
import com.yunliaoim.firechat.ui.recyclerview.HeaderAndFooterAdapter;
import com.yunliaoim.firechat.ui.recyclerview.ViewHolder;
import com.yunliaoim.firechat.util.ChatTimeUtil;
import com.yunliaoim.firechat.util.EmotionUtil;
import com.yunliaoim.firechat.util.ImageLoaderHelper;
import com.yunliaoim.firechat.utils.util.ValueUtil;

import android.content.Context;
import android.text.SpannableString;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import static android.R.id.message;

/**
 * 聊天记录列表适配器
 */
public class ChatRecordAdapter extends HeaderAndFooterAdapter<ChatRecord> {
    private Context mContext;

    public ChatRecordAdapter(Context context, List<ChatRecord> list) {

        super(list);
        this.mContext = context;
    }

    @Override
    public ViewHolder onCreateItemViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(mContext).inflate(R.layout.chat_record_item_layout, parent, false);
        return new ChatRecordViewHolder(view);
    }

    @Override
    public void onBindItemViewHolder(ViewHolder holder, int position, ChatRecord item) {

        ChatRecordViewHolder viewHolder = (ChatRecordViewHolder) holder;

        if(!ValueUtil.isEmpty(item.getFriendAvatar())) {
            ImageLoaderHelper.displayImage(viewHolder.avatar, item.getFriendAvatar());
        }
        viewHolder.nickName.setText(item.getSessionJid());
        if(!ValueUtil.isEmpty(item.getLastMessage())) {
            if(viewHolder.message.getVisibility() == View.GONE) {
                viewHolder.message.setVisibility(View.VISIBLE);
            }
            SpannableString content = EmotionUtil.getInputEmotionContent(mContext, EmotionType.EMOTION_TYPE_CLASSIC, viewHolder.message, item.getLastMessage());
            viewHolder.message.setText(content);
        }
        viewHolder.chatTime.setText(ChatTimeUtil.getFriendlyTimeSpanByNow(item.getChatTime()));
        String messageCount = item.getUnReadMessageCount() > 0 ? String.valueOf(item.getUnReadMessageCount()) : "";
        viewHolder.messageCount.setText(messageCount);
    }
}
