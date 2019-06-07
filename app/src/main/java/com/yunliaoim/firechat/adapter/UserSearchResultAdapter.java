package com.yunliaoim.firechat.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.yunliaoim.firechat.R;
import com.yunliaoim.firechat.adapter.viewholder.ChatRoomViewHolder;

import org.jivesoftware.smackx.muc.RoomInfo;

import java.util.List;

/**
 * 消息列表数据适配器
 */
public class UserSearchResultAdapter extends RecyclerView.Adapter<ChatRoomViewHolder> {

    private Context mContext;
    private List<RoomInfo> mListRoom;

    public UserSearchResultAdapter(Context context, List<RoomInfo> listRoom) {
        this.mContext = context;
        this.mListRoom = listRoom;
    }

    @NonNull
    @Override
    public ChatRoomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chatroom_item_view, parent, false);
        return new ChatRoomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatRoomViewHolder viewHolder, int position) {
        RoomInfo room = mListRoom.get(position);
        viewHolder.mTvRoomname.setText(room.getName());
        viewHolder.mTvRoomdesc.setText(room.getSubject());
    }

    @Override
    public int getItemCount() {
        return mListRoom.size();
    }
}