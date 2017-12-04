package com.zyl.mp3cutter.other;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.jaeger.library.StatusBarUtil;
import com.zyl.mp3cutter.R;
import com.zyl.mp3cutter.common.app.di.AppComponent;
import com.zyl.mp3cutter.common.base.BaseActivity;
import com.zyl.mp3cutter.home.ui.HomeFragment;


public class MainActivity extends BaseActivity {
    private Toolbar mToolBar;
    private DrawerLayout mDrawerLayout;
    private NavigationView mNavigationView;
    Fragment mCurFragment;
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
        switchToHomePage();
        StatusBarUtil.setColorForDrawerLayout(MainActivity.this,
                mDrawerLayout, Color.TRANSPARENT);
    }

    private void initView() {
        initToolBar();
        initDrawer();
        initNavigationView();
    }

    private void initToolBar() {
        mToolBar = (Toolbar) findViewById(R.id.tb_custom);
        mToolBar.setTitle("主页");
        mToolBar.setTitleTextColor(Color.parseColor("#ffffff"));
        setSupportActionBar(mToolBar);
    }

    private void initDrawer() {
        mDrawerLayout = (DrawerLayout) findViewById(R.id.dl_left);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, mDrawerLayout,
                mToolBar, R.string.app_name, R.string.app_name);
        mDrawerLayout.addDrawerListener(toggle);
        toggle.syncState();
    }

    private void initNavigationView() {
        mNavigationView = (NavigationView) findViewById(R.id.nav_view);
        mNavigationView.setItemIconTintList(null);
        mNavigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
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
                mDrawerLayout.closeDrawers();
                return true;
            }
        });
    }

    private void switchToSetting() {
        getSupportFragmentManager().beginTransaction().replace(R.id.frame_content, new SettingFragment()).commit();
        mToolBar.setTitle(getResources().getString(R.string.main_tab_setting));
    }

    private void switchToHomePage() {
        getSupportFragmentManager().beginTransaction().replace(R.id.frame_content, HomeFragment.newInstance()).commit();
        mToolBar.setTitle(getResources().getString(R.string.main_tab_home));
    }

    private void switchToAbout() {
        getSupportFragmentManager().beginTransaction().replace(R.id.frame_content, new AboutFragment()).commit();
        mToolBar.setTitle(getResources().getString(R.string.main_tab_about));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_menu, menu);

        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        mCurFragment = getSupportFragmentManager().findFragmentById(R.id.frame_content);
        if(mCurFragment instanceof HomeFragment){
            menu.findItem(R.id.home_item_open).setVisible(true);
            menu.findItem(R.id.home_item_voice).setVisible(true);
        }
        else{
            menu.findItem(R.id.home_item_open).setVisible(false);
            menu.findItem(R.id.home_item_voice).setVisible(false);
        }
        Log.d(TAG, "onPrepareOptionsMenu: visible"+ (mCurFragment instanceof HomeFragment));
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
}
