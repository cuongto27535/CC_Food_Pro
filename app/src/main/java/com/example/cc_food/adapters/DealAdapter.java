package com.example.cc_food.adapters;

import static android.content.Context.MODE_PRIVATE;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.example.cc_food.DAO.VoucherDAO;
import com.example.cc_food.R;
import com.example.cc_food.activities.PaymentActivity;
import com.example.cc_food.modules.VoucherModule;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class DealAdapter extends RecyclerView.Adapter<DealAdapter.ViewHolder> {
    private Context context;
    private List<VoucherModule> voucherList;
    static VoucherDAO voucherDAO;
    PaymentActivity paymentActivity;

    public DealAdapter(Context context, List<VoucherModule> voucherList) {
        this.context = context;
        this.voucherList = voucherList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.voucher_item, null);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        voucherDAO = new VoucherDAO(context.getApplicationContext());
        VoucherModule newOb = voucherList.get(position);

        holder.imgVoucher.setImageResource(newOb.img);
        holder.tvID.setText("ID: " + newOb.id);
        holder.tvDiscount.setText(newOb.discount + "");
        holder.tvDeadlineVoucher.setText(newOb.voucherDeadline);


        holder.btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Date dateCurrent = new Date();
                try {
                    Date date = new SimpleDateFormat("yyyy-MM-dd").parse(newOb.voucherDeadline);
                    if (date.after(dateCurrent)) {
                        rememberVoucher(newOb.id, newOb.discount, newOb.voucherDeadline);
                        if (voucherDAO.delete(newOb.id) > 0) {
                            paymentActivity = new PaymentActivity();
                            voucherList.remove(position);
                            notifyDataSetChanged();
                            Toast.makeText(context, "Đã dùng !", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(context, PaymentActivity.class);
                            context.startActivity(intent);
                            Intent intent1 = new Intent(context, PaymentActivity.class);
                            Bundle bundle = new Bundle();
                            bundle.putInt("value", newOb.discount);
                            intent1.putExtra("discount", bundle);
                            paymentActivity.check = 1;
                            context.startActivity(intent1);

                        }
                    }
                    else {
                        Toast.makeText(context, "Voucher đã hết hạn !", Toast.LENGTH_SHORT).show();
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }


            }
        });
    }


    @Override
    public int getItemCount() {
        return voucherList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imgVoucher;
        TextView tvID, tvDiscount, tvDeadlineVoucher;
        Button btnSave;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvID = itemView.findViewById(R.id.tvID);
            imgVoucher = itemView.findViewById(R.id.imgVoucher);
            tvDiscount = itemView.findViewById(R.id.tvDiscount);
            tvDeadlineVoucher = itemView.findViewById(R.id.tvDeadlineVoucher);
            btnSave = itemView.findViewById(R.id.save_voucher);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
        }
    }

    public void rememberVoucher(int id, int discount, String deadline) {
        SharedPreferences pref = context.getSharedPreferences("VOUCHER", MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();

        // lưu dữ liệu
        editor.putInt("ID", id);
        editor.putInt("DISCOUNT", discount);
        editor.putString("DEADLINE", deadline);

        // lưu lại
        editor.commit();
    }


}
