package com.example.khem.javaproject;

import com.stripe.exception.StripeException;
import com.stripe.model.Charge;

import java.util.HashMap;
import java.util.Map;

public class Billing {
    public Billing() {
    }



    public void payment(String customer, String currency, int t, Map<String,Map<String,String>> detail){
        com.stripe.Stripe.apiKey = "sk_test_INBDjLiewupY7DvRv1N2UCRK";

        String total = Integer.toString((t+50));
        String order_detail = "";
        for(Map.Entry<String,Map<String,String>> sub : detail.entrySet()){
            order_detail += sub.getKey();
            for( Map.Entry<String,String> insub : sub.getValue().entrySet()){
                order_detail += " "+insub.getKey()+" "+insub.getValue()+" || ";
            }
        }
        order_detail += "Amount : "+total+"\n";

        Map<String,Object> chargeParam = new HashMap<String, Object>();
        chargeParam.put("amount",(total+50)); // 0.5 charge for sevice
        chargeParam.put("currency",currency);
        chargeParam.put("customer",customer);
        chargeParam.put("description",order_detail);

        try {
            Charge.create(chargeParam);
        } catch (StripeException e) {
            e.printStackTrace();
        }
    }



    public static void main(String[] args){
        Billing b = new Billing();

        Map<String,Map<String,String>> m = new HashMap<String,Map<String,String>>();
        Map<String,String> m2 = new HashMap<String,String>();
        m2.put("3","300");
        m.put("Fire Rice",m2);

        b.payment("cus_E2M0NXMoitNqxM","usd",100,m);
    }
}
