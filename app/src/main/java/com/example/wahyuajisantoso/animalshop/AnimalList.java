package com.example.wahyuajisantoso.animalshop;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Toast;

import com.example.wahyuajisantoso.animalshop.Common.Common;
import com.example.wahyuajisantoso.animalshop.Interface.ItemClickListener;
import com.example.wahyuajisantoso.animalshop.Model.Animal;
import com.example.wahyuajisantoso.animalshop.ViewHolder.AnimaViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mancj.materialsearchbar.MaterialSearchBar;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class AnimalList extends AppCompatActivity {

    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;

    FirebaseDatabase database;
    DatabaseReference animalList;

    String categoryId="";

    FirebaseRecyclerAdapter<Animal,AnimaViewHolder> adapter;

    //Search
    FirebaseRecyclerAdapter<Animal,AnimaViewHolder> searchadapter;
    List<String> suggestList = new ArrayList<>();
    MaterialSearchBar materialSearchBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_animal_list);

        //Firebase
        database = FirebaseDatabase.getInstance();
        animalList = database.getReference("Animals");

        recyclerView = (RecyclerView)findViewById(R.id.recycler_animal);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        //Intent
        if (getIntent() != null)
            categoryId = getIntent().getStringExtra("CategoryId");
        if(!categoryId.isEmpty() && categoryId != null)
        {
            if (Common.isConnectedToInternet(getBaseContext()))
            loadListAnimal(categoryId);
            else
            {
                Toast.makeText(AnimalList.this, "Cek Koneksi Anda", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        //search
        materialSearchBar = (MaterialSearchBar)findViewById(R.id.searchBar);
        materialSearchBar.setHint("Pencarian");
       //materialSearchBar.setSpeechMode(false);
        loadSuggest();
        materialSearchBar.setLastSuggestions(suggestList);
        materialSearchBar.setCardViewElevation(10);
        materialSearchBar.addTextChangeListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                List<String> suggest = new ArrayList<String>();
                for (String search:suggestList)
                {
                    if (search.toLowerCase().contains(materialSearchBar.getText().toLowerCase()))
                        suggest.add(search);
                }
                materialSearchBar.setLastSuggestions(suggest);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        materialSearchBar.setOnSearchActionListener(new MaterialSearchBar.OnSearchActionListener() {
            @Override
            public void onSearchStateChanged(boolean enabled) {
                //kalo search bar keluar
                //restore original adapter
                if (!enabled)
                    recyclerView.setAdapter(adapter);
            }

            @Override
            public void onSearchConfirmed(CharSequence text) {
                //kalo search udah selesai
                //tampil search resultnya
                startSearch(text);
            }

            @Override
            public void onButtonClicked(int buttonCode) {

            }
        });
    }

    private void startSearch(CharSequence text) {
        searchadapter = new FirebaseRecyclerAdapter<Animal, AnimaViewHolder>(
                Animal.class,
                R.layout.animal_item,
                AnimaViewHolder.class,
                animalList.orderByChild("name").equalTo(text.toString())
        ) {

            @Override
            protected void populateViewHolder(AnimaViewHolder viewHolder, Animal model, int position) {
                viewHolder.animal_name.setText(model.getName());
                Picasso.with(getBaseContext()).load(model.getImage())
                        .into(viewHolder.animal_image);

                final Animal local = model;
                viewHolder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {
                        //activity baru
                        Intent animalDetail = new Intent(AnimalList.this,AnimalDetail.class);
                        animalDetail.putExtra("AnimalId",searchadapter.getRef(position).getKey()); //mengirim animal id ke action baru
                        startActivity(animalDetail);
                    }
                });
            }
        };
        recyclerView.setAdapter(searchadapter);//set adapter buat recycler view di search result
    }

    private void loadSuggest() {
        animalList.orderByChild("menuId").equalTo(categoryId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot postSnapshot : dataSnapshot.getChildren())
                        {
                            Animal item = postSnapshot.getValue(Animal.class);
                            suggestList.add(item.getName());//menambahkan suggest animal list
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    private void loadListAnimal(String categoryId) {
        adapter = new FirebaseRecyclerAdapter<Animal, AnimaViewHolder>(Animal.class,
                R.layout.animal_item,
                AnimaViewHolder.class,
                animalList.orderByChild("menuId").equalTo(categoryId) //select animal id
                ) {
            @Override
            protected void populateViewHolder(AnimaViewHolder viewHolder, Animal model, int position) {
               viewHolder.animal_name.setText(model.getName());
                Picasso.with(getBaseContext()).load(model.getImage())
                        .into(viewHolder.animal_image);

                final Animal local = model;
                viewHolder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {
                        //activity baru
                        Intent animalDetail = new Intent(AnimalList.this,AnimalDetail.class);
                        animalDetail.putExtra("AnimalId",adapter.getRef(position).getKey()); //mengirim animal id ke action baru
                        startActivity(animalDetail);
                    }
                });
            }
        };
        //set adapter
        recyclerView.setAdapter(adapter);
    }
}
