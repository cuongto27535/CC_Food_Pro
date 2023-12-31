package com.example.cc_food.activities;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cc_food.DAO.RecommendDAO;
import com.example.cc_food.R;
import com.example.cc_food.adapters.ProductHiddenAdapter;
import com.example.cc_food.adapters.recommend.ItemRecommend;

import java.util.List;

public class ProductHiddenActivity extends AppCompatActivity {
    private RecyclerView rcvProductHidden;
    static RecommendDAO recommendDAO;
    ProductHiddenAdapter productHiddenAdapter;
    List<ItemRecommend> listProduct;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_hidden);

//        gridView = view.findViewById(R.id.grView);
//        gridAdapter = new GridAdapter(getActivity(), R.layout.layout_item, logoObjects);
//        gridView.setAdapter(gridAdapter);
        rcvProductHidden = findViewById(R.id.rcvProductHidden);
        recommendDAO = new RecommendDAO(getApplicationContext());
        listProduct = recommendDAO.getALL(1);
        productHiddenAdapter = new ProductHiddenAdapter(getApplicationContext(), listProduct);
        rcvProductHidden.setAdapter(productHiddenAdapter);
        rcvProductHidden.setLayoutManager(new LinearLayoutManager(getApplicationContext(), RecyclerView.HORIZONTAL, false));
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        listProduct = recommendDAO.getALL(1);
        productHiddenAdapter = new ProductHiddenAdapter(getApplicationContext(), listProduct);
        rcvProductHidden.setAdapter(productHiddenAdapter);
        rcvProductHidden.setLayoutManager(new LinearLayoutManager(getApplicationContext(), RecyclerView.HORIZONTAL, false));
    }
}