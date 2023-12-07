package com.example.cc_food.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cc_food.DAO.CartItemDAO;
import com.example.cc_food.DAO.CartItemTempDAO;
import com.example.cc_food.DAO.OderDAO;
import com.example.cc_food.DAO.UsersDAO;
import com.example.cc_food.R;
import com.example.cc_food.adapters.CartItemTempAdapter;
import com.example.cc_food.modules.CartItemModule;
import com.example.cc_food.modules.CartTempModule;
import com.example.cc_food.modules.OderHistoryModelNew;
import com.example.cc_food.modules.billdetailmodel;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Random;

public class PaymentActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private TextView tvNumberOder, tvValueVoucher, tvName, tvPhone, tvAddress, tvToTalCost, tvFeeTransport, tvVoucher;
    private RecyclerView rcvListProduct;
    static CartItemDAO cartItemDAO;
    CartItemModule itemCart;
    ArrayList<CartTempModule> listCart;
    com.example.cc_food.adapters.CartItemTempAdapter CartItemTempAdapter;
    static CartItemTempDAO cartItemTempDAO;
    static UsersDAO usersDAO;
    static OderDAO oderDAO;
    ArrayList<OderHistoryModelNew> listOder;
    OderHistoryModelNew itemOder;
    public static int check = 0;
    int numberNotification = 0;
    private Button btnPurchase;
    private static final String TAG = "test";
    private String chuoi = "";
    public String date;
    private int voucher = 0;

    private LinearLayout selectLocation, selectVoucher;
    public ArrayList<billdetailmodel> moduleArrayList;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);
        selectLocation = findViewById(R.id.selectLocation);
        selectLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(PaymentActivity.this, SelectLocationActivity.class));
            }
        });
        tvName = findViewById(R.id.tvFullName);
        tvPhone = findViewById(R.id.tvPhoneNumber);
        tvAddress = findViewById(R.id.tvAddress);
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss ");
        date = format.format(calendar.getTime());
        usersDAO = new UsersDAO(getApplicationContext());
        oderDAO = new OderDAO(getApplicationContext());
        SharedPreferences pref = getSharedPreferences("USER_FILE", MODE_PRIVATE);
        String email = pref.getString("EMAIL", "");
        int idUser = usersDAO.getIDUser(email);
        tvNumberOder = findViewById(R.id.tvNumberOder);
        String name = usersDAO.getNameUser(email);
        String phone = usersDAO.getPhone(email);
        String address = usersDAO.getAddress(email);

        tvName.setText(name + " | ");
        tvPhone.setText(phone);
        tvAddress.setText(address);


        listOder = (ArrayList<OderHistoryModelNew>) oderDAO.getAllByIdUser(idUser);
        btnPurchase = findViewById(R.id.btnPurchase);

        if (listOder != null) {
            numberNotification = listOder.size();
            tvNumberOder.setText(" " + numberNotification);
        }


        toolbar = findViewById(R.id.toolBarCart);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        toolbar.setTitle("Thanh toán");
        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.setNavigationIcon(R.drawable.ic_baseline_arrow_back_24);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        cartItemDAO = new CartItemDAO(getApplicationContext());
        cartItemTempDAO = new CartItemTempDAO(getApplicationContext());
        rcvListProduct = findViewById(R.id.rcvListProductDetail);
        listCart = (ArrayList<CartTempModule>) cartItemTempDAO.getALL(idUser, 1);
        double priceTotal = 0.0;
        for (int i = 0; i < listCart.size(); i++) {
            priceTotal += listCart.get(i).cost;
        }
        if (listCart.size() == 0) {
            btnPurchase.setText("Mua hàng(0)");
        } else {
            btnPurchase.setText("Mua hàng(" + listCart.size() + ")");
            CartItemTempAdapter = new CartItemTempAdapter(listCart, getApplicationContext());
            rcvListProduct.setAdapter(CartItemTempAdapter);
            rcvListProduct.setLayoutManager(new LinearLayoutManager(getApplicationContext(), RecyclerView.VERTICAL, false));
        }
        tvValueVoucher = findViewById(R.id.tvValueVoucher);
        selectVoucher = findViewById(R.id.selectVoucher);
        selectVoucher.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(PaymentActivity.this, DealsActivity.class));

            }
        });

        double price = cartItemTempDAO.getToTalPrice();
        tvToTalCost = findViewById(R.id.tvToTalCost);

        tvFeeTransport = findViewById(R.id.tvFeeTransport);
        double feeTransport = priceTotal * 0.05;
        tvFeeTransport.setText("Phí vận chuyển($)  " + String.format("%.2f", feeTransport));
        double total = priceTotal + feeTransport;
        tvToTalCost.setText(String.format("%.2f", (total-voucher)));
        tvVoucher = findViewById(R.id.tvVoucher);
        if (check > 0) {
            Intent intent = getIntent();
            if (intent != null) {
                Bundle bundle = intent.getBundleExtra("discount");
                if (bundle != null) {
                    int discount = bundle.getInt("value");
                    voucher = discount;
                    tvVoucher.setText("Voucher(" + discount + "%)");
                    double voucher = total * discount /100;
                    tvValueVoucher.setText("- " + String.format("%.2f", voucher));
                    tvToTalCost.setText(String.format("%.2f", (total-voucher)));


                }

            }

        }


        double finalPriceTotal = priceTotal;
        btnPurchase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (cartItemDAO.deleteSelected(1) > 0) {
                    if (cartItemTempDAO.deleteByCheckSelected(1) > 0) {
                        Log.d(TAG, "onClick: " + "removed products as by");
                        moduleArrayList = new ArrayList<>();
                        listOder = new ArrayList<>();
                        itemOder = new OderHistoryModelNew();
                        Random random = new Random();
                        int id_randum = random.nextInt();
                        if (id_randum < 0) {
                            id_randum *= -1;
                        }
                        double tongtiensanpham = finalPriceTotal;
                        double tax = tongtiensanpham * 0.1;
                        double feeTransport = tongtiensanpham * 0.05;
                        //double total = tongtiensanpham + tongtiensanpham * 0.1 + tongtiensanpham * 0.05 - voucher;
                        double total = tongtiensanpham + feeTransport - (tongtiensanpham * voucher / 100);

                        FirebaseDatabase database = FirebaseDatabase.getInstance();
                        DatabaseReference reference = database.getReference("objec_bill");
                        moduleArrayList.add(new billdetailmodel(id_randum, chuoi, "Chưa thanh toán", date, tongtiensanpham, feeTransport, total));
                        reference.setValue(moduleArrayList, new DatabaseReference.CompletionListener() {
                            @Override
                            public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                                Log.d(TAG, "onComplete: " + "push success to firebase");
                            }
                        });

                        itemOder.code = id_randum;
                        itemOder.listProduct = chuoi;
                        itemOder.checkStatus = 0;
                        itemOder.status = "Chưa thanh toán";
                        itemOder.idUser = usersDAO.getIDUser(email);
                        itemOder.dateTime = date;
                        itemOder.totalFinal = total;
                        itemOder.totalItem = tongtiensanpham;
                        itemOder.feeTransport = feeTransport;
                        if (oderDAO.insert(itemOder) > 0) {
                            numberNotification +=1;
                            tvNumberOder.setText(" " + numberNotification);
                            Log.d(TAG, "onClick: " + "save data success");
                        }
                        if (oderDAO.getALL().size() > 0) {
                            FirebaseDatabase database1 = FirebaseDatabase.getInstance();
                            DatabaseReference reference1 = database1.getReference("objec_bill");
                            listOder = (ArrayList<OderHistoryModelNew>) oderDAO.getALL();
                            reference1.setValue(listOder, new DatabaseReference.CompletionListener() {
                                @Override
                                public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                                    Toast.makeText(getApplicationContext(), "Đặt hàng thành công !", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                        startActivity(new Intent(PaymentActivity.this, MainActivity.class));
                        finishAffinity();
                    }
                }
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_bottom_nav, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_home:
                startActivity(new Intent(PaymentActivity.this, MainActivity.class));
                return true;
            case R.id.action_support:

                return true;
            case R.id.action_order:
                startActivity(new Intent(PaymentActivity.this, OrderHistoryActivity.class));
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences pref = getSharedPreferences("USER_FILE", MODE_PRIVATE);
        String email = pref.getString("EMAIL", "");
        int idUser = usersDAO.getIDUser(email);
        listCart = (ArrayList<CartTempModule>) cartItemTempDAO.getALL(idUser, 1);
        double price = cartItemTempDAO.getToTalPrice();
        tvToTalCost = findViewById(R.id.tvToTalCost);

//        tvToTalCost.setText(String.format("%.2f", price));
        double abc = 0.0;
        for (int i = 0; i < listCart.size(); i++) {
            abc += cartItemTempDAO.getToTal(i, 0);
        }
        tvFeeTransport = findViewById(R.id.tvFeeTransport);
        double feeTransport = abc * 0.05;
        tvFeeTransport.setText(String.format("%.2f", feeTransport));
        double total = abc + feeTransport;
        tvVoucher = findViewById(R.id.tvVoucher);
        if (check > 0) {
            Intent intent = getIntent();
            if (intent != null) {
                Bundle bundle = intent.getBundleExtra("discount");
                if (bundle != null) {
                    int discount = bundle.getInt("value");
                    voucher = discount;
                    tvVoucher.setText("Voucher(" + discount + "%)");
                    double voucher = total * discount /100;
                    tvValueVoucher.setText("- " + String.format("%.2f", voucher));
                    tvToTalCost.setText(String.format("%.2f", (total-voucher)));
                }

            }

        }

    }

    @Override
    public void onStart() {
        super.onStart();
        SharedPreferences pref = getSharedPreferences("USER_FILE", MODE_PRIVATE);
        String email = pref.getString("EMAIL", "");
        int idUser = usersDAO.getIDUser(email);
        cartItemTempDAO = new CartItemTempDAO(getApplicationContext());
        listCart = (ArrayList<CartTempModule>) cartItemTempDAO.getALL(idUser);
        for (int i = 0; i < listCart.size(); i++) {
            chuoi += "● " + listCart.get(i).name + "   --   SL: " + listCart.get(i).quantities + "\n";
        }

    }
}