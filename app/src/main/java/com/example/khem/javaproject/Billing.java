package com.example.khem.javaproject;

import com.stripe.exception.StripeException;
import com.stripe.model.Charge;

import java.util.HashMap;
import java.util.Map;

public class Billing {
    public Billing() {
    }



    public static void payment(String customer, String currency, int t){
        com.stripe.Stripe.apiKey = "sk_test_INBDjLiewupY7DvRv1N2UCRK";

        String total = Integer.toString((t+50));

        Map<String,Object> chargeParam = new HashMap<String, Object>();
        chargeParam.put("amount",(total+50)); // 0.5 charge for sevice
        chargeParam.put("currency",currency);
        chargeParam.put("customer",customer);

        try {
            Charge.create(chargeParam);
        } catch (StripeException e) {
            e.printStackTrace();
        }
    }
}
