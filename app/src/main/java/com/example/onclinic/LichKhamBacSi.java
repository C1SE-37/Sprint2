package com.example.onclinic;

import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.adapter.LichKhamAdapter;
import com.example.local_data.DataLocalManager;
import com.example.model.LichKham;
import com.example.sqlhelper.NgayGio;
import com.example.sqlhelper.NoteFireBase;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

public class LichKhamBacSi extends AppCompatActivity {

    private RecyclerView rcvLichKham;
    private LichKhamAdapter lichKhamAdapter;
    private List<LichKham> listLichKham;

    String idPhongKham;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lich_kham_bac_si);
        idPhongKham = DataLocalManager.getIDPhongKham();
        layDanhSachLichKhamTuFireBase();

        addControls();
    }


    private void layDanhSachLichKhamTuFireBase() {
        DatabaseReference myRef = FirebaseDatabase.getInstance(NoteFireBase.firebaseSource).getReference().child(NoteFireBase.PHONGKHAM);
        DatabaseReference refSuatKham = myRef.child(idPhongKham).child(NoteFireBase.LICHKHAM);
        refSuatKham.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listLichKham.clear();
                for(DataSnapshot data : snapshot.getChildren())
                {
                    LichKham lichKham = data.getValue(LichKham.class);
                    try {
                        Date ngayFirebase = NgayGio.ConvertStringToDate(lichKham.getNgayKham());
                        //String today = sdf.format(Calendar.getInstance().getTime());
                        Date ngayHienTai = NgayGio.GetDateCurrent();
                        //so sánh ngày trong lịch và hiện tại để thêm vào lịch khám
                        if(ngayFirebase.getTime() >= ngayHienTai.getTime())
                        {
                            listLichKham.add(lichKham);
                            Collections.sort(listLichKham, LichKham.LichKhamDateAsendingComparator);//sắp xếp ngày tăng dần
                        }
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
                lichKhamAdapter.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(LichKhamBacSi.this,"Lỗi đọc dữ liệu",Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(lichKhamAdapter!= null)
            lichKhamAdapter.release();
    }

    private void addControls() {
        rcvLichKham = findViewById(R.id.lvLichKham);
        LinearLayoutManager layoutManager = new LinearLayoutManager(LichKhamBacSi.this);
        rcvLichKham.setLayoutManager(layoutManager);

        listLichKham = new ArrayList<>();
        lichKhamAdapter = new LichKhamAdapter(listLichKham, LichKhamBacSi.this);

        rcvLichKham.setAdapter(lichKhamAdapter);
    }
}