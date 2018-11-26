package com.example.khem.javaproject;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.stripe.android.view.CardMultilineWidget;
import com.stripe.exception.StripeException;
import com.stripe.model.Token;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class Creaditregister extends Activity {

    CardMultilineWidget input;
    Button finish_card;
    String name;
    String id;

    public void Creaditregister(){ }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.creaditregister);

        name = getIntent().getStringExtra("name");

        input = (CardMultilineWidget)findViewById(R.id.card_input_widget);
        finish_card =  (Button)findViewById(R.id.finish_card);

        setId();


        finish_card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(input.getCard().validateCard()){
                    try {
                        addCard();
                        setLastLogin();
                    } catch (StripeException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

    }

    protected void setLastLogin(){
        DatabaseReference mDatabase;
        mDatabase = FirebaseDatabase.getInstance().getReference("lastLogin/email");

        mDatabase.setValue(name);

    }
    protected void setId() {
        DatabaseReference mDatabase;
        mDatabase = FirebaseDatabase.getInstance().getReference("User/"+name);

        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot childsnap : dataSnapshot.getChildren()){
                    if(childsnap.getKey().equals("customer_id"))
                        id = childsnap.getValue().toString();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private class HttpTask extends AsyncTask<String, Integer, String> {

        protected String doInBackground(String... params)   {
            try {

                com.stripe.Stripe.apiKey = "sk_test_INBDjLiewupY7DvRv1N2UCRK";

                String customer_id = getId();
                com.stripe.model.Customer customer = com.stripe.model.Customer.retrieve(id);

                Map<String,Object> cardparam = new HashMap<String, Object>();
                cardparam.put("number",input.getCard().getNumber());
                cardparam.put("exp_month",input.getCard().getExpMonth());
                cardparam.put("exp_year",input.getCard().getExpYear());
                cardparam.put("cvc",input.getCard().getCVC());

                Map<String,Object> tokenparam = new HashMap<String, Object>();
                tokenparam.put("card",cardparam);


                Token token = Token.create(tokenparam);
                Map<String,Object> source = new HashMap<String, Object>();
                source.put("source",token.getId());

                customer.getSources().create(source);

                return "Success";

            } catch (StripeException e) {
                e.printStackTrace();
            }


            return "Fail";
        }

        protected void onProgressUpdate(Integer... values) {

        }

        protected void onPostExecute(String result)  {

        }
    }


    protected void addCard() throws StripeException, ExecutionException, InterruptedException {
        new Creaditregister.HttpTask().execute().get();
    }

    public String getId() {
        return id;
    }


}