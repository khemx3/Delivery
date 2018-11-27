package com.example.khem.javaproject;

import android.os.AsyncTask;

import com.stripe.exception.StripeException;
import com.stripe.model.Charge;
import com.stripe.model.Customer;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class Billing {
    public Billing() {
    }

    String currency , customer;
    int t;

    private class HttpTask extends AsyncTask<String, Integer, String> {

        protected String doInBackground(String... params)   {
            com.stripe.Stripe.apiKey = "sk_test_INBDjLiewupY7DvRv1N2UCRK";

            String total = Integer.toString((t+50));

            Map<String,Object> chargeParam = new HashMap<String, Object>();
            chargeParam.put("amount",(total+50)); // 0.5 charge for sevice
            chargeParam.put("currency",currency);
            chargeParam.put("customer",customer);

            try {
                Charge.create(chargeParam);
                return "success";
            } catch (StripeException e) {
                e.printStackTrace();
            }
            return "fail";
        }

        protected void onProgressUpdate(Integer... values) {

        }

        protected void onPostExecute(String result)  {

        }
    }



    public void payment(String customer, String currency, int t){
        try {
            this.currency = currency;
            this.customer = customer;
            this.t = t;
            new HttpTask().execute().get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
