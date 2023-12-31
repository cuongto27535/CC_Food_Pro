package com.example.cc_food.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.example.cc_food.DAO.CartItemTempDAO;
import com.example.cc_food.DAO.RecommendDAO;
import com.example.cc_food.DAO.UsersDAO;
import com.example.cc_food.R;
import com.example.cc_food.modules.CartTempModule;

import java.io.IOException;
import java.util.List;

public class CartItemTempAdapter extends RecyclerView.Adapter<CartItemTempAdapter.ViewHolder> {
    private List<CartTempModule> list;
    private Context context;
    static CartItemTempDAO cartItemTempDAO;
    CartTempModule itemCartTemp;
    static UsersDAO usersDAO;
    static RecommendDAO recommendDAO;
    private static final String TAG = "test";

    public CartItemTempAdapter(List<CartTempModule> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_detail_cart, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        cartItemTempDAO = new CartItemTempDAO(context);
        recommendDAO = new RecommendDAO(context);
        usersDAO = new UsersDAO(context);
        String uri = list.get(position).img;
        if (!(uri.isEmpty() || uri.equals("null"))) {
            holder.imgAvatar.setImageBitmap(convert(uri));
        }

        holder.tvName.setText(list.get(position).name);
        holder.tvCost.setText("Giá: " + String.format("%.2f", list.get(position).cost / list.get(position).quantities));
        holder.tvQuantity.setText("x" + list.get(position).quantities);
        holder.tvNumberProduct.setText("" + list.get(position).quantities);

        holder.imgDecrement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                itemCartTemp = new CartTempModule();
                int quantities = Integer.parseInt(holder.tvNumberProduct.getText().toString());
                quantities--;
                if (quantities != 0) {
                    holder.tvNumberProduct.setText("" + quantities);
                    holder.tvQuantity.setText("x" + quantities);

                    itemCartTemp.name = list.get(position).name;
                    itemCartTemp.costNew = quantities * (list.get(position).cost / list.get(position).quantities);
                    itemCartTemp.quantitiesNew = quantities;
                    if (cartItemTempDAO.updatePrice(itemCartTemp) > 0) {
                        Log.d(TAG, "onClick: " + "update new cost success");
                    }
                }
            }
        });
        holder.imgIncrement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                itemCartTemp = new CartTempModule();
                int quantities = Integer.parseInt(holder.tvNumberProduct.getText().toString());
                quantities++;
                holder.tvNumberProduct.setText("" + quantities);
                holder.tvQuantity.setText("x" + quantities);
                itemCartTemp = new CartTempModule();

                itemCartTemp.name = list.get(position).name;
                itemCartTemp.costNew = quantities * (list.get(position).cost / list.get(position).quantities);
                itemCartTemp.quantitiesNew = quantities;
                if (cartItemTempDAO.updatePrice(itemCartTemp) > 0) {
                    Log.d(TAG, "onClick: " + "update new cost success");
                }

            }
        });


    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imgAvatar;
        TextView tvName, tvCost, tvQuantity;
        TextView tvNumberProduct;
        ImageView imgIncrement, imgDecrement;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgAvatar = itemView.findViewById(R.id.imgDetailCart);
            tvName = itemView.findViewById(R.id.tvNameProductDetail);
            tvCost = itemView.findViewById(R.id.tvPriceProductDetail);
            tvQuantity = itemView.findViewById(R.id.tvQuantityProductDetail);
            tvNumberProduct = itemView.findViewById(R.id.tvNumberProduct);
            imgIncrement = itemView.findViewById(R.id.imgIncrement);
            imgDecrement = itemView.findViewById(R.id.imgDecrement);
        }
    }

    public Bitmap convert(String path) {
        Bitmap bitmap = null;
        try {
            bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), Uri.parse(path));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bitmap;
    }

}
