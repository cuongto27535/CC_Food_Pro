package com.example.cc_food.ui.cart;

import static android.content.Context.MODE_PRIVATE;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.example.cc_food.DAO.CartItemDAO;
import com.example.cc_food.DAO.CartSystemDAO;
import com.example.cc_food.DAO.OderDAO;
import com.example.cc_food.DAO.UsersDAO;
import com.example.cc_food.R;
import com.example.cc_food.activities.PaymentActivity;
import com.example.cc_food.activities.UserUpdateInfoActivity;
import com.example.cc_food.adapters.Billdetails_adapter;
import com.example.cc_food.adapters.CartItemAdapter;
import com.example.cc_food.modules.CartItemModule;
import com.example.cc_food.modules.CartSystemModule;
import com.example.cc_food.modules.OderHistoryModel;
import com.example.cc_food.modules.UsersModule;
import com.example.cc_food.modules.billdetailmodel;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import com.example.cc_food.adapters.CartItemAdapter;

public class CartFragmentOld extends Fragment {

    private ConstraintLayout checkOut, titleCart;
    RecyclerView recyclerView, RCL;
    private ArrayList<CartItemModule> listCartItem, listCartItem1;
    CartItemModule itemCart;
    static CartItemAdapter CartItemAdapter;
    static com.example.cc_food.DAO.CartItemDAO CartItemDAO;
    static CartSystemDAO cartSystemDAO;
    private ArrayList<CartSystemModule> listCartSystem;
    static UsersDAO usersDAO;
    UsersModule itemUser;
    ArrayList<UsersModule> listUser;
    public String date;
    private String chuoi = "";
    Billdetails_adapter adapter;
    public ArrayList<billdetailmodel> moduleArrayList;
    static OderDAO oderDAO;
    ArrayList<OderHistoryModel> listOder;
    OderHistoryModel itemOder;


    TextView tvToTal, tvDelivery, tvTax, tvCouponCost, tvCoupon;
    TextView tvToTalPriceFinal;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    public static final String TOTAL_KEY = "doanhthu";
    public static final String TOTAL_FINAL_KEY = "tongdoanhthu";

    private static final String TAG = "test";

    public CartFragmentOld() {
        // Required empty public constructor
    }

    @NonNull
    public static CartFragmentOld newInstance() {
        CartFragmentOld fragment = new CartFragmentOld();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_cart, container, false);


        oderDAO = new OderDAO(getContext());
        CartItemDAO = new CartItemDAO(getContext());
        cartSystemDAO = new CartSystemDAO(getContext());
        usersDAO = new UsersDAO(getContext());

        titleCart = view.findViewById(R.id.title_cart);
        checkOut = view.findViewById(R.id.checkOut);
        tvToTal = view.findViewById(R.id.total_price_item);
        tvDelivery = view.findViewById(R.id.delivery_price);
        tvTax = view.findViewById(R.id.tax_price);
        tvToTalPriceFinal = view.findViewById(R.id.total_price_cart);
        recyclerView = view.findViewById(R.id.rec_cart);
        tvCouponCost = view.findViewById(R.id.tvCouponCost);
        tvCouponCost.setText("Chọn voucher");
        tvCoupon = view.findViewById(R.id.tvCoupon);
        tvCoupon.setText("Voucher: ");
        Calendar calendar = Calendar.getInstance();

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss ");
        date = format.format(calendar.getTime());

        moduleArrayList = new ArrayList<>();


//        tvCouponCost.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                temp2++;
//                startActivity(new Intent(getContext(), DealsActivity.class));
//                getFragmentManager().beginTransaction().detach(CartFragment.this).attach(CartFragment.this).commit();
//
//
//            }
//        });

        listData();
        checkItemSelected(1, 0);
        return view;
    }

    private void listData() {
        SharedPreferences pref = getContext().getSharedPreferences("USER_FILE", MODE_PRIVATE);
        String email = pref.getString("EMAIL", "");
        int idUser = usersDAO.getIDUser(email);
        listCartItem = (ArrayList<CartItemModule>) CartItemDAO.getALLSelected(0);
        if (listCartItem.size() == 0) {
            titleCart.setVisibility(View.INVISIBLE);
            Toast.makeText(getContext(), "Giỏ hàng trống. Quay lại mua hàng.", Toast.LENGTH_SHORT).show();
        } else {
            CartItemAdapter = new CartItemAdapter(listCartItem, getContext());
            recyclerView.setAdapter(CartItemAdapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
            swipeToDelete();
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference reference = database.getReference("object_cart");
            reference.setValue(listCartItem, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
//                    Toast.makeText(getContext(), "Thàng công", Toast.LENGTH_SHORT).show();
                }
            });
        }

    }


    public Double total() {
        double price = 0;
        SharedPreferences pref = getContext().getSharedPreferences("USER_FILE", MODE_PRIVATE);
        String email = pref.getString("EMAIL", "");
        int idUser = usersDAO.getIDUser(email);
        listCartItem = (ArrayList<CartItemModule>) CartItemDAO.getALLPro(idUser);
        for (int i = 0; i < listCartItem.size(); i++) {
            price += listCartItem.get(i).cost;
        }
        return price;
    }

    // vuốt để xoá
    public void swipeToDelete() {
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                CartItemModule itemDelete = listCartItem.get(viewHolder.getAdapterPosition());
                int position = viewHolder.getAdapterPosition();
                int id = itemDelete.id;
                itemCart = new CartItemModule();
                itemCart.check = 1;
                itemCart.name = itemDelete.name;
                if (CartItemDAO.updateStatus(itemCart) > 0) {
                    Log.d(TAG, "onSwiped: " + "update check success");
                }
                listCartItem1 = (ArrayList<CartItemModule>) CartItemDAO.getALLSelected(0);
                CartItemAdapter = new CartItemAdapter(listCartItem1, getContext());
                recyclerView.setAdapter(CartItemAdapter);
                recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));

                Snackbar snackbar = Snackbar.make(recyclerView, "Deleted " + itemDelete.name, Snackbar.LENGTH_LONG);
                snackbar.setAction("Undo", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        itemCart = new CartItemModule();
                        itemCart.check = 0;
                        itemCart.name = itemDelete.name;
                        if (CartItemDAO.updateStatus(itemCart) > 0) {
                            Log.d(TAG, "onSwiped: " + "rollback check success");
                            listCartItem = (ArrayList<CartItemModule>) CartItemDAO.getALLSelected(0);
                            CartItemAdapter = new CartItemAdapter(listCartItem, getContext());
                            recyclerView.setAdapter(CartItemAdapter);
                            recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
                        }

                    }
                });
                Snackbar.SnackbarLayout snackBarLayout = (Snackbar.SnackbarLayout) snackbar.getView();
                snackBarLayout.setPadding(0, 0, 0, 110);
                snackbar.show();

            }
        }).attachToRecyclerView(recyclerView);


    }

    public void checkItemSelected(int mCheck, int discount) {

        SharedPreferences pref = getContext().getSharedPreferences("USER_FILE", MODE_PRIVATE);
        String email = pref.getString("EMAIL", "");

        if (mCheck != 0) {
//            Double total = Double.valueOf(pref.getString("COST", ""));
            CartItemDAO = new CartItemDAO(getContext());
            double totalPriceItem = total();
            tvToTal.setText(String.format("%.2f", totalPriceItem) + " $");
            tvTax.setText(String.format("%.2f", totalPriceItem * 0.1) + " $");
            Double coupon = discount * totalPriceItem / 100;
            tvCouponCost.setText(coupon + " $");
            tvCoupon.setText("Voucher: " + discount + "%");
            tvDelivery.setText(String.format("%.2f", totalPriceItem * 0.05) + " $");
            double totalFinal = totalPriceItem + totalPriceItem * 0.1 + totalPriceItem * 0.05 - coupon;
            tvToTalPriceFinal.setText(String.format("%.2f", totalFinal) + " $");
            int idUser = usersDAO.getIDUser(email);
            listCartItem = (ArrayList<CartItemModule>) CartItemDAO.getALLPro(idUser);
            checkOut.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (listCartItem.size() == 0) {
                        Toast.makeText(getContext(), "Chưa có sản phẩm", Toast.LENGTH_SHORT).show();
                    } else {
                        if (checkInfo() > 0) {
                            startActivity(new Intent(getContext(), PaymentActivity.class));
                        }
                    }
                }
            });
        }
    }

    private int checkInfo() {
        int check = -1;
        SharedPreferences pref = getContext().getSharedPreferences("USER_FILE", MODE_PRIVATE);
        String email = pref.getString("EMAIL", "");
        listUser = (ArrayList<UsersModule>) usersDAO.getALLByEmail(email);
        itemUser = listUser.get(0);
        String name = itemUser.name;
        String phoneNumber = itemUser.phoneNumber;
        String address = itemUser.address;

        if (name.equals("null") || phoneNumber.equals("null") || address.equals("null")) {
            new AlertDialog.Builder(getContext())
                    .setTitle("Warning")
                    .setMessage("Để mua hàng bạn cần cập nhật đầy đủ thông tin tài khoản ?")
                    .setIcon(R.drawable.ic_baseline_warning_24)
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            startActivity(new Intent(getContext(), UserUpdateInfoActivity.class));
                        }
                    })
                    .setNegativeButton(android.R.string.no, null)
                    .setCancelable(false)
                    .show();
        } else {
            check = 1;
        }

        return check;

    }




    @Override
    public void onStart() {
        super.onStart();
//        SharedPreferences pref = getContext().getSharedPreferences("CheckSelected", MODE_PRIVATE);
//        String name = pref.getString("name", "");
//        Toast.makeText(getContext(), "name: " + name, Toast.LENGTH_SHORT).show();

//        getdata();
        for (int i = 0; i < listCartItem.size(); i++) {
            chuoi += "● " + listCartItem.get(i).name + "   --   SL: " + listCartItem.get(i).quantities + "\n";
        }
//        FirebaseDatabase database = FirebaseDatabase.getInstance();
//        DatabaseReference reference = database.getReference("objec_bill");
//        reference.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                if (moduleArrayList != null) {
//                    moduleArrayList.clear();
//                }
//                for (DataSnapshot snapshot1 : snapshot.getChildren()) {
//                    billdetailmodel billdetailmodel1 = snapshot1.getValue(billdetailmodel.class);
//                    moduleArrayList.add(billdetailmodel1);
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });
    }


}
