package com.example.khem.javaproject;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.safetynet.SafetyNet;
import com.google.android.gms.safetynet.SafetyNetApi;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.stripe.exception.StripeException;
import com.stripe.model.Customer;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;

public class Register extends Activity {

    Bundle facebookdata;
    String userResponseToken;
    String customer_id;

    EditText name;
    EditText pass;
    EditText repass;
    EditText email;
    EditText phone;

    Button next;
    Button detail;

    public Register(){ }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.register);

        name = (EditText)findViewById(R.id.name);
        pass = (EditText)findViewById(R.id.password);
        repass = (EditText)findViewById(R.id.repassword);
        email = (EditText)findViewById(R.id.email);
        phone = (EditText)findViewById(R.id.phone);

        Button next  = (Button)findViewById(R.id.next);
        Button detail = (Button)findViewById(R.id.detail);



        this.facebookdata = getIntent().getExtras();

        if(facebookdata != null){
            try {
                String name_data = facebookdata.get("first_name").toString();
                String last_data = facebookdata.get("last_name").toString();
                String email_data = facebookdata.get("email").toString();



                name.setText(name_data + " " + last_data);
                email.setText(email_data);


            }
            catch (Exception e){
                System.out.println(e);}
        }


        detail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dialog pass_suggest = new Dialog(Register.this);
                pass_suggest.setContentView(R.layout.password_suggest);

                pass_suggest.show();
            }
        });


        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String error_msg = validateData();
                if(error_msg == null){
                    SafetyNet.getClient(Register.this).verifyWithRecaptcha("6LfZxnwUAAAAAB4oOGMyj-UZ17-1a5Nv3zeW6oI6")
                            .addOnSuccessListener((Activity) Register.this,
                                    new OnSuccessListener<SafetyNetApi.RecaptchaTokenResponse>() {
                                        @Override
                                        public void onSuccess(SafetyNetApi.RecaptchaTokenResponse response) {
                                            // Indicates communication with reCAPTCHA service was
                                            // successful.
                                            userResponseToken = response.getTokenResult();
                                            if (!userResponseToken.isEmpty()) {
                                                // Validate the user response token using the
                                                // reCAPTCHA siteverify API.

                                                try {
                                                    writeToStripe();
                                                    writeTofireBase();
                                                } catch (StripeException e) {
                                                    e.printStackTrace();
                                                } catch (InterruptedException e) {

                                                } catch (ExecutionException e) {
                                                    e.printStackTrace();
                                                }

                                                sendRequest();

                                            }
                                        }
                                    })
                            .addOnFailureListener((Activity) Register.this, new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    if (e instanceof ApiException) {
                                        // An error occurred when communicating with the
                                        // reCAPTCHA service. Refer to the status code to
                                        // handle the error appropriately.
                                        ApiException apiException = (ApiException) e;
                                        int statusCode = apiException.getStatusCode();
                                        Log.d("recaptcha", "Error: " + CommonStatusCodes
                                                .getStatusCodeString(statusCode));
                                    } else {
                                        // A different, unknown type of error occurred.
                                        Log.d("recaptcha2", "Error: " + e.getMessage());
                                    }
                                }
                            });}
                else
                    errorDialog(error_msg);
            }
        });
    }

    public void sendRequest()  {

        String url = "https://www.google.com/recaptcha/api/siteverify";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject obj = new JSONObject(response);
                            if (obj.getString("success").equals("true")){
                                creditCard();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(Register.this, error.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }) {

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("secret","6LfZxnwUAAAAACbipld32Hd_dUT8Wja-_Q6Th_h6");
                params.put("response", userResponseToken);
                return params;
            }
        };
        AppController.getInstance(this).addToRequestQueue(stringRequest);

    }

    protected String validateData(){
        String pass_test = pass.getText().toString();
        if( name.getText().toString().equals("") ||
                email.getText().toString().equals("") ||
                pass.getText().toString().equals("")||
                repass.getText().toString().equals("")||
                phone.getText().toString().equals(""))
            return "Blank input";
        else if (!(pass_test.matches(".*[A-Za-z].*") && pass_test.matches(".*[0-9].*") && pass_test.matches("[A-Za-z0-9]*") && pass_test.length()>= 8))
            return "Password not following condition";
        else if(!(pass_test.equals(repass.getText().toString())))
            return "Password and Re-Password are not match";
        return null;
    };

    protected void creditCard(){
        Intent newpage = new Intent(this,Creaditregister.class);
        newpage.putExtra("name",name.getText().toString());

        startActivity(newpage);
    }

    protected void errorDialog(String msg){
        ShowError missing = new ShowError(Register.this,msg);
        missing.setContentView(R.layout.missingdialog);

        missing.show();
    }

    protected void writeTofireBase(){
        DatabaseReference mDatabase;
        mDatabase = FirebaseDatabase.getInstance().getReference();

        HashMap<String, Object> result = new HashMap<>();
        result.put("name", name.getText().toString());
        result.put("pass", pass.getText().toString());
        result.put("email",email.getText().toString());
        result.put("phone", phone.getText().toString());
        result.put("customer_id",customer_id);

        mDatabase.child("User").child(email.getText().toString()).setValue(result);
    }


    private class HttpTask extends AsyncTask<String, Integer, String> {

        protected String doInBackground(String... params)   {
            com.stripe.Stripe.apiKey = "sk_test_INBDjLiewupY7DvRv1N2UCRK";

            Map<String, Object> customerParams = new HashMap<String, Object>();
            customerParams.put("description", "Customer for "+ email.getText().toString());
            customerParams.put("email",email.getText().toString());


            try {
                Customer newCustomer =  Customer.create(customerParams);
                setCustomer_id(newCustomer.getId());
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


    protected void writeToStripe() throws StripeException, ExecutionException, InterruptedException {
        new Register.HttpTask().execute().get();
    }

    public synchronized void setCustomer_id(String customer_id) {
        this.customer_id = customer_id;
    }
}
