package com.yunliaoim.firechat.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import org.jivesoftware.smackx.muc.HostedRoom;
import org.jivesoftware.smackx.muc.MultiUserChat;
import org.jivesoftware.smackx.muc.MultiUserChatManager;
import org.jivesoftware.smackx.muc.RoomInfo;
import org.jxmpp.jid.EntityBareJid;
import org.jxmpp.jid.Jid;
import org.jxmpp.jid.impl.JidCreate;
import org.jxmpp.jid.parts.Resourcepart;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import butterknife.BindView;
import butterknife.ButterKnife;
import com.yunliaoim.firechat.R;
import com.yunliaoim.firechat.adapter.ChatRoomAdapter;
import com.yunliaoim.firechat.smack.SmackListenerManager;
import com.yunliaoim.firechat.smack.SmackManager;
import com.yunliaoim.firechat.util.IMUtil;
import com.yunliaoim.firechat.util.LoginHelper;
import com.yunliaoim.firechat.utils.util.UIUtil;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * 聊天室列表
 */
public class ChatRoomFragment extends Fragment {
    @BindView(R.id.refreshLayout)
    SmartRefreshLayout mSRLayout;

    @BindView(R.id.recyclerView)
    RecyclerView mRecyclerView;

    List<RoomInfo> mListRoom;
    ChatRoomAdapter mAdapter;

    private GestureDetector mGestureDetector;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chatroom, container, false);
        ButterKnife.bind(this, view);

        initView();
        initData();

        return view;
    }

    private void initView() {
        mSRLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshlayout) {
                loadData(true);

            }
        });

        mSRLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshlayout) {
//                loadData(false);
                mSRLayout.finishLoadMore(1000/*,false*/);//传入false表示加载失败
            }
        });

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));

        mGestureDetector = new GestureDetector(getContext(), new GestureDetector.SimpleOnGestureListener() {
            @Override
            public void onLongPress(MotionEvent e) {
                super.onLongPress(e);
            }

            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                View childView = mRecyclerView.findChildViewUnder(e.getX(),e.getY());
                if(childView != null){
                    int position = mRecyclerView.getChildLayoutPosition(childView);
                    RoomInfo room = mListRoom.get(position);

                    MultiUserChat multiUserChat = null;
                    try {
                        multiUserChat = SmackManager.getInstance().getMultiUserChatManager().getMultiUserChat(room.getRoom());
                        String name = LoginHelper.getUser().getUsername();
                        multiUserChat.join(Resourcepart.fromOrNull(name));
                        SmackListenerManager.addMultiChatMessageListener(multiUserChat);
                    } catch (Exception e2) {
                        UIUtil.showToast(getContext(), "无法加入");
                        return true;
                    }

                    IMUtil.startChatRoomActivity(getContext(), multiUserChat.getRoom().getLocalpart().toString());
                    return true;
                }

                return super.onSingleTapUp(e);
            }
        });

        mRecyclerView.addOnItemTouchListener(new RecyclerView.SimpleOnItemTouchListener() {
            @Override
            public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
                //交由手势处理
                return mGestureDetector.onTouchEvent(e);
            }
        });

        mListRoom = new ArrayList<>();
        mAdapter = new ChatRoomAdapter(getContext(), mListRoom);
        mRecyclerView.setAdapter(mAdapter);
    }

    @SuppressLint("CheckResult")
    private void initData() {
        loadData(true);
    }

    @SuppressLint("CheckResult")
    private void loadData(final boolean refresh) {
        Observable.fromCallable(new Callable<List<HostedRoom>>() {
            @Override
            public List<HostedRoom> call() throws Exception {
                return SmackManager.getInstance().getHostedRooms();
            }
        })
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(new Consumer<List<HostedRoom>>() {
            @Override
            public void accept(List<HostedRoom> listRoom) throws Exception {
                if (listRoom == null || listRoom.size() == 0) {
                    return;
                }

                if (refresh) {
                    mListRoom.clear();
                }

                MultiUserChatManager manager = MultiUserChatManager.getInstanceFor(SmackManager.getInstance().getConnection());
                for (HostedRoom room : listRoom) {
                    RoomInfo info = null;
                    try {
                        info = manager.getRoomInfo(room.getJid());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    mListRoom.add(info);
                }

                mAdapter.notifyDataSetChanged();

                if (refresh) {
                    mSRLayout.finishRefresh(1000/*,false*/);//传入false表示刷新失败
                } else {
                    mSRLayout.finishLoadMore(1000/*,false*/);//传入false表示加载失败
                }
            }
        });
    }
}
