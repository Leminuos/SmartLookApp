package com.example.template;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.text.Layout;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class HistoryActivity extends AppCompatActivity {
    private ListView listView;
    private ArrayList<History> listData;
    private ArrayList<String> listHis;
    private ArrayAdapter<String> adapter;
    private DatabaseReference dataRef;
    private LinearLayout btnDelAll;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        initUi();
        initListener();
    }

    private void initListener() {
        btnDelAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openConfirmDialog();
            }
        });

        Query query = dataRef.orderByKey();

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                listData.clear();
                listHis.clear();

                for(DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String name = snapshot.child("Name").getValue(String.class);
                    String date = snapshot.child("Date").getValue(String.class);
                    String time = snapshot.child("Time").getValue(String.class);
                    String type = snapshot.child("Type").getValue(String.class);
                    String ID = snapshot.child("ID").getValue(Integer.class).toString().trim();
                    String str = "[" + time + " " + date + "] " + name + " mở cửa thông qua " + type + ".";

                    listData.add(0, new History(name, type, time, date, ID));
                    listHis.add(0, str);
                }

                if(listHis.isEmpty())
                    btnDelAll.setVisibility(View.GONE);

                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Xử lý khi có lỗi xảy ra
                Log.e("Firebase", "Error: " + databaseError.getMessage());
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                openInformationDialog(listData.get(position));
            }
        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                dataRef.child(listData.get(position).ID).removeValue();
                listData.remove(position);
                listHis.remove(position);
                adapter.notifyDataSetChanged();
                return true;
            }
        });
    }

    private void initUi() {
        btnDelAll = findViewById(R.id.btnDeleteAll);
        listView = findViewById(R.id.listviewInfo);
        dataRef = FirebaseDatabase.getInstance().getReference().child("History");

        listHis = new ArrayList<>();
        listData = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, listHis);
        listView.setAdapter(adapter);
    }

    private void openInformationDialog(History history) {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.history_layout);

        Window window = dialog.getWindow();
        if(window == null) {
            return;
        }

        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        WindowManager.LayoutParams windowAttributes = window.getAttributes();
        windowAttributes.gravity = Gravity.CENTER;
        window.setAttributes(windowAttributes);

        TextView txtName = dialog.findViewById(R.id.txtNameHisInfo);
        TextView txtType = dialog.findViewById(R.id.txtTypeHisInfo);
        TextView txtDate = dialog.findViewById(R.id.txtDateHisInfo);
        TextView txtTime = dialog.findViewById(R.id.txtTimeHisInfo);

        txtName.setText(history.name);
        txtType.setText(history.type);
        txtDate.setText(history.date);
        txtTime.setText(history.time);

        dialog.show();
    }

    private void openConfirmDialog() {
        View view = LayoutInflater.from(HistoryActivity.this).inflate(R.layout.confirm_layout, null);
        AlertDialog.Builder confirm = new AlertDialog.Builder(HistoryActivity.this);
        confirm.setView(view);

        AlertDialog alertDialog = confirm.create();
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        CardView btnCancel = view.findViewById(R.id.btnCancel);
        CardView btnConfirm = view.findViewById(R.id.btnConfirm);

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });

        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listData.clear();
                listHis.clear();
                dataRef.removeValue();
                adapter.notifyDataSetChanged();
                alertDialog.dismiss();
            }
        });

        alertDialog.show();
    }
}