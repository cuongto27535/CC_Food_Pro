package com.example.cc_food.adapters.category;

import static android.content.Context.MODE_PRIVATE;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;


import com.example.cc_food.DAO.CategoryDAO;
import com.example.cc_food.R;
import com.example.cc_food.activities.UpdateItemCategoryActivity;
import com.example.cc_food.adapters.recommend.ItemRecommend;

import java.io.IOException;
import java.util.List;

public class ItemCategoryAdapter extends RecyclerView.Adapter<ItemCategoryAdapter.viewHolder> {
    Context context;
    List<ItemCategory> list;
    List<ItemRecommend> list1;
    CategoryDAO categoryDAO;

    boolean check = true;
    boolean selected = true;
    int row_index = -1;


    public ItemCategoryAdapter(Context context, List<ItemCategory> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_category, parent, false);
        return new viewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, @SuppressLint("RecyclerView") int position) {
        ItemCategory item = list.get(position);
        categoryDAO = new CategoryDAO(context);
        if (item == null) {
            return;
        }


        String nameCategory = item.getName();
        String uri = categoryDAO.getUriImg(nameCategory);
        holder.img.setImageBitmap(convert(uri));

//        String[] listPath = {"content://media/external_primary/images/media/1000002401", "content://media/external_primary/images/media/1000002403", "content://media/external_primary/images/media/1000002405", "content://media/external_primary/images/media/1000002402", "content://media/external_primary/images/media/1000002404", "content://media/external_primary/images/media/1000002414"};

        holder.tvName.setText(nameCategory);
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "" + nameCategory, Toast.LENGTH_SHORT).show();
//                Toast.makeText(context, ""+ item.getImg(), Toast.LENGTH_SHORT).show();

                // filter
                holder.cardView.setBackgroundResource(R.drawable.change_bg);
                selected = false;



            }
        });


    }

    @Override
    public int getItemCount() {
        if (list != null) {
            return list.size();
        }
        return 0;
    }

    public class viewHolder extends RecyclerView.ViewHolder {
        TextView tvName;
        ImageView img, imgDelete;
        ConstraintLayout cardView;


        public viewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvNameItem);
            img = itemView.findViewById(R.id.img_item_cate);
            cardView = itemView.findViewById(R.id.card_view);
            SharedPreferences pref = context.getSharedPreferences("USER_FILE", MODE_PRIVATE);
            String email = pref.getString("EMAIL", "");
            int begin_index = email.indexOf("@");
            int end_index = email.indexOf(".");
            String domain_name = email.substring(begin_index + 1, end_index);
            if (domain_name.equals("merchant")){
                itemView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        SharedPreferences pref = v.getContext().getSharedPreferences("INFO_ITEM", MODE_PRIVATE);
                        SharedPreferences.Editor editor = pref.edit();
                        // lưu dữ liệu
                        editor.putInt("ID", list.get(getLayoutPosition()).getId());
                        editor.putString("NAME", list.get(getLayoutPosition()).getName());
                        editor.putString("IMG", list.get(getLayoutPosition()).getImg());

                        // lưu lại
                        editor.commit();
                        v.getContext().startActivity(new Intent(context, UpdateItemCategoryActivity.class));
                        return false;
                    }
                });
            }
            else {
                itemView.setOnLongClickListener(null);
            }



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

