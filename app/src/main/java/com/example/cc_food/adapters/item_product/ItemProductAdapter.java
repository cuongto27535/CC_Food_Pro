package com.example.cc_food.adapters.item_product;

import static android.content.Context.MODE_PRIVATE;

import static androidx.core.content.ContextCompat.startActivity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.example.cc_food.Admin.AdminActivity;
import com.example.cc_food.R;
import com.example.cc_food.activities.FavouriteActivity;
import com.example.cc_food.activities.ShowDetailActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ItemProductAdapter extends RecyclerView.Adapter<ItemProductAdapter.viewHolder> implements Filterable {
    Context context;
    ImageView imgZoomIn;
    ArrayList<ItemProduct> list;
    ArrayList<ItemProduct> listOld;



    public ItemProductAdapter(Context context) {
        this.context = context;
    }
    public void setData(ArrayList<ItemProduct> list) {
        this.listOld = list;
        this.list = list;
        notifyDataSetChanged();

    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_products, parent, false);
        return new viewHolder(view);
    }
    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, @SuppressLint("RecyclerView") int position) {
        ItemProduct item = list.get(position);
        SharedPreferences pref = context.getSharedPreferences("USER_FILE", MODE_PRIVATE);
        String email = pref.getString("EMAIL", "");
        int begin_index = email.indexOf("@");
        int end_index = email.indexOf(".");
        String domain_name = email.substring(begin_index + 1, end_index);
        if (domain_name.toLowerCase(Locale.ROOT).equals("merchant")) {
            holder.imgDelete.setVisibility(View.VISIBLE);

//           if (holder.btnThongke != null) {
//               // Gọi phương thức setVisibility
//                holder.btnThongke.setVisibility(View.VISIBLE); // hoặc View.GONE hoặc View.INVISIBLE
//           }
        }


        holder.imgDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                list.remove(position);
                notifyDataSetChanged();
                Toast.makeText(context, "Delete " + item.getTitle(), Toast.LENGTH_SHORT).show();
//                v.getContext().startActivity(new Intent(context, MainActivity.class));

            }
        });
        if (item == null) {
            return;
        }



        holder.img.setImageResource(item.getImg());
        holder.img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Animation aniSlide = AnimationUtils.loadAnimation(v.getContext(), R.anim.zoom_in);
//                v.startAnimation(aniSlide);
                Dialog dialog = new Dialog(v.getContext());
                dialog.setContentView(R.layout.dialog_image_zoom);

                imgZoomIn = dialog.findViewById(R.id.imgZoomIn);
                imgZoomIn.setImageResource(list.get(position).getImg());

                WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
                lp.copyFrom(dialog.getWindow().getAttributes());
                lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
                lp.height = WindowManager.LayoutParams.WRAP_CONTENT;

                dialog.getWindow().setAttributes(lp);
                dialog.show();
            }
        });
        holder.imgFavourite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (holder.imgFavourite.getTag().equals("default")){
                    holder.imgFavourite.setImageResource(R.drawable.ic_baseline_favorite_24);
                    holder.imgFavourite.setTag("oke");
//                    item.favourite = 1;
//                    item.title = list.get(position).title;
//                    if (recommendDAO.updateFavourite(item) > 0){
//                        Toast.makeText(context, "Đã thêm vào yêu thích !", Toast.LENGTH_SHORT).show();
//                    }
                }
                else {
                    holder.imgFavourite.setImageResource(R.drawable.ic_baseline_favorite_border_24);
                    holder.imgFavourite.setTag("default");
//                    item.favourite = 0;
//                    item.title = list.get(position).title;
//                    if (recommendDAO.updateFavourite(item) > 0){
//                        Toast.makeText(context, "Đã bỏ yêu thích !", Toast.LENGTH_SHORT).show();
//                    }
                }
            }
        });
        holder.imgAdd.setImageResource(item.getResource_image());
        holder.tvPrice.setText("" + item.getMoney());
        holder.tvTitle.setText(item.getTitle());
        holder.imgAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(v.getContext(), ShowDetailActivity.class);
                Bundle bundle = new Bundle();
                bundle.putInt("image", list.get(position).getImg());
                bundle.putDouble("price", list.get(position).getMoney());
                bundle.putString("title", list.get(position).getTitle());
                bundle.putString("time_delay", "15");
                bundle.putDouble("calo", 65.8);
                bundle.putDouble("rate", 4.8);
                bundle.putString("description", "Mô tả sản phẩm");
                intent1.putExtra("data", bundle);
                v.getContext().startActivity(intent1);
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
        TextView tvTitle, tvPrice;
        ImageView img, imgAdd, imgDelete, imgFavourite;
       LinearLayout btnThongke;

        public viewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvTitleProduct);
            tvPrice = itemView.findViewById(R.id.tvPrice);
            img = itemView.findViewById(R.id.imgProducts);
            imgAdd = itemView.findViewById(R.id.imgAdd);
            imgDelete = itemView.findViewById(R.id.imgDeleteItemProduct);
            imgFavourite = itemView.findViewById(R.id.imgFavouriteProduct);
            btnThongke=itemView.findViewById(R.id.btn_Thongke);


        }
    }
    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                String str = constraint.toString();
                if (str.isEmpty()) {
                    list = listOld;
                } else {
                    List<ItemProduct> mlist = new ArrayList<>();
                    for (ItemProduct food : listOld) {
                        if (food.getTitle().toLowerCase().contains(str.toLowerCase())) {
                            mlist.add(food);
                        }
                    }
                    list = (ArrayList<ItemProduct>) mlist;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = list;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                list = (ArrayList<ItemProduct>) results.values;
                notifyDataSetChanged();
            }
        };
    }

}

