package com.example.khem.javaproject;

import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.example.khem.javaproject.Database.Database;
import com.example.khem.javaproject.Model.Food;
import com.example.khem.javaproject.Model.Order;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class food_details extends AppCompatActivity {

    TextView foodName, foodPrice, foodDescription;
    ImageView foodImage;
    CollapsingToolbarLayout collapsingToolbarLayout;
    FloatingActionButton btnCart;
    ElegantNumberButton numberButton;

    String foodId = "";

    //Firebase Database
    FirebaseDatabase database;
    DatabaseReference foods;

    Food currentFood;
    Bundle food;

    private Database mydb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_details);

        food = getIntent().getExtras();
        //Firebase
        database = FirebaseDatabase.getInstance();
        foods = database.getReference("menu");

        //Init view
        numberButton = (ElegantNumberButton)findViewById(R.id.number_button);
        btnCart = findViewById(R.id.btn_Cart);

        btnCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle currentFood = food;
                new Database(getBaseContext()).addToCart(new Order(
                        currentFood.get("id").toString(),
                        currentFood.get("name").toString(),
                        numberButton.getNumber(),
                        currentFood.get("price").toString()
                ));
                Toast.makeText(food_details.this, food.get("name").toString() + " added", Toast.LENGTH_SHORT).show();
            }
        });

        foodDescription = findViewById(R.id.food_description);
        foodName = findViewById(R.id.food_name);
        foodPrice = findViewById(R.id.food_price);
        foodImage = findViewById(R.id.food_image);

        collapsingToolbarLayout = findViewById(R.id.collapsing);

        getDetailsFood(food.get("id").toString());
    }

    // getDetailsFood() method
    private void getDetailsFood(String foodId) {

        Bundle currentFood = food;
        Picasso.get().load(currentFood.get("image").toString()).into(foodImage);
        collapsingToolbarLayout.setTitle(currentFood.get("name").toString());

        foodPrice.setText(currentFood.get("price").toString());
        foodName.setText(currentFood.get("name").toString());
        foodDescription.setText(currentFood.get("description").toString());
    }
}
