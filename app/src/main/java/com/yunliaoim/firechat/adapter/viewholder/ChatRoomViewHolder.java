package com.yunliaoim.firechat.adapter.viewholder;

import android.view.View;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import com.yunliaoim.firechat.R;
import com.yunliaoim.firechat.ui.recyclerview.ViewHolder;

/**
 *
 */
public class ChatRoomViewHolder extends ViewHolder {
    @BindView(R.id.tv_roomname)
    public TextView mTvRoomname;

    @BindView(R.id.tv_roomdesc)
    public TextView mTvRoomdesc;

    public ChatRoomViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }
}
