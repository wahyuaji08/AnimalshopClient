package com.example.wahyuajisantoso.animalshop;

import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.wahyuajisantoso.animalshop.Common.Common;
import com.example.wahyuajisantoso.animalshop.Model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rengwuxian.materialedittext.MaterialEditText;

public class signup extends AppCompatActivity {

    MaterialEditText edtphone, edtname, edtpass;
    Button btnSignUp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        edtname = (MaterialEditText) findViewById(R.id.edtname);
        edtphone = (MaterialEditText) findViewById(R.id.edtphone);
        edtpass = (MaterialEditText) findViewById(R.id.edtpass);

        btnSignUp = (Button) findViewById(R.id.btnSignUp);

        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference table_user = database.getReference("User");

        btnSignUp.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View view) {
                if (Common.isConnectedToInternet(getBaseContext())) {
                    final ProgressDialog mDialog = new ProgressDialog(signup.this);
                    mDialog.setMessage("Mohon Tunggu");
                    mDialog.show();

                    table_user.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            //cek pengguna yang sudah ada
                            if (dataSnapshot.child(edtphone.getText().toString()).exists()) {
                                mDialog.dismiss();

                                Toast.makeText(signup.this, "Sign Up Sukses", Toast.LENGTH_SHORT).show();
                            } else {
                                mDialog.dismiss();
                                User user = new User(edtname.getText().toString(), edtpass.getText().toString());
                                table_user.child(edtphone.getText().toString()).setValue(user);
                                Toast.makeText(signup.this, "Sign Up Sukses", Toast.LENGTH_SHORT).show();
                                finish();
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
                else
                {
                    Toast.makeText(signup.this, "Cek Koneksi Anda", Toast.LENGTH_SHORT).show();
                    return;
                }
            }
        });
    }
}

