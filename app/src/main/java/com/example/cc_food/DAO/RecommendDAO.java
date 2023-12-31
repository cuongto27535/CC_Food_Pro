package com.example.cc_food.DAO;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;


import com.example.cc_food.adapters.recommend.ItemRecommend;
import com.example.cc_food.database.DbHelper;

import java.util.ArrayList;
import java.util.List;

public class RecommendDAO {
    private SQLiteDatabase db;

    public RecommendDAO(Context context) {
        DbHelper dbHelper = new DbHelper(context);
        db = dbHelper.getWritableDatabase();
    }

    public long insert(ItemRecommend obj) {
        ContentValues values = new ContentValues();

        values.put("img", obj.img_resource);
        values.put("name", obj.title);
        values.put("cost", obj.price);
        values.put("idUser", obj.idUser);
        values.put("mCheck", obj.check);
        values.put("favourite", obj.favourite);
        values.put("description", obj.description);
        values.put("timeDelay", obj.timeDelay);
        values.put("rate", obj.rate);
        values.put("calo", obj.calo);
        values.put("quantity_sold", obj.quantity_sold);
        values.put("location", obj.location);


        return db.insert("Recommend", null, values);
    }
    public int updateAll(ItemRecommend obj){
        ContentValues values = new ContentValues();
        values.put("img", obj.img_resource);
        values.put("name", obj.title);
        values.put("cost", obj.price);
        values.put("idUser", obj.idUser);
        values.put("favourite", obj.favourite);
        values.put("description", obj.description);
        values.put("timeDelay", obj.timeDelay);
        values.put("calo", obj.calo);
        values.put("location", obj.location);

        return db.update("Recommend", values, "id=?", new String[]{String.valueOf(obj.id)} );
    }
    public int updateFavourite(ItemRecommend obj){
        ContentValues values = new ContentValues();
        values.put("favourite", obj.favourite);
        return db.update("Recommend", values, "name=?", new String[]{obj.title});
    }

    public String getUriImg(String name) {
        String sql = "SELECT * FROM Recommend WHERE name=?";
        List<ItemRecommend> list = getData(sql, name);
        return list.get(0).img_resource;
    }
    public int getFavourite(String name) {
        String sql = "SELECT * FROM Recommend WHERE name=?";
        List<ItemRecommend> list = getData(sql, name);
        return list.get(0).favourite;
    }
    public int updateStatus(ItemRecommend obj) {
        ContentValues values = new ContentValues();
        values.put("mCheck", obj.check);
        return db.update("Recommend", values, "id=?", new String[]{String.valueOf(obj.id)});
    }


    public int delete(String name) {
        return db.delete("Recommend", "name=?", new String[]{name});
    }


    public List<ItemRecommend> getALL(int check) {
        String sql = "SELECT * FROM Recommend WHERE mCheck=?";
        return getData(sql, new String[]{String.valueOf(check)});
    }
    public List<ItemRecommend> getALLFavourite(int favourite){
        String sql = "SELECT * FROM Recommend WHERE favourite=?";
        return getData(sql, String.valueOf(favourite));
    }
    public List<ItemRecommend> getALLByID(int id, int check){
        String sql = "SELECT * FROM Recommend WHERE id=? AND mCheck=?";
        return getData(sql, new String[]{String.valueOf(id), String.valueOf(check)});
    }


    @SuppressLint("Range")
    private List<ItemRecommend> getData(String sql, String... selectionArgs) {
        List<ItemRecommend> list = new ArrayList<>();
        Cursor cursor = db.rawQuery(sql, selectionArgs);
        while (cursor.moveToNext()) {
            ItemRecommend obj = new ItemRecommend();
            obj.id = Integer.parseInt(cursor.getString(cursor.getColumnIndex("id")));
            obj.idUser = Integer.parseInt(cursor.getString(cursor.getColumnIndex("idUser")));
            obj.check = Integer.parseInt(cursor.getString(cursor.getColumnIndex("mCheck")));
            obj.img_resource = cursor.getString(cursor.getColumnIndex("img"));
            obj.title = cursor.getString(cursor.getColumnIndex("name"));
            obj.price = Double.parseDouble(cursor.getString(cursor.getColumnIndex("cost")));
            obj.favourite = Integer.parseInt(cursor.getString(cursor.getColumnIndex("favourite")));
            obj.description = cursor.getString(cursor.getColumnIndex("description"));
            obj.rate = Double.parseDouble(cursor.getString(cursor.getColumnIndex("rate")));
            obj.calo = Double.parseDouble(cursor.getString(cursor.getColumnIndex("calo")));
            obj.timeDelay = cursor.getString(cursor.getColumnIndex("timeDelay"));
            obj.quantity_sold = Integer.parseInt(cursor.getString(cursor.getColumnIndex("quantity_sold")));
            obj.location = cursor.getString(cursor.getColumnIndex("location"));
            list.add(obj);

        }
        return list;

    }
}
