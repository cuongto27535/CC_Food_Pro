package com.example.cc_food.ui.favourite.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.example.cc_food.R;
import com.example.cc_food.adapters.FeaturedAdapter;
import com.example.cc_food.adapters.FeaturedVerAdapter;
import com.example.cc_food.modules.FeaturedModule;
import com.example.cc_food.modules.RecommendedModule;

import java.util.ArrayList;
import java.util.List;


public class ThirdFragment extends Fragment {

    ///////Hor
    List<FeaturedModule> featuredModules;
    RecyclerView recyclerView;
    FeaturedAdapter featuredAdapter;
    //////Ver
    List<RecommendedModule> foods;
    RecyclerView recyclerView2;
    FeaturedVerAdapter featuredVerAdapter;

    public ThirdFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_third, container, false);
        recyclerView = view.findViewById(R.id.featured_hor_rec3);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, false));

        featuredModules = new ArrayList<>();
        featuredModules.add(new FeaturedModule(R.drawable.new1, "New 1", "Description 1"));
        featuredModules.add(new FeaturedModule(R.drawable.new2, "New 2", "Description 2"));
        featuredModules.add(new FeaturedModule(R.drawable.new3, "New 3", "Description 3"));
        featuredModules.add(new FeaturedModule(R.drawable.new3, "New 4", "Description 4"));
        featuredModules.add(new FeaturedModule(R.drawable.new3, "New 5", "Description 5"));


        featuredAdapter = new FeaturedAdapter(featuredModules);
        recyclerView.setAdapter(featuredAdapter);
///////////////ver

        recyclerView2 = view.findViewById(R.id.featured_ver_rec3);
        recyclerView2.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));

        foods = new ArrayList<>();
        foods.add(new RecommendedModule(R.drawable.new4, "Stewed beef", 3.6, R.drawable.plus_circle));
        foods.add(new RecommendedModule(R.drawable.new5, "Passionfruit Salmon", 2.4, R.drawable.plus_circle));
        foods.add(new RecommendedModule(R.drawable.new6, "Cheese baked noodles", 2.1, R.drawable.plus_circle));
        foods.add(new RecommendedModule(R.drawable.new6, "Cheese baked noodles 2", 2.1, R.drawable.plus_circle));
        foods.add(new RecommendedModule(R.drawable.new6, "Cheese baked noodles 3", 2.1, R.drawable.plus_circle));



        featuredVerAdapter = new FeaturedVerAdapter(getContext(), foods);
        recyclerView2.setAdapter(featuredVerAdapter);
        return view;
    }
}