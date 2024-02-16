package com.example.template;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

public class inforAdapter extends BaseAdapter {

    Context context;
    int layout;
    List<Information> inforList;

    public inforAdapter(Context context, int layout, List<Information> inforList) {
        this.context = context;
        this.layout = layout;
        this.inforList = inforList;
    }

    @Override
    public int getCount() {
        return inforList.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView = inflater.inflate(layout, null);

        TextView txtID = convertView.findViewById(R.id.txtID);
        TextView txtName = convertView.findViewById(R.id.txtName);
        TextView txtDate = convertView.findViewById(R.id.txtDate);
        TextView txtTime = convertView.findViewById(R.id.txtTime);

        txtID.setText(inforList.get(position).ID);
        txtName.setText(inforList.get(position).name);
        txtDate.setText(inforList.get(position).date);
        txtTime.setText(inforList.get(position).time);

        return convertView;
    }
}
