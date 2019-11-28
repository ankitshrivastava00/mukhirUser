package com.shops.Mukhir.activities;

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

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.JsonElement;
import com.shops.Mukhir.R;
import com.shops.Mukhir.build.api.ApiClient;
import com.shops.Mukhir.build.api.ApiInterface;
import com.shops.Mukhir.build.configure.BuildConfigure;
import com.shops.Mukhir.clientapimodel.postorder;
import com.shops.Mukhir.fragments.CartFragment;
import com.shops.Mukhir.helper.GlobalData;
import com.shops.Mukhir.models.Order;
import com.razorpay.Checkout;
import com.razorpay.PaymentResultListener;


import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

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
        name.setText("Mukhir");
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
            options.put("name", "Mukhir");
            options.put("description", "Demoing Charges");
            //You can omit the image option to fetch the image from dashboard
            options.put("image", "https://s3.amazonaws.com/rzp-mobile/images/rzp.png");
            options.put("currency", "INR");
            options.put("amount", bamt);
            JSONObject preFill = new JSONObject();
            preFill.put("email", "mukhirofficail@gmail.com");
            preFill.put("contact", "7999478752");
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
         //   Toast.makeText(this, "Payment Successful: " + razorpayPaymentID, Toast.LENGTH_SHORT).show();

            CartFragment.checkoutMap1.put("payment_type", "PPD");

            checkOutClient(CartFragment.checkoutMap1);
            Log.d("Payment Record",CartFragment.checkoutMap.toString());
            return;

        } catch (Exception e) {


            CartFragment.checkoutMap1.put("payment_type", "PPD");

            checkOutClient(CartFragment.checkoutMap1);
        }
    }

    private void checkOutClient(final HashMap<String, String> map) {
        try {

            StringRequest stringRequest = new StringRequest(Request.Method.POST, BuildConfigure.CLIENT_BASE_URL+"api/postorders",
                    new com.android.volley.Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {

                            Log.d("Resfdsf ",response +"");
                            try {
                                JSONObject object = new JSONObject(response.trim());
                                String isSuccessful =object.getString("isSuccessful").trim();;

                                if (isSuccessful.equalsIgnoreCase("true")) {
                                    // Log.e(TAG, "Exception in onPaymentSuccess", e);
                                    String message =object.getString("message").trim();;
                                    String role =object.getJSONObject("data").getString("role").trim();
                                    String id =object.getJSONObject("data").getString("id").trim();
                                    String reference_no =object.getJSONObject("data").getString("reference_no").trim();

                                    CartFragment.checkoutMap.put("payment_mode", "razorpay");
                                    CartFragment.checkoutMap.put("order_no", reference_no);
                                    checkOut(CartFragment.checkoutMap,reference_no);

                                } else {
                                 //   Toast.makeText(PaymentActivity.this,"Payment Failed",Toast.LENGTH_SHORT).show();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    },
                    new com.android.volley.Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.d("Resfdsferror ",error +"");
                        }
                    }) {
                @Override
                protected Map<String, String> getParams() {
                       /* Map<String, String> params = new HashMap<String, String>();
                        params.put("name", strEmail);
                        params.put("num", strEmail);
                        params.put("did", sd.getUserFcmId());*/
                    return map;
                }
            };

            stringRequest.setRetryPolicy(new DefaultRetryPolicy(60000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            RequestQueue requestQueue = Volley.newRequestQueue(PaymentActivity.this);
            requestQueue.add(stringRequest);

        } catch (Exception e) {
            e.printStackTrace();

        }
    }

    private void checkOut(HashMap<String, String> map,final String refrenceNo) {

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
                    startActivity(new Intent(context, CurrentOrderDetailActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP).putExtra("order_no",refrenceNo));
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
