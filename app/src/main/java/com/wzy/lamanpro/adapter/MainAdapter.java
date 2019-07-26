package com.wzy.lamanpro.adapter;

import android.content.Context;
import android.widget.TextView;

import com.wzy.lamanpro.R;
import com.wzy.lamanpro.bean.GFs;
import com.wzy.lamanpro.bean.ProductData;
import com.wzy.lamanpro.utils.WZYBaseAdapter;

import java.util.List;

public class MainAdapter extends WZYBaseAdapter<GFs> {
    public MainAdapter(List<GFs> data, Context context, int layoutRes) {
        super(data, context, layoutRes);
    }

    @Override
    public void bindData(ViewHolder holder, GFs gFs, int indexPostion) {
        TextView name = (TextView) holder.getView(R.id.name);
        TextView point = (TextView) holder.getView(R.id.point);
        TextView point_now = (TextView) holder.getView(R.id.point_now);
        name.setText(gFs.getName());
        point.setText(String.valueOf(gFs.getPoint()));
        point_now.setText(String.valueOf(gFs.getPoint_now()));
//        email.setText(users.getEmail());

    }
}
