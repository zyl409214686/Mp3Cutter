package mp3cutter.zyl.com.mp3cutter.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;

import mp3cutter.zyl.com.mp3cutter.R;

/**
 * Created by zouyulong on 16/10/15.
 */

public class MainActivity extends BaseActivity implements View.OnClickListener {
    ImageView mPlayIv, mForwardIv, mBackwardIv, mCutIv;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }

    private void initView(){
        mPlayIv = (ImageView)findViewById(R.id.iv_play);
        mForwardIv = (ImageView)findViewById(R.id.iv_forward);
        mBackwardIv = (ImageView)findViewById(R.id.iv_backward);
        mCutIv = (ImageView)findViewById(R.id.iv_cut);
        mPlayIv.setOnClickListener(this);
        mForwardIv.setOnClickListener(this);
        mBackwardIv.setOnClickListener(this);
        mCutIv.setOnClickListener(this);
        findViewById(R.id.rl_folders).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int viewId = v.getId();
        switch (viewId){
            case R.id.iv_play:
                break;
            case R.id.iv_forward:
                break;
            case R.id.iv_backward:
                break;
            case R.id.iv_cut:
                break;
            case R.id.rl_folders:
                break;
        }
    }
}
