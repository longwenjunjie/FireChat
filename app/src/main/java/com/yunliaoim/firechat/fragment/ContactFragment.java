package com.yunliaoim.firechat.fragment;

import butterknife.BindView;
import butterknife.ButterKnife;
import com.yunliaoim.firechat.R;
import com.yunliaoim.firechat.adapter.ContactAdapter;
import com.yunliaoim.firechat.decoration.ContactItemDecoration;
import com.yunliaoim.firechat.bean.ContactEntity;
import com.yunliaoim.firechat.ui.ChatPromptDialog;
import com.yunliaoim.firechat.smack.SmackManager;
import com.yunliaoim.firechat.utils.base.BaseFragment;
import cn.ittiger.indexlist.IndexStickyView;
import cn.ittiger.indexlist.listener.OnItemClickListener;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

import com.orhanobut.logger.Logger;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.jivesoftware.smack.roster.RosterEntry;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * 联系人列表
 */
public class ContactFragment extends BaseFragment {
    @BindView(R.id.contactRefreshLayout)
    SwipeRefreshLayout mRefreshLayout;

    @BindView(R.id.indexStickyView)
    IndexStickyView mIndexStickyView;

    ContactAdapter mAdapter;

    @Override
    public View getContentView(LayoutInflater inflater, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_contact, null);
        ButterKnife.bind(this, view);
        initView();
        return view;
    }

    private void initView() {
        mIndexStickyView.addItemDecoration(new ContactItemDecoration());
        mRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshData();
            }
        });
    }

    @SuppressLint("CheckResult")
    @Override
    public void refreshData() {
        startRefresh();

        Observable.create(new ObservableOnSubscribe<List<ContactEntity>>() {
            @Override
            public void subscribe(ObservableEmitter<List<ContactEntity>> emitter) throws Exception {
                Set<RosterEntry> friends = SmackManager.getInstance().getAllFriends();
                List<ContactEntity> list = new ArrayList<>();
                for (RosterEntry friend : friends) {
                    list.add(new ContactEntity(friend));
                }
                emitter.onNext(list);
                emitter.onComplete();
            }
        })
        .subscribeOn(Schedulers.io())//指定上面的Subscriber线程
        .observeOn(AndroidSchedulers.mainThread())//指定下面的回调线程
        .doOnError(new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {
                refreshFailed();
                Logger.e(throwable, "query contact list failure");
            }
        })
        .subscribe(new Consumer<List<ContactEntity>>() {
            @Override
            public void accept(List<ContactEntity> contacts) throws Exception {
                if(mAdapter == null) {
                    mAdapter = new ContactAdapter(mContext, contacts);
                    mAdapter.setOnItemClickListener(mContactItemClickListener);
                    mIndexStickyView.setAdapter(mAdapter);
                } else {
                    mAdapter.reset(contacts);
                }
                refreshSuccess();
            }
        });
    }

    OnItemClickListener<ContactEntity> mContactItemClickListener = new OnItemClickListener<ContactEntity>() {
        ChatPromptDialog mDialog;

        @Override
        public void onItemClick(View childView, int position, ContactEntity item) {
            if(mDialog == null) {
                mDialog = new ChatPromptDialog(mContext);
            }
            mDialog.show(item);
        }
    };

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        EventBus.getDefault().register(this);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        EventBus.getDefault().unregister(this);
        mAdapter = null;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onAddContactEntityEvent(ContactEntity event) {
        if(!isRemoving() && mAdapter != null) {
            mAdapter.add(event);
        }
    }

    @Override
    public String getTitle() {
        return getString(R.string.text_contact);
    }

    @Override
    public void refreshFailed() {
        super.refreshFailed();

        mRefreshLayout.setRefreshing(false);
    }

    @Override
    public void refreshSuccess() {
        super.refreshSuccess();

        mRefreshLayout.setRefreshing(false);
    }
}
