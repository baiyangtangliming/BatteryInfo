package com.liming.batteryinfo.adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.liming.batteryinfo.R;
import com.liming.batteryinfo.entity.ToolBean;

import java.util.List;


public class ToolAdapter extends BaseAdapter {

    private Context context;
    private List<ToolBean> list;

    public ToolAdapter(Context context, List<ToolBean> list) {
        super();
        this.context = context;
        this.list = list;
    }

    @Override
    public int getCount() {
        if (list != null) {
            return list.size();
        }
        return 0;
    }

    @Override
    public Object getItem(int position) {
        if (list != null) {
            return list.get(position);
        }
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHold hold;
        if (convertView == null) {
            hold = new ViewHold();
            convertView = LayoutInflater.from(context).inflate(R.layout.item, null);
            hold.itemTool = convertView.findViewById(R.id.txt_tool);
            hold.itemTip = convertView.findViewById(R.id.txt_tool_tip);
            hold.itemNum = convertView.findViewById(R.id.txt_tool_num);
            Typeface mtypeface = Typeface.createFromFile("/system/fonts/Roboto-Thin.ttf");
            hold.itemNum.setTypeface(mtypeface);
            convertView.setTag(hold);
        } else {
            hold = (ViewHold) convertView.getTag();
        }

        hold.itemTool.setText(list.get(position).getItemTool());
        hold.itemTip.setText(list.get(position).getItemTip());
        hold.itemNum.setText(list.get(position).getItemNum());
        return convertView;
    }

    static class ViewHold {
        TextView itemTool;
        TextView itemTip;
        TextView itemNum;

    }

}
