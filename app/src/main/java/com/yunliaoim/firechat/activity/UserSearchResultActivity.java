package com.yunliaoim.firechat.activity;

import android.annotation.SuppressLint;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

import com.orhanobut.logger.Logger;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.yunliaoim.firechat.R;
import com.yunliaoim.firechat.activity.base.IMBaseActivity;
import com.yunliaoim.firechat.adapter.ChatRoomAdapter;
import com.yunliaoim.firechat.adapter.CheckableContactAdapter;
import com.yunliaoim.firechat.bean.CheckableContactEntity;
import com.yunliaoim.firechat.decoration.ContactItemDecoration;
import com.yunliaoim.firechat.smack.SmackListenerManager;
import com.yunliaoim.firechat.smack.SmackManager;
import com.yunliaoim.firechat.util.IMUtil;
import com.yunliaoim.firechat.util.LoginHelper;
import com.yunliaoim.firechat.utils.util.UIUtil;

import org.jivesoftware.smack.roster.RosterEntry;
import org.jivesoftware.smackx.muc.HostedRoom;
import org.jivesoftware.smackx.muc.MultiUserChat;
import org.jivesoftware.smackx.muc.MultiUserChatManager;
import org.jivesoftware.smackx.muc.RoomInfo;
import org.jivesoftware.smackx.search.ReportedData;
import org.jivesoftware.smackx.search.UserSearchManager;
import org.jivesoftware.smackx.xdata.Form;
import org.jxmpp.jid.DomainBareJid;
import org.jxmpp.jid.impl.JidCreate;
import org.jxmpp.jid.parts.Resourcepart;
import org.jxmpp.stringprep.XmppStringprepException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.ittiger.indexlist.IndexStickyView;
import cn.ittiger.indexlist.listener.OnItemClickListener;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * 用户搜索结果
 */
public class UserSearchResultActivity extends IMBaseActivity {
    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    @BindView(R.id.toolbarTitle)
    TextView mToolbarTitle;

    @BindView(R.id.refreshLayout)
    SmartRefreshLayout mSRLayout;

    @BindView(R.id.recyclerView)
    RecyclerView mRecyclerView;

    List<RoomInfo> mListRoom;
    ChatRoomAdapter mAdapter;

    private GestureDetector mGestureDetector;

    DomainBareJid mUserSearchService = null;

    public static void start(Context context, String keyword) {
        Intent intent = new Intent(context, UserSearchResultActivity.class);
        intent.putExtra("keyword", keyword);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        setContentView(R.layout.activity_user_search_result);

        ButterKnife.bind(this);

        initView();
        initData();
    }

    private void initView() {
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);//不显示ToolBar的标题
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mToolbarTitle.setText(getString(R.string.title_user_search));
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

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

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));

        mGestureDetector = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public void onLongPress(MotionEvent e) {
                super.onLongPress(e);
            }

            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                View childView = mRecyclerView.findChildViewUnder(e.getX(),e.getY());
                if(childView != null){
                    int position = mRecyclerView.getChildLayoutPosition(childView);

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
        mAdapter = new ChatRoomAdapter(this, mListRoom);
        mRecyclerView.setAdapter(mAdapter);
    }

    @SuppressLint("CheckResult")
    private void initData() {
        loadData(true);
    }

    @SuppressLint("CheckResult")
    private void loadData(final boolean refresh) {
        String keyword = getIntent().getStringExtra("keyword");
        try {
            mUserSearchService = JidCreate.domainBareFrom("search.yunliaoim.com");

            ReportedData data;
            UserSearchManager usersearchManager = new UserSearchManager(SmackManager.getInstance().getConnection());

            Form f = usersearchManager.getSearchForm(mUserSearchService);

            Form answer = f.createAnswerForm();
            answer.setAnswer("Name", true);
            answer.setAnswer("Email", true);
            answer.setAnswer("Username", true);
            answer.setAnswer("search", keyword);

            data = usersearchManager.getSearchResults(answer, mUserSearchService);
            List<ReportedData.Row> rows = data.getRows();
            List<ReportedData.Column> columns = data.getColumns();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
