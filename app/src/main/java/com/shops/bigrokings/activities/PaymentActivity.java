package com.shops.chiggys.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.shops.chiggys.R;
import com.shops.chiggys.build.api.ApiClient;
import com.shops.chiggys.build.api.ApiInterface;
import com.shops.chiggys.fragments.CartFragment;
import com.shops.chiggys.helper.CustomDialog;
import com.shops.chiggys.helper.GlobalData;
import com.shops.chiggys.models.Order;
import com.razorpay.Checkout;
import com.razorpay.PaymentResultListener;


import org.json.JSONObject;

import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PaymentActivity extends Activity implements PaymentResultListener {
    private static final String TAG = PaymentActivity.class.getSimpleName();
    public static ApiInterface apiInterface = ApiClient.getRetrofit().create(ApiInterface.class);
    public static Context context;

    private String bamt = "";
    private int tamt=0;

    private TextView name,amt;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_payment);
        tamt=getIntent().getIntExtra("tamt",0);
        bamt = String.valueOf(tamt*100);
        setContentView(R.layout.activity_payment);

        name =(TextView) findViewById(R.id.mname);
        amt = (TextView) findViewById(R.id.amount_txt);

        name.setText("chiggys");
        amt.setText("₹"+tamt);

        context = PaymentActivity.this;

        /*
         To ensure faster loading of the Checkout form,
          call this method as early as possible in your checkout flow.
         */
        Checkout.preload(getApplicationContext());

        // Payment button created by you in XML layout
        Button button = (Button) findViewById(R.id.btn_pay);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startPayment();
            }
        });
    }

    public void startPayment() {
        /*
          You need to pass current activity in order to let Razorpay create CheckoutActivity
         */
        final Activity activity = this;

        final Checkout co = new Checkout();

        try {
            JSONObject options = new JSONObject();
            options.put("name", "Sigma Infotech");
            options.put("description", "Demoing Charges");
            //You can omit the image option to fetch the image from dashboard
            options.put("image", "https://s3.amazonaws.com/rzp-mobile/images/rzp.png");
            options.put("currency", "INR");
            options.put("amount", bamt);

            JSONObject preFill = new JSONObject();
            preFill.put("email", "sigmainfotech01@gmail.com");
            preFill.put("contact", "9822994260");

            options.put("prefill", preFill);

            co.open(activity, options);
        } catch (Exception e) {
            Toast.makeText(activity, "Error in payment: " + e.getMessage(), Toast.LENGTH_SHORT)
                    .show();
            e.printStackTrace();
        }
    }

    /**
     * The name of the function has to be
     * onPaymentSuccess
     * Wrap your code in try catch, as shown, to ensure that this method runs correctly
     */
    @SuppressWarnings("unused")
    @Override
    public void onPaymentSuccess(String razorpayPaymentID) {


        try {
            Toast.makeText(this, "Payment Successful: " + razorpayPaymentID, Toast.LENGTH_SHORT).show();

            CartFragment.checkoutMap.put("payment_mode", "cash");
            checkOut(CartFragment.checkoutMap);

            Log.d("Payment Record",CartFragment.checkoutMap.toString());
            return;

        } catch (Exception e) {
           // Log.e(TAG, "Exception in onPaymentSuccess", e);
            CartFragment.checkoutMap.put("payment_mode", "razorpay");
            checkOut(CartFragment.checkoutMap);
        }
    }


    private void checkOut(HashMap<String, String> map) {

        Call<Order> call = apiInterface.postCheckout(map);
        call.enqueue(new Callback<Order>() {
            @Override
            public void onResponse(@NonNull Call<Order> call, @NonNull Response<Order> response) {

                if (response.isSuccessful()) {
                    GlobalData.addCart = null;
                    GlobalData.notificationCount = 0;
                    GlobalData.selectedShop = null;
                    GlobalData.profileModel.setWalletBalance(response.body().getUser().getWalletBalance());
                    GlobalData.isSelectedOrder = new Order();
                    GlobalData.isSelectedOrder = response.body();
                    startActivity(new Intent(context, CurrentOrderDetailActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                    finish();
                } else {
                    try {
                        JSONObject jObjError = new JSONObject(response.errorBody().string());
                        Toast.makeText(context, jObjError.optString("message"), Toast.LENGTH_LONG).show();
                    } catch (Exception e) {
                        Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<Order> call, @NonNull Throwable t) {
                Toast.makeText(PaymentActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
            }
        });
    }
    /**
     * The name of the function has to be
     * onPaymentError
     * Wrap your code in try catch, as shown, to ensure that this method runs correctly
     */
    @SuppressWarnings("unused")
    @Override
    public void onPaymentError(int code, String response) {
        try {
            Toast.makeText(this, "Payment failed: " + code + " " + response, Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Log.e(TAG, "Exception in onPaymentError", e);
        }
    }
}
