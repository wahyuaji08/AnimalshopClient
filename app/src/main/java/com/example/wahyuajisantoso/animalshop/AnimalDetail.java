package com.example.wahyuajisantoso.animalshop;

import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.example.wahyuajisantoso.animalshop.Common.Common;
import com.example.wahyuajisantoso.animalshop.Database.Database;
import com.example.wahyuajisantoso.animalshop.Model.Animal;
import com.example.wahyuajisantoso.animalshop.Model.Order;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class AnimalDetail extends AppCompatActivity {

    TextView animal_name,animal_price,animal_description;
    ImageView animal_image;
    CollapsingToolbarLayout collapsingToolbarLayout;
    FloatingActionButton btnCart;
    ElegantNumberButton numberButton;

    String animalId="";

    FirebaseDatabase database;
    DatabaseReference animals;

    Animal currentAnimal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_animal_detail);

        //Firebase
        database = FirebaseDatabase.getInstance();
        animals = database.getReference("Animals");

        //Init View
        numberButton = (ElegantNumberButton)findViewById(R.id.number_button);
        btnCart = (FloatingActionButton)findViewById(R.id.btnCart);

        btnCart.setOnClickListener(new View.OnClickListener() {
         @Override
            public void onClick(View view) {
                 new Database(getBaseContext()).addToCart(new Order(
                         animalId,
                         currentAnimal.getName(),
                         numberButton.getNumber(),
                         currentAnimal.getPrice()


                 ));

             Toast.makeText(AnimalDetail.this, "Menambahkan Ke Keranjang", Toast.LENGTH_SHORT).show();
               }
        });

        animal_description = (TextView)findViewById(R.id.animal_description);
        animal_name = (TextView)findViewById(R.id.animal_name);
        animal_price = (TextView)findViewById(R.id.animal_price);
        animal_image = (ImageView)findViewById(R.id.img_animal);

        collapsingToolbarLayout = (CollapsingToolbarLayout)findViewById(R.id.collapsing);
        collapsingToolbarLayout.setExpandedTitleTextAppearance(R.style.ExpandedAppbar);
        collapsingToolbarLayout.setCollapsedTitleTextAppearance(R.style.CollapsedAppbar);

        //Get animal id dari intent
        if (getIntent() !=null)
            animalId = getIntent().getStringExtra("AnimalId");
        if(!animalId.isEmpty())
        {
            if (Common.isConnectedToInternet(getBaseContext()))
            getDetailAnimal(animalId);
            else
            {
                Toast.makeText(AnimalDetail.this, "Cek Koneksi Anda", Toast.LENGTH_SHORT).show();
                return;
            }
        }
    }

    private void getDetailAnimal(String animalId) {
        animals.child(animalId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                currentAnimal = dataSnapshot.getValue(Animal.class);

                //set image
                Picasso.with(getBaseContext()).load(currentAnimal.getImage())
                        .into(animal_image);

                collapsingToolbarLayout.setTitle(currentAnimal.getName());

                animal_price.setText(currentAnimal.getPrice());

                animal_name.setText(currentAnimal.getName());

                animal_description.setText(currentAnimal.getDescription());

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
