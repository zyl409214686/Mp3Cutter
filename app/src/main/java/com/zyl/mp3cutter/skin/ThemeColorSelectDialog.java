package com.zyl.mp3cutter.skin;

import android.content.res.TypedArray;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.GridLayoutManager;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import com.zyl.mp3cutter.R;
import com.zyl.mp3cutter.common.base.EasyRecyclerViewAdapter;
import com.zyl.mp3cutter.common.utils.DensityUtils;
import com.zyl.mp3cutter.databinding.DialogThemeColorBinding;

import java.util.ArrayList;

import skin.support.SkinCompatManager;

/**
 * Description: 主题颜色选择dialog
 * Created by zouyulong on 2018/1/9.
 */

public class ThemeColorSelectDialog extends DialogFragment {
    private ThemeColorAdapter mThemeColorAdapter = new ThemeColorAdapter();
    private ArrayList<ThemeColor> mThemeColorList = new ArrayList<>();
    private LoadSkinListener mListener;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        final Window window = getDialog().getWindow();
        final DisplayMetrics metrics = getResources().getDisplayMetrics();
        final DialogThemeColorBinding binding = DataBindingUtil.inflate(inflater, R.layout.dialog_theme_color,
                ((ViewGroup) window.findViewById(android.R.id.content)), false);
        binding.recyclerTheme.setLayoutManager(new GridLayoutManager(getActivity(), 4));
        init(binding);
        initListener(binding);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));//注意此处
        window.setLayout(metrics.widthPixels - DensityUtils.dp2px(getContext(), 30), -2);
        return binding.getRoot();
    }

    private void init(DialogThemeColorBinding binding){
        TypedArray colors = getResources().obtainTypedArray(R.array.theme_colors);
        String[] names = getResources().getStringArray(R.array.theme_names);
        int titleLength = colors.length();
        mThemeColorList.clear();
        for( int i = 0; i < titleLength; i++ ){
            mThemeColorList.add(new ThemeColor(colors.getResourceId(i, 0)
                    , names[i]));
        }
        mThemeColorAdapter.setDatas(mThemeColorList);
        binding.recyclerTheme.setAdapter(mThemeColorAdapter);
    }

    private void initListener(DialogThemeColorBinding binding){
        mThemeColorAdapter.setOnItemClickListener(new EasyRecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void OnItemClick(View view, int position, Object data) {
                for (ThemeColor themeColor : mThemeColorList) {
                    themeColor.setChosen(false);
                }
                mThemeColorList.get(position).setChosen(true);
                mThemeColorAdapter.notifyDataSetChanged();
            }
        });
        binding.tvSure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SkinCompatManager.getInstance().loadSkin(
                        mThemeColorList.get(mThemeColorAdapter.getPosition()).getName(),
                        new SkinCompatManager.SkinLoaderListener() {
                            @Override
                            public void onStart() {

                            }

                            @Override
                            public void onSuccess() {
                                if(mListener!=null)
                                    mListener.loadSkinSucess();
                            }

                            @Override
                            public void onFailed(String errMsg) {

                            }
                        }, SkinCompatManager.SKIN_LOADER_STRATEGY_BUILD_IN);
                dismiss();
            }
        });
    }

    public void setLoadSkinListener(LoadSkinListener listener){
        mListener = listener;
    }

    public interface LoadSkinListener{
        void loadSkinSucess();
    }
}
