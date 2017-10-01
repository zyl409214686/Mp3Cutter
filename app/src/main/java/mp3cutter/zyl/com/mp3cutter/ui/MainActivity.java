package mp3cutter.zyl.com.mp3cutter.ui;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.Toast;

import com.jaeger.library.StatusBarUtil;

import mp3cutter.zyl.com.mp3cutter.R;
import mp3cutter.zyl.com.mp3cutter.home.ui.HomeFragment;

public class MainActivity extends AppCompatActivity {
    private Toolbar mToolBar;
    private DrawerLayout mDrawerLayout;
    private NavigationView mNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toast.makeText(this, "main", Toast.LENGTH_LONG).show();
        initView();
        switchToCutterPage();
//        StatusBarUtil.setTransparent(HomeActivity.this);
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
//                        switchToFolder();
                        switchToCutterPage();
                        break;
                    case R.id.item_setting:
                        switchToSetting();
                        break;
                }
                menuItem.setChecked(true);
                mDrawerLayout.closeDrawers();
                return true;
            }
        });
    }

    private void switchToSetting() {
        getSupportFragmentManager().beginTransaction().replace(R.id.frame_content, new SettingFragment()).commit();
        mToolBar.setTitle("个人设置~");
    }

    private void switchToFolder() {
        getSupportFragmentManager().beginTransaction().replace(R.id.frame_content, new FolderFragment()).commit();
        mToolBar.setTitle("选择目录~~");
    }

    private void switchToCutterPage() {
        getSupportFragmentManager().beginTransaction().replace(R.id.frame_content, HomeFragment.newInstance("", "")).commit();
        mToolBar.setTitle("音乐播放~~~");
    }

    private void switchToAbout() {
        getSupportFragmentManager().beginTransaction().replace(R.id.frame_content, new AboutFragment()).commit();
        mToolBar.setTitle("关于~~~~");
    }
}
