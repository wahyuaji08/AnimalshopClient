package com.example.wahyuajisantoso.animalshop;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.wahyuajisantoso.animalshop.Common.Common;
import com.example.wahyuajisantoso.animalshop.Database.Database;
import com.example.wahyuajisantoso.animalshop.Model.Order;
import com.example.wahyuajisantoso.animalshop.Model.Request;
import com.example.wahyuajisantoso.animalshop.ViewHolder.CartAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import info.hoang8f.widget.FButton;

public class Cart extends AppCompatActivity {

    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;

    FirebaseDatabase database;
    DatabaseReference requests;

    TextView txtTotalPrice;
    FButton btnPlace;

    List<Order> cart = new ArrayList<>();

    CartAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        //Firebase
        database = FirebaseDatabase.getInstance();
        requests=database.getReference("Requests");

        //Init
        recyclerView = (RecyclerView)findViewById(R.id.listCart);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        txtTotalPrice = (TextView)findViewById(R.id.total);
        btnPlace = (FButton)findViewById(R.id.btnPlaceOrder);

        btnPlace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (cart.size()>0)
                showAlertDialog();
                else
                    Toast.makeText(Cart.this, "Keranjang Anda Kosong", Toast.LENGTH_SHORT).show();


            }
        });

        loadListAnimal();
    }

    private void showAlertDialog() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(Cart.this);
        alertDialog.setTitle("Satu Step Lagi");
        alertDialog.setMessage("Masukkan Alamat Anda");

        LayoutInflater inflater = this.getLayoutInflater();
        View order_address_comment = inflater.inflate(R.layout.order_address_comment,null);

        final MaterialEditText edtAddress = (MaterialEditText)order_address_comment.findViewById(R.id.edtAddress);
        final MaterialEditText edtComment = (MaterialEditText)order_address_comment.findViewById(R.id.edtComment);

        alertDialog.setView(order_address_comment);
        alertDialog.setIcon(R.drawable.ic_shopping_cart_black_24dp);

        alertDialog.setPositiveButton("YA", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //request
                Request request = new Request(
                        Common.curretUser.getPhone(),
                        Common.curretUser.getName(),
                        edtAddress.getText().toString(),
                        txtTotalPrice.getText().toString(),
                        "0",//status
                        edtComment.getText().toString(),
                        cart
                );

                //Submit Firebase
                requests.child(String.valueOf(System.currentTimeMillis()))
                        .setValue(request);

                //hapus cart
                new Database(getBaseContext()).cleanCart();
                Toast.makeText(Cart.this, "Terima Kasih, Menunggu Konfirmasi", Toast.LENGTH_SHORT).show();
                finish();
            }
        });

        alertDialog.setNegativeButton("TIDAK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        alertDialog.show();
    }

    private void loadListAnimal(){
        cart = new Database(this).getCarts();
        adapter = new CartAdapter(cart,this);
        adapter.notifyDataSetChanged();
        recyclerView.setAdapter(adapter);

        //total price
        int total = 0;
        for(Order order:cart)
            total+=(Integer.parseInt(order.getPrice()))*(Integer.parseInt(order.getQuantity()));
        Locale localeID = new Locale("in","ID");
        NumberFormat fmt = NumberFormat.getCurrencyInstance(localeID);

        txtTotalPrice.setText(fmt.format(total));
    }

    @Override
    public boolean onContextItemSelected( MenuItem item) {
        if(item.getTitle().equals(Common.DELETE))
            deleteCart(item.getOrder());
        return true;

    }

    private void deleteCart(int position) {
        cart.remove(position);
        new Database(this).cleanCart();
        for (Order item:cart)
            new Database(this).addToCart(item);
        //refresh
        loadListAnimal();
    }
}
