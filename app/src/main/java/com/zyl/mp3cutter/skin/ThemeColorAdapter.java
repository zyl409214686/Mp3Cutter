package com.zyl.mp3cutter.skin;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.zyl.mp3cutter.R;
import com.zyl.mp3cutter.common.base.EasyRecyclerViewAdapter;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Description: 颜色选择adapter
 * Created by zouyulong on 2018/1/9.
 */

public class ThemeColorAdapter extends EasyRecyclerViewAdapter<ThemeColor> {
    private int position;

    @Override
    public RecyclerView.ViewHolder onCreate(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_theme_color, parent, false);
        return new ThemeColorViewHolder(view);
    }

    @Override
    public void onBind(final RecyclerView.ViewHolder viewHolder, final int RealPosition, ThemeColor data) {
        ((ThemeColorViewHolder) viewHolder).them_color.setImageResource(data.getDrawableResId());
        if (data.isChosen()) {
            ((ThemeColorViewHolder) viewHolder).chosen.setVisibility(View.VISIBLE);
            position=RealPosition;
        } else {
            ((ThemeColorViewHolder) viewHolder).chosen.setVisibility(View.GONE);
        }
    }


    class ThemeColorViewHolder extends EasyViewHolder {
        CircleImageView them_color;
        ImageView chosen;

        public ThemeColorViewHolder(View itemView) {
            super(itemView);
            them_color = (CircleImageView) itemView.findViewById(R.id.them_color);
            chosen = (ImageView) itemView.findViewById(R.id.choose);
        }
    }

    public int getPosition() {
        return position;
    }
}
