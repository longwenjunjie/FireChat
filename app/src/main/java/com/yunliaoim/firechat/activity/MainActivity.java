package com.yunliaoim.firechat.activity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.support.v4.widget.DrawerLayout;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TabHost;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import com.yunliaoim.firechat.R;
import com.yunliaoim.firechat.activity.base.IMBaseActivity;
import com.yunliaoim.firechat.fragment.ChatRoomFragment;
import com.yunliaoim.firechat.fragment.ContactFragment;
import com.yunliaoim.firechat.fragment.MessageFragment;
import com.yunliaoim.firechat.smack.SmackListenerManager;
import com.yunliaoim.firechat.smack.SmackManager;
import com.yunliaoim.firechat.ui.FragmentSaveStateTabHost;
import com.yunliaoim.firechat.util.IntentHelper;
import com.yunliaoim.firechat.util.LoginHelper;
import com.yunliaoim.firechat.util.ShareHelper;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * 主页面
 */
public class MainActivity extends IMBaseActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        TabHost.OnTabChangeListener, Toolbar.OnMenuItemClickListener {
    static {
        /**
         * 此方法必须必须引用appcompat-v7:23.4.0
         *
         * Button类控件使用vector必须使用selector进行包装才会起作用，不然会crash
         * 并且使用selector时必须调用下面的方法进行设置，否则也会crash
         * */
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }

    private static final Class[] TABBAR_CLASSES = {MessageFragment.class, ContactFragment.class, ChatRoomFragment.class};
    private static final int[] TABBAR_DRAWABLES = {R.drawable.ic_tabbar_message, R.drawable.ic_tabbar_contact, R.drawable.ic_tabbar_chatroom};
    private static final int[] TABBAR_NAMES = {R.string.text_message, R.string.text_contact, R.string.text_chatroom};
    private static final int[] TABBAR_TAGS = {R.string.text_message, R.string.text_contact, R.string.text_chatroom};

    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    @BindView(R.id.toolbarTitle)
    TextView mToolbarTitle;

    @BindView(R.id.drawer_layout)
    DrawerLayout mDrawerLayout;

    @BindView(R.id.nav_container)
    NavigationView mNavigationView;

//    @BindView(R.id.nav_header_user)
//    TextView mTvName;

    @BindView(android.R.id.tabhost)
    FragmentSaveStateTabHost mTabHost;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
//        ButterKnife.bind(R.layout.activity_main_nav_header, mNavigationView.getHeaderView(0));

        initToolbar();
        initTabHost();

        //普通消息接收监听
        SmackListenerManager.addGlobalListener();
    }

    private void initToolbar() {
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);//不显示ToolBar的标题
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, mDrawerLayout, mToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawerLayout.setDrawerListener(toggle);
        toggle.syncState();

        mNavigationView.setNavigationItemSelectedListener(this);
        mToolbar.setNavigationIcon(R.drawable.ic_toolbar_avatar);
        mToolbar.setOnMenuItemClickListener(this);

        mDrawerLayout.addDrawerListener(new NavDrawerListener());

        View ll = mNavigationView.getHeaderView(0);

        TextView tvName = ll.findViewById(R.id.nav_header_user);
        tvName.setText(LoginHelper.getUser().getUsername());

        tvName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AccountMngActivity.start(MainActivity.this);
            }
        });
    }

    /**
     * 主页底部Tab
     */
    private void initTabHost() {
        mTabHost.setup(this, getSupportFragmentManager(), R.id.tabItemContent);
        mTabHost.getTabWidget().setDividerDrawable(new ColorDrawable(Color.TRANSPARENT));
        mTabHost.setOnTabChangedListener(this);

        for (int i = 0; i < TABBAR_CLASSES.length; i++) {
            TabHost.TabSpec tabSpec = mTabHost.newTabSpec(getString(TABBAR_TAGS[i]))
                    .setIndicator(getTabHostIndicator(i));
            mTabHost.addTab(tabSpec, TABBAR_CLASSES[i], null);
        }
    }

    private View getTabHostIndicator(int tabIndex) {
        View view = LayoutInflater.from(this).inflate(R.layout.tabbar_item_view, null);

        TextView tabName = ButterKnife.findById(view, R.id.tabbar_name);
        tabName.setText(TABBAR_NAMES[tabIndex]);

        ImageView tabIcon = ButterKnife.findById(view, R.id.tabbar_icon);
        tabIcon.setBackgroundResource(TABBAR_DRAWABLES[tabIndex]);
        return view;
    }

    @Override
    public void onTabChanged(String tabId) {
        mToolbarTitle.setText(tabId);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        if(item.isChecked()) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
            return true;
        }

        int id = item.getItemId();
        switch (id) {
            case R.id.nav_share:
                ShareHelper.shareApp(mActivity);
                break;
            case R.id.nav_about:
                mActivity.startActivity(new Intent(mActivity, AboutActivity.class));
                break;
        }
        mDrawerLayout.closeDrawer(GravityCompat.START);

        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_toolbar_menu, menu);
        return true;
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.toolbar_add_contact:
                mActivity.startActivity(new Intent(mActivity, AddFriendActivity.class));
                break;
            case R.id.toolbar_create_multi_chat:
                mActivity.startActivity(new Intent(mActivity, CreateMultiChatActivity.class));
                break;
            case R.id.toolbar_user_search:
                UserSearchActivity.start(MainActivity.this);
                break;
            default:
                break;
        }
        return true;
    }

    @Override
    public boolean doubleExitAppEnable() {
        return true;
    }

    @Override
    public void onBackPressed() {
        if(mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    class NavDrawerListener implements DrawerLayout.DrawerListener {

        @Override
        public void onDrawerSlide(View drawerView, float slideOffset) {

        }

        @Override
        public void onDrawerOpened(View drawerView) {

        }

        @Override
        public void onDrawerClosed(View drawerView) {

            int size = mNavigationView.getMenu().size();
            for(int i = 0; i < size; i++) {
                if(mNavigationView.getMenu().getItem(i).isChecked()) {
                    mNavigationView.getMenu().getItem(i).setChecked(false);
                    break;
                }
            }
        }

        @Override
        public void onDrawerStateChanged(int newState) {

        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        EventBus.getDefault().unregister(this);
        SmackListenerManager.getInstance().destroy();
        SmackManager.getInstance().logout();
        SmackManager.getInstance().disconnect();
    }

    @Override
    protected void onStart() {
        super.onStart();

        if(!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onSwitchTabFragmentEvent(Integer index) {
        if(index == IntentHelper.CONTACT_TAB_INDEX || index == IntentHelper.MESSAGE_TAB_INDEX) {
            mTabHost.setCurrentTab(index);
        }
    }
}
