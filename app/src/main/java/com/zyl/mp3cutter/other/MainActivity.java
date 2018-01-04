package com.zyl.mp3cutter.other;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;

import com.jaeger.library.StatusBarUtil;
import com.orhanobut.logger.Logger;
import com.zyl.mp3cutter.R;
import com.zyl.mp3cutter.common.app.di.AppComponent;
import com.zyl.mp3cutter.common.base.BaseActivity;
import com.zyl.mp3cutter.common.base.BasePresenter;
import com.zyl.mp3cutter.common.base.IBaseView;
import com.zyl.mp3cutter.databinding.ActivityMainBinding;
import com.zyl.mp3cutter.home.ui.HomeFragment;


public class MainActivity extends BaseActivity<IBaseView, BasePresenter<IBaseView>, ActivityMainBinding> {
    private Fragment mCurFragment;
    private Fragment mSettingFragment;
    private Fragment mHomeFragment;
    private Fragment mAboutFragment;
    private static final String TAG = "MainActivity";

    @Override
    protected void ComponentInject(AppComponent appComponent) {

    }

    @Override
    protected int initLayoutResId() {
        return R.layout.activity_main;
    }

    @Override
    protected void initData(Bundle savedInstanceState) {
        initView();
        mSettingFragment = new SettingFragment();
        mHomeFragment = new HomeFragment();
        mAboutFragment = new AboutFragment();
        switchToHomePage();
        StatusBarUtil.setColorForDrawerLayout(MainActivity.this,
                mDataBinding.drawerlayout, Color.TRANSPARENT);
    }

    private void initView() {
        initToolBar();
        initDrawer();
        initNavigationView();
    }

    private void initToolBar() {
        mDataBinding.toolbar.setTitle(getResources().getString(R.string.main_tab_home));
        mDataBinding.toolbar.setTitleTextColor(Color.parseColor("#ffffff"));
        setSupportActionBar(mDataBinding.toolbar);
    }

    private void initDrawer() {
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, mDataBinding.drawerlayout,
                mDataBinding.toolbar, R.string.app_name, R.string.app_name);
        mDataBinding.drawerlayout.addDrawerListener(toggle);
        toggle.syncState();
    }

    private void initNavigationView() {
        mDataBinding.navigationView.setItemIconTintList(null);
        mDataBinding.navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.item_about:
                        switchToAbout();
                        break;
                    case R.id.item_home:
                        switchToHomePage();
                        break;
                    case R.id.item_setting:
                        switchToSetting();
                        break;
                }
                invalidateOptionsMenu();
                menuItem.setChecked(true);
                mDataBinding.drawerlayout.closeDrawers();
                return true;
            }
        });
    }

    private void switchToSetting() {
        mCurFragment = mSettingFragment;
        getSupportFragmentManager().beginTransaction().replace(R.id.frame_content, mSettingFragment).commit();
        mDataBinding.toolbar.setTitle(getResources().getString(R.string.main_tab_setting));
    }

    private void switchToHomePage() {
        mCurFragment = mHomeFragment;
        getSupportFragmentManager().beginTransaction().replace(R.id.frame_content, mHomeFragment).commit();
        mDataBinding.toolbar.setTitle(getResources().getString(R.string.main_tab_home));
    }

    private void switchToAbout() {
        mCurFragment = mAboutFragment;
        getSupportFragmentManager().beginTransaction().replace(R.id.frame_content, mAboutFragment).commit();
        mDataBinding.toolbar.setTitle(getResources().getString(R.string.main_tab_about));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
//        mCurFragment = getSupportFragmentManager().findFragmentById(R.id.frame_content);
        if(mCurFragment instanceof HomeFragment){
            menu.findItem(R.id.home_item_open).setVisible(true);
            menu.findItem(R.id.home_item_voice).setVisible(true);
        }
        else{
            menu.findItem(R.id.home_item_open).setVisible(false);
            menu.findItem(R.id.home_item_voice).setVisible(false);
        }
        Logger.d("onPrepareOptionsMenu: visible"+ (mCurFragment instanceof HomeFragment));
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.home_item_voice:
                if(mCurFragment instanceof HomeFragment){
                    ((HomeFragment)mCurFragment).voicePanelAnimation();
                }
                break;
            case R.id.home_item_open:
                if(mCurFragment instanceof HomeFragment){
                    ((HomeFragment)mCurFragment).openFile();
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * 监听后退退出
     *
     * @param keyCode 按键码
     * @param event   时间
     * @return 是否处理完成
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            moveTaskToBack(false);
        }
        return super.onKeyDown(keyCode, event);
    }
}
