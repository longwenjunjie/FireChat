package com.yunliaoim.firechat.activity;

import butterknife.BindView;
import butterknife.ButterKnife;
import com.yunliaoim.firechat.R;
import com.yunliaoim.firechat.activity.base.IMBaseActivity;
import com.yunliaoim.firechat.adapter.CheckableContactAdapter;
import com.yunliaoim.firechat.decoration.ContactItemDecoration;
import com.yunliaoim.firechat.bean.CheckableContactEntity;
import com.yunliaoim.firechat.smack.SmackListenerManager;
import com.yunliaoim.firechat.smack.SmackManager;
import com.yunliaoim.firechat.util.IMUtil;
import com.yunliaoim.firechat.util.LoginHelper;
import com.yunliaoim.firechat.utils.util.UIUtil;
import cn.ittiger.indexlist.IndexStickyView;
import cn.ittiger.indexlist.listener.OnItemClickListener;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

import com.orhanobut.logger.Logger;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.roster.RosterEntry;
import org.jivesoftware.smackx.muc.MultiUserChat;
import org.jivesoftware.smackx.muc.MultiUserChatManager;
import org.jivesoftware.smackx.muc.RoomInfo;
import org.jxmpp.jid.impl.JidCreate;
import org.jxmpp.stringprep.XmppStringprepException;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * 创建群聊界面
 */
public class CreateMultiChatActivity extends IMBaseActivity
        implements Toolbar.OnMenuItemClickListener {
    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    @BindView(R.id.toolbarTitle)
    TextView mToolbarTitle;

    @BindView(R.id.indexStickyView)
    IndexStickyView mIndexStickyView;

    private MenuItem mMenuItem;
    private CheckableContactAdapter mAdapter;
    private List<CheckableContactEntity> mCheckList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_create_multi_chat);
        ButterKnife.bind(this);
        initToolBar();
        mIndexStickyView.addItemDecoration(new ContactItemDecoration());
    }

    private void initToolBar() {
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);//不显示ToolBar的标题
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mToolbarTitle.setText(R.string.text_create_multi_chat);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                onBackPressed();
            }
        });
        mToolbar.setOnMenuItemClickListener(this);
    }

    @SuppressLint("CheckResult")
    @Override
    public void refreshData() {
        startRefresh();
        Observable.create(new ObservableOnSubscribe<List<CheckableContactEntity>>() {
            @Override
            public void subscribe(ObservableEmitter<List<CheckableContactEntity>> subscriber) throws Exception {

                Set<RosterEntry> friends = SmackManager.getInstance().getAllFriends();
                List<CheckableContactEntity> list = new ArrayList<>();
                for (RosterEntry friend : friends) {
                    list.add(new CheckableContactEntity(friend));
                }
                subscriber.onNext(list);
                subscriber.onComplete();
            }
        })
        .subscribeOn(Schedulers.io())//指定上面的Subscriber线程
        .observeOn(AndroidSchedulers.mainThread())//指定下面的回调线程
        .doOnError(new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {

                refreshFailed();
                Logger.e(throwable, "create multi chat query contact list failure");
            }
        })
        .subscribe(new Consumer<List<CheckableContactEntity>>() {
            @Override
            public void accept(List<CheckableContactEntity> contacts) throws Exception {
                if(mAdapter == null) {
                    mAdapter = new CheckableContactAdapter(mActivity, contacts);
                    mAdapter.setOnItemClickListener(mContactItemClickListener);
                    mIndexStickyView.setAdapter(mAdapter);
                } else {
                    mAdapter.reset(contacts);
                }
                refreshSuccess();
            }
        });
    }

    OnItemClickListener<CheckableContactEntity> mContactItemClickListener = new OnItemClickListener<CheckableContactEntity>() {

        @Override
        public void onItemClick(View childView, int position, CheckableContactEntity item) {

            item.setChecked(!item.isChecked());
            mAdapter.notifyItemChanged(position);
            if(item.isChecked()) {
                mCheckList.add(item);
            } else {
                mCheckList.remove(item);
            }
            if(mCheckList.size() > 1) {
                mMenuItem.setEnabled(true);
            } else {
                mMenuItem.setEnabled(false);
            }
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.create_multi_chat_toolbar_menu, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        mMenuItem = menu.findItem(R.id.toolbar_create_multi_chat_finish);
        mMenuItem.setEnabled(false);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.toolbar_create_multi_chat_finish:
                createMultiChat();
                break;
            default:
                break;
        }
        return true;
    }

    /**
     * 创建多人聊天
     */
    private void createMultiChat() {
        final EditText inputServer = new EditText(this);
        final String meUserName = LoginHelper.getUser().getUsername();
        inputServer.setText(String.format(getString(R.string.text_default_multi_chat_nickname), meUserName));

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("群聊名称").setIcon(android.R.drawable.ic_dialog_info).setView(inputServer)
                .setNegativeButton("取消", null);
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                String roomName = inputServer.getText().toString();

                MultiUserChatManager manager = MultiUserChatManager.getInstanceFor(SmackManager.getInstance().getConnection());
                RoomInfo info = null;
                try {
                    info = manager.getRoomInfo(JidCreate.entityBareFrom(roomName + "@conference.yunliaoim.com"));
                } catch (Exception e) {
                    e.printStackTrace();
                }

                if (info != null) {
                    UIUtil.showToast(CreateMultiChatActivity.this, getString(R.string.text_multi_chat_exist));
                    return;
                }

                String reason = String.format(getString(R.string.text_invite_to_multi_chat), meUserName);
                try {
                    MultiUserChat multiUserChat = SmackManager.getInstance().createChatRoom(roomName, meUserName, null);
                    for(CheckableContactEntity entity : mCheckList) {
                        String jid = SmackManager.getInstance().getFullJid(entity.getRosterEntry().getUser());
                        multiUserChat.invite(JidCreate.entityBareFromOrNull(jid), reason);//邀请入群
                    }
                    SmackListenerManager.addMultiChatMessageListener(multiUserChat);
                    IMUtil.startChatRoomActivity( CreateMultiChatActivity.this, multiUserChat.getRoom().getLocalpart().toString());
                    finish();
                } catch (Exception e) {
                    Logger.e(e, "invite friend to chatRoom failure ");
                    UIUtil.showToast(mActivity, R.string.text_create_multi_chat_failure);
                }
            }
        });
        builder.show();
    }

    @Override
    public boolean isLceActivity() {
        return true;
    }
}
