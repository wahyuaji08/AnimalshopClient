package com.example.wahyuajisantoso.animalshop;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.example.wahyuajisantoso.animalshop.Common.Common;
import com.example.wahyuajisantoso.animalshop.Model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rengwuxian.materialedittext.MaterialEditText;

import io.paperdb.Paper;


public class signin extends AppCompatActivity {

    EditText edtphone,edtpass;
    Button btnSignIn;
    com.rey.material.widget.CheckBox ckbRemember;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);

        edtpass = (MaterialEditText)findViewById(R.id.edtpass);
        edtphone = (MaterialEditText)findViewById(R.id.edtphone);
        btnSignIn = (Button)findViewById(R.id.btnSignIn);
        ckbRemember = (com.rey.material.widget.CheckBox) findViewById(R.id.ckbRemember);

        //Init Paper
        Paper.init(this);

        //Init Firebase
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference table_user = database.getReference("User");

        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (Common.isConnectedToInternet(getBaseContext())) {

                    //save user & pass
                    if (ckbRemember.isChecked())
                    {
                        Paper.book().write(Common.USER_KEY,edtphone.getText().toString());
                        Paper.book().write(Common.PWD_KEY,edtpass.getText().toString());
                    }


                    final ProgressDialog mDialog = new ProgressDialog(signin.this);
                    mDialog.setMessage("Mohon Tunggu");
                    mDialog.show();

                    table_user.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            //Cek User
                            if (dataSnapshot.child(edtphone.getText().toString()).exists()) {
                                //Informasi User
                                mDialog.dismiss();
                                User user = dataSnapshot.child(edtphone.getText().toString()).getValue(User.class);
                                user.setPhone(edtphone.getText().toString()); //set phone
                                if (user.getPassword().equals(edtpass.getText().toString())) {
                                    {
                                        Intent homeIntent = new Intent(signin.this, Home.class);
                                        Common.curretUser = user;
                                        startActivity(homeIntent);
                                        finish();
                                    }
                                } else {
                                    Toast.makeText(signin.this, "Password Salah", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                mDialog.dismiss();
                                Toast.makeText(signin.this, "User Tidak Terdaftar", Toast.LENGTH_SHORT).show();
                            }
                        }


                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
                else
                {
                    Toast.makeText(signin.this, "Cek Koneksi Anda", Toast.LENGTH_SHORT).show();
                    return;
                }
            }

        });
    }
}