package com.example.cc_food.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cc_food.DAO.OderDAO;
import com.example.cc_food.R;
import com.example.cc_food.modules.OderHistoryModelNew;
import com.example.cc_food.modules.billdetail_paid_model;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class Billdetails_adapter extends RecyclerView.Adapter<Billdetails_adapter.ViewHolder> {
    private ArrayList<OderHistoryModelNew> list;
    private Context context;
    private ArrayList<billdetail_paid_model> arrayList = new ArrayList<>();
    static OderDAO oderDAO;
    OderHistoryModelNew item;


    public Billdetails_adapter(ArrayList<OderHistoryModelNew> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_bill_detail, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        oderDAO = new OderDAO(context);
        holder.txtmadonhang.setText("" + list.get(position).getCode());
        holder.txttrangthai.setText(list.get(position).getStatus());
        holder.ngaymua.setText(list.get(position).getDateTime());
        holder.txttongtien.setText(String.format("%.2f", list.get(position).getTotalFinal()) + " $");
        holder.txttiensanpham.setText(String.format("%.2f", list.get(position).getTotalItem()) + " $");
        holder.txtdeliverybill.setText(String.format("%.2f", list.get(position).getFeeTransport()) + " $");
//        holder.txttaxbill.setText(String.format("%.2f", list.get(position).getTax()) + " $");
        holder.txttensanphamdamua.setText(list.get(position).getListProduct());
        holder.btnthnahtoan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseDatabase database1 = FirebaseDatabase.getInstance();
                DatabaseReference reference1 = database1.getReference("objec_bill");
                reference1.child("" + position).child("status").setValue("Đã thanh toán", new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                        Toast.makeText(context, "update thành công", Toast.LENGTH_SHORT).show();
                        item = new OderHistoryModelNew();

                        item.id = list.get(position).id;
                        item.checkStatus = 2;
                        item.status = "Đã thanh toán";
                        if (oderDAO.updateStatus(item)>0){
                            Toast.makeText(context, "Thanh Toán Thành công !" , Toast.LENGTH_SHORT).show();
                            notifyDataSetChanged();
                        }
                    }
                });
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference reference = database.getReference("object_bill_paid");
                reference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (arrayList != null) {
                            arrayList.clear();
                        }
                        for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                            billdetail_paid_model billdetailmodel1 = snapshot1.getValue(billdetail_paid_model.class);
                            arrayList.add(billdetailmodel1);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
                FirebaseDatabase database2 = FirebaseDatabase.getInstance();
                DatabaseReference reference2 = database2.getReference("object_bill_paid");
                arrayList.add(new billdetail_paid_model(list.get(position).getCode(), list.get(position).getListProduct(), "Đã thanh toán",
                        list.get(position).
                                getDateTime(), list.get(position).getTotalItem(), list.get(position).getFeeTransport(),
                        list.get(position).getTotalFinal()));
                reference2.setValue(arrayList, new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                        Toast.makeText(context, "thành công", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
        FirebaseDatabase database2 = FirebaseDatabase.getInstance();
        DatabaseReference reference2 = database2.getReference("object_bill_paid");
        reference2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (arrayList != null) {
                    arrayList.clear();
                }
                for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                    billdetail_paid_model model = snapshot1.getValue(billdetail_paid_model.class);
                    arrayList.add(model);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        if (list.get(position).getStatus().equals("Đã thanh toán")) {
            holder.btnthnahtoan.setVisibility(View.GONE);
            holder.txttrangthai.setTextColor(Color.GREEN);

            FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
            DatabaseReference databaseReference = firebaseDatabase.getReference("soluongdondaban");
            databaseReference.setValue(arrayList.size(), new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                }
            });
        }
        else {
            holder.txttrangthai.setTextColor(Color.RED);
            holder.btnthnahtoan.setVisibility(View.VISIBLE);
        }
        if (list.get(position).getStatus().equals("Đã huỷ")){
            holder.btnthnahtoan.setVisibility(View.GONE);
        }


    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtmadonhang, ngaymua, txttongtien, txttiensanpham, txtdeliverybill, txttensanphamdamua, txttrangthai;
        ConstraintLayout btnthnahtoan;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtmadonhang = itemView.findViewById(R.id.txtmadonhang1);
            ngaymua = itemView.findViewById(R.id.txtnaymua);
            txttongtien = itemView.findViewById(R.id.total_price_bill);
            txttiensanpham = itemView.findViewById(R.id.price_bill);
            txtdeliverybill = itemView.findViewById(R.id.delivery_bill);
            txttensanphamdamua = itemView.findViewById(R.id.txtcacsanphamdadat);
            txttrangthai = itemView.findViewById(R.id.txttrangthai);
            btnthnahtoan = itemView.findViewById(R.id.thanhtoan);


        }
    }


}