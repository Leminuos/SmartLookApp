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
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class infoFrintActivity extends AppCompatActivity {
    private ListView listView;
    private ArrayList<Information> listData;
    private inforAdapter adapter;
    private DatabaseReference dataRef;
    private boolean isShowingErrorMessage = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_frint);

        initUi();
        initListener();
    }

    private void initListener() {
        dataRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                listData.clear();

                for(DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String ID = snapshot.child("ID").getValue(String.class);
                    String name = snapshot.child("Name").getValue(String.class);
                    String date = snapshot.child("Date created").getValue(String.class);
                    String time = snapshot.child("Time created").getValue(String.class);
                    listData.add(new Information(ID, name, time, date));
                }

                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Xử lý khi có lỗi xảy ra
                Log.e("Firebase", "Error: " + databaseError.getMessage());
            }
        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                openFeedbackDialog(position, listData.get(position));
                return false;
            }
        });
    }

    private void initUi() {
        listView = findViewById(R.id.listviewInfo);
        dataRef = FirebaseDatabase.getInstance().getReference().child("Finger");

        listData = new ArrayList<>();
        adapter = new inforAdapter(this, R.layout.information_background, listData);
        listView.setAdapter(adapter);
    }

    private void openFeedbackDialog(int position, Information info) {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.feeback_layout);

        Window window = dialog.getWindow();
        if(window == null) {
            return;
        }

        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        WindowManager.LayoutParams windowAttributes = window.getAttributes();
        windowAttributes.gravity = Gravity.CENTER;
        window.setAttributes(windowAttributes);

        EditText edtName = dialog.findViewById(R.id.edt_name);
        CardView btnSave = dialog.findViewById(R.id.btnSave);

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newName = edtName.getText().toString().trim();
                Handler handler = new Handler();

                if(newName.equals(""))
                {
                    if(!isShowingErrorMessage) {
                        Toast.makeText(infoFrintActivity.this, "Vui lòng nhập tên bạn muốn thay đổi",
                                Toast.LENGTH_SHORT).show();
                        isShowingErrorMessage = true;
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                isShowingErrorMessage = false;
                            }
                        }, 2000);
                    }

                    return;
                }
                else if(newName.equals(info.name)) {
                    if(!isShowingErrorMessage) {
                        Toast.makeText(infoFrintActivity.this, "Tên mới trùng với tên cũ. Vui lòng nhập lại!",
                                Toast.LENGTH_SHORT).show();
                        isShowingErrorMessage = true;
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                isShowingErrorMessage = false;
                            }
                        }, 2000);
                    }

                    return;
                }

                dataRef.child(info.ID).child("Name").setValue(newName);
                if(!isShowingErrorMessage) {
                    Toast.makeText(infoFrintActivity.this, "Đã thay đổi " + info.name + " thành " + newName,
                            Toast.LENGTH_SHORT).show();
                    isShowingErrorMessage = true;
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            isShowingErrorMessage = false;
                        }
                    }, 2000);
                }

                dialog.dismiss();
            }
        });

        dialog.show();
    }

    private void openConfirmDialog(int position, String ID) {
        View view = LayoutInflater.from(infoFrintActivity.this).inflate(R.layout.confirm_layout, null);
        AlertDialog.Builder confirm = new AlertDialog.Builder(infoFrintActivity.this);
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
                listData.remove(position);

                dataRef.child(ID).removeValue(new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(DatabaseError error, DatabaseReference ref) {
                        Toast.makeText(infoFrintActivity.this, "Đã xoá ID " + ID + " thành công.",
                                Toast.LENGTH_SHORT).show();
                    }
                });

                adapter.notifyDataSetChanged();
                alertDialog.dismiss();
            }
        });

        alertDialog.show();
    }
}