package com.example.cc_food.adapters;


import static android.content.Context.MODE_PRIVATE;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cc_food.DAO.RecommendDAO;
import com.example.cc_food.R;
import com.example.cc_food.activities.ShowDetailActivity;
import com.example.cc_food.adapters.recommend.ItemRecommend;

import java.io.IOException;
import java.util.List;

public class FavouriteAdapter extends RecyclerView.Adapter<FavouriteAdapter.viewHolder>  {
    ImageView imgZoomIn;
    private Context context;
    static RecommendDAO recommendDAO;
    private List<ItemRecommend> list;
    private List<ItemRecommend> listOld;
    ItemRecommend item ;




    public FavouriteAdapter(Context context, List<ItemRecommend> list) {
        this.context = context;
        this.list = list;
        this.listOld = list;
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_favourite, parent, false);
        return new viewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, @SuppressLint("RecyclerView") int position) {
        recommendDAO = new RecommendDAO(context);
        item = new ItemRecommend();

        holder.imgAdd.setImageResource(R.drawable.plus_circle);

        int favourite = recommendDAO.getFavourite(list.get(position).title);
        if (favourite ==1){
            holder.imgFavourite.setImageResource(R.drawable.ic_baseline_favorite_24);
        }else {
            holder.imgFavourite.setImageResource(R.drawable.ic_baseline_favorite_border_24);
        }

        String path = recommendDAO.getUriImg(list.get(position).title);
        holder.img.setImageBitmap(convert(path));

        holder.img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Animation aniSlide = AnimationUtils.loadAnimation(v.getContext(), R.anim.zoom_in);
//                v.startAnimation(aniSlide);
                Dialog dialog = new Dialog(v.getContext());
                dialog.setContentView(R.layout.dialog_image_zoom);

                imgZoomIn = dialog.findViewById(R.id.imgZoomIn);
                imgZoomIn.setImageBitmap(convert(path));

                WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
                lp.copyFrom(dialog.getWindow().getAttributes());
                lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
                lp.height = WindowManager.LayoutParams.WRAP_CONTENT;

                dialog.getWindow().setAttributes(lp);
                dialog.show();
            }
        });
        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), Uri.parse(path));
            holder.img.setImageBitmap(bitmap);
        } catch (IOException e) {
            e.printStackTrace();
        }

        holder.tvTitle.setText(list.get(position).title);
        holder.tvPrice.setText(String.valueOf(list.get(position).price + " $"));
        SharedPreferences pref = context.getSharedPreferences("USER_FILE", MODE_PRIVATE);
        String email = pref.getString("EMAIL", "");
//        int begin_index = email.indexOf("@");
//        int end_index = email.indexOf(".");
//        String domain_name = email.substring(begin_index + 1, end_index);
//        if (domain_name.toLowerCase(Locale.ROOT).equals("merchant")){
//            holder.imgDelete.setVisibility(View.VISIBLE);
//        }
//        holder.imgDelete.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Toast.makeText(context, "Deleted: " + list.get(position).title, Toast.LENGTH_SHORT).show();
//                recommendDAO.delete(list.get(position).title);
//                v.getContext().startActivity(new Intent(v.getContext(), MainActivity.class));
//            }
//        });
        holder.imgFavourite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (holder.imgFavourite.getTag().equals("oke")){
                    holder.imgFavourite.setImageResource(R.drawable.ic_baseline_favorite_border_24);
                    holder.imgFavourite.setTag("default");
                    item.favourite = 0;
                    item.title = list.get(position).title;
                    if (recommendDAO.updateFavourite(item) > 0){
                        Toast.makeText(context, "Đã bỏ yêu thích !", Toast.LENGTH_SHORT).show();
                        list.remove(position);
                        notifyDataSetChanged();
                    }
                }

            }
        });
        holder.imgAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(context, ShowDetailActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("image_resource", list.get(position).img_resource);
                bundle.putDouble("price", list.get(position).price);
                bundle.putString("title", list.get(position).title);
                intent1.putExtra("data", bundle);
                intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent1);
            }
        });

    }

    @Override
    public int getItemCount() {
        if (list == null){
            return 0;
        }
        return list.size();
    }


    public class viewHolder extends RecyclerView.ViewHolder {

        TextView tvTitle, tvPrice;
        ImageView img, imgAdd,imgFavourite, imgDelete;

        public viewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvNameFavourite);
            tvPrice = itemView.findViewById(R.id.tvCostFavourite);
            img = itemView.findViewById(R.id.imgAvatarFavourite);
            imgAdd = itemView.findViewById(R.id.imgAddFavourite);
//            imgDelete = itemView.findViewById(R.id.imgDeleteItemRecommend);
            imgFavourite = itemView.findViewById(R.id.imgItemFavourite);


        }
    }


    public Bitmap convert(String path){
        Bitmap bitmap = null;
        try {
            bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), Uri.parse(path));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bitmap;
    }
}

