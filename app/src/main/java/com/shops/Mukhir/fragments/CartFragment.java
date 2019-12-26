package com.shops.Mukhir.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.graphics.drawable.AnimatedVectorDrawableCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.ethanhua.skeleton.Skeleton;
import com.ethanhua.skeleton.ViewSkeletonScreen;
import com.shops.Mukhir.HomeActivity;
import com.shops.Mukhir.R;
import com.shops.Mukhir.activities.AccountPaymentActivity;
import com.shops.Mukhir.activities.SaveDeliveryLocationActivity;
import com.shops.Mukhir.activities.SetDeliveryLocationActivity;
import com.shops.Mukhir.adapter.ViewCartAdapter;
import com.shops.Mukhir.build.api.ApiClient;
import com.shops.Mukhir.build.api.ApiInterface;
import com.shops.Mukhir.helper.GlobalData;
import com.shops.Mukhir.helper.ConnectionHelper;
import com.shops.Mukhir.helper.CustomDialog;
import com.shops.Mukhir.models.AddCart;
import com.shops.Mukhir.models.Cart;
import com.shops.Mukhir.utils.Utils;
import com.robinhood.ticker.TickerUtils;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.shops.Mukhir.adapter.ViewCartAdapter.bottomSheetDialogFragment;


/**
 * Created by santhosh@appoets.com on 22-08-2017.
 */
public class CartFragment extends Fragment {

    @BindView(R.id.re)
    RelativeLayout re;
    @BindView(R.id.order_item_rv)
    RecyclerView orderItemRv;

    @BindView(R.id.map_marker_image)
    ImageView mapMarkerImage;
    @BindView(R.id.location_error_title)
    TextView locationErrorTitle;
    @BindView(R.id.location_error_sub_title)
    TextView locationErrorSubTitle;
    @BindView(R.id.add_address_btn)
    Button addAddressBtn;
    @BindView(R.id.dummy_image_view)
    ImageView dummyImageView;
    @BindView(R.id.total_amount)
    TextView totalAmount;
    @BindView(R.id.buttonLayout)
    LinearLayout buttonLayout;
    @BindView(R.id.address_header)
    TextView addressHeader;
    @BindView(R.id.address_detail)
    TextView addressDetail;
    @BindView(R.id.address_delivery_time)
    TextView addressDeliveryTime;
    @BindView(R.id.add_address_txt)
    TextView addAddressTxt;
    @BindView(R.id.bottom_layout)
    LinearLayout bottomLayout;
    public static RelativeLayout dataLayout;
    public static RelativeLayout errorLayout;
    @BindView(R.id.location_info_layout)
    LinearLayout locationInfoLayout;
    @BindView(R.id.location_error_layout)
    RelativeLayout locationErrorLayout;
    @BindView(R.id.restaurant_image)
    ImageView restaurantImage;
    @BindView(R.id.restaurant_name)
    TextView restaurantName;
    @BindView(R.id.restaurant_description)
    TextView restaurantDescription;
    @BindView(R.id.proceed_to_pay_btn)
    Button proceedToPayBtn;
    @BindView(R.id.selected_address_btn)
    Button selectedAddressBtn;
    @BindView(R.id.error_layout_description)
    TextView errorLayoutDescription;
    @BindView(R.id.use_wallet_chk_box)
    CheckBox useWalletChkBox;
    @BindView(R.id.amount_txt)
    TextView amountTxt;
    @BindView(R.id.custom_notes)
    TextView customNotes;
    @BindView(R.id.wallet_layout)
    LinearLayout walletLayout;
    private Context context;
    private ViewGroup toolbar;
    private View toolbarLayout;
    AnimatedVectorDrawableCompat avdProgress;
    //Animation number
    private static final char[] NUMBER_LIST = TickerUtils.getDefaultNumberList();

    public static TextView itemTotalAmount, deliveryCharges, promoCodeApply, discountAmount, serviceTax, payAmount;

    Fragment orderFullViewFragment;
    FragmentManager fragmentManager;
    //Orderitem List
    public  static List<Cart> viewCartItemList;

    int priceAmount = 0;
    public int tamt=0;
    int discount = 0;
    public static int deliveryChargeValue = 0;
    public static int tax = 0;
    int itemCount = 0;
    int itemQuantity = 0;
    int ADDRESS_SELECTION = 1;

    ApiInterface apiInterface = ApiClient.getRetrofit().create(ApiInterface.class);
    public  static ViewCartAdapter viewCartAdapter;
    CustomDialog customDialog;
    ViewSkeletonScreen skeleton;
    ConnectionHelper connectionHelper;
    Activity activity;

    public static HashMap<String,String> checkoutMap;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.context = getContext();
        this.activity = getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_cart, container, false);
        ButterKnife.bind(this, view);
        connectionHelper = new ConnectionHelper(context);

      /*  Intialize Global Values*/
        itemTotalAmount = (TextView) view.findViewById(R.id.item_total_amount);
        deliveryCharges = (TextView) view.findViewById(R.id.delivery_charges);
        promoCodeApply = (TextView) view.findViewById(R.id.promo_code_apply);
        discountAmount = (TextView) view.findViewById(R.id.discount_amount);
        serviceTax = (TextView) view.findViewById(R.id.service_tax);
        payAmount = (TextView) view.findViewById(R.id.total_amount);
        dataLayout = (RelativeLayout) view.findViewById(R.id.data_layout);
        errorLayout = (RelativeLayout) view.findViewById(R.id.error_layout);

        HomeActivity.updateNotificationCount(context, 0);
        customDialog = new CustomDialog(context);

        skeleton = Skeleton.bind(dataLayout)
                .load(R.layout.skeleton_fragment_cart)
                .show();
        viewCartItemList = new ArrayList<>();
        //Offer Restaurant Adapter
        orderItemRv.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
        orderItemRv.setItemAnimator(new DefaultItemAnimator());
        orderItemRv.setHasFixedSize(false);
        orderItemRv.setNestedScrollingEnabled(false);

        //Intialize address Value
        if (GlobalData.getInstance().selectedAddress != null && GlobalData.getInstance().selectedAddress.getLandmark() != null) {
            if (GlobalData.getInstance().addressList.getAddresses().size() == 1)
                addAddressTxt.setText(getString(R.string.add_address));
            else
                addAddressTxt.setText(getString(R.string.change_address));
            addAddressBtn.setBackgroundResource(R.drawable.button_corner_bg_green);
            addAddressBtn.setText(getResources().getString(R.string.proceed_to_pay));
            addressHeader.setText(GlobalData.getInstance().selectedAddress.getType());
            addressDetail.setText(GlobalData.getInstance().selectedAddress.getMapAddress());
            if (viewCartItemList != null && viewCartItemList.size() != 0)
                addressDeliveryTime.setText(viewCartItemList.get(0).getProduct().getShop().getEstimatedDeliveryTime().toString() + " Mins");
        } else if (GlobalData.getInstance().addressList != null) {
            addAddressBtn.setBackgroundResource(R.drawable.button_corner_bg_theme);
            addAddressBtn.setText(getResources().getString(R.string.add_address));
            locationErrorSubTitle.setText(GlobalData.getInstance().addressHeader);
            selectedAddressBtn.setVisibility(View.VISIBLE);
            locationErrorLayout.setVisibility(View.VISIBLE);
            locationInfoLayout.setVisibility(View.GONE);
        } else {
            if (GlobalData.getInstance().selectedAddress != null)
                locationErrorSubTitle.setText(GlobalData.selectedAddress.getMapAddress());
            else
                locationErrorSubTitle.setText(GlobalData.getInstance().addressHeader);
            locationErrorLayout.setVisibility(View.VISIBLE);
            selectedAddressBtn.setVisibility(View.GONE);
            locationInfoLayout.setVisibility(View.GONE);
        }
        return view;
    }

    private void getViewCart() {
        Call<AddCart> call = apiInterface.getViewCart();
        call.enqueue(new Callback<AddCart>() {
            @Override
            public void onResponse(Call<AddCart> call, Response<AddCart> response) {
                skeleton.hide();
                if (response != null && !response.isSuccessful() && response.errorBody() != null) {
                    errorLayout.setVisibility(View.VISIBLE);
                    dataLayout.setVisibility(View.GONE);
                    try {
                        JSONObject jObjError = new JSONObject(response.errorBody().string());
                        Toast.makeText(context, jObjError.optString("message"), Toast.LENGTH_LONG).show();
                    } catch (Exception e) {
//                        Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                } else if (response.isSuccessful()) {
                    try
                    {
                    customDialog.dismiss();
                    //get Item Count
                    itemCount = response.body().getProductList().size();
                    GlobalData.getInstance().notificationCount=response.body().getProductList().size();
                    if (itemCount == 0) {
                        errorLayout.setVisibility(View.VISIBLE);
                        dataLayout.setVisibility(View.GONE);
                        GlobalData.addCart=response.body();
                        GlobalData.addCart=null;
                    } else {
                        AddCart addCart=response.body();
                        errorLayout.setVisibility(View.GONE);
                        dataLayout.setVisibility(View.VISIBLE);
                        for (int i = 0; i < itemCount; i++) {
                            //Get Total item Quantity
                            itemQuantity = itemQuantity + response.body().getProductList().get(i).getQuantity();
                            //Get product price
                            if (response.body().getProductList().get(i).getProduct().getPrices().getPrice() != null)
                                priceAmount = priceAmount + (response.body().getProductList().get(i).getQuantity() * response.body().getProductList().get(i).getProduct().getPrices().getPrice());

                            if (addCart.getProductList().get(i).getCartAddons() != null && !addCart.getProductList().get(i).getCartAddons().isEmpty()) {
                                for (int j = 0; j < addCart.getProductList().get(i).getCartAddons().size(); j++) {
                                    priceAmount = priceAmount + (addCart.getProductList().get(i).getQuantity() * (addCart.getProductList().get(i).getCartAddons().get(j).getQuantity() *
                                            addCart.getProductList().get(i).getCartAddons().get(j).getAddonProduct().getPrice()));
                                }
                            }
                        }
                        GlobalData.notificationCount=itemQuantity;
                        GlobalData.getInstance().addCartShopId = response.body().getProductList().get(0).getProduct().getShopId();
                        //Set Payment details
                        String currency = response.body().getProductList().get(0).getProduct().getPrices().getCurrency();
                        itemTotalAmount.setText(currency + "" + priceAmount);
                        if(response.body().getProductList().get(0).getProduct().getShop().getOfferMinAmount()!=null){
                            if (response.body().getProductList().get(0).getProduct().getShop().getOfferMinAmount() < priceAmount) {
                                int offerPercentage = response.body().getProductList().get(0).getProduct().getShop().getOfferPercent();
                                discount = (int) (priceAmount * (offerPercentage * 0.01));
                            }
                        }
                        discountAmount.setText("- " + currency + "" + discount);
                        int topPayAmount = priceAmount - discount;
                        int tax = (int) Math.round(topPayAmount * (response.body().getTaxPercentage() * 0.01));
                        serviceTax.setText(currency +String.valueOf(tax));
                        topPayAmount = topPayAmount + response.body().getDeliveryCharges() + tax;
                        tamt=topPayAmount;
                        payAmount.setText(currency + "" + topPayAmount);
                        //Set Restaurant Details
                      /*  checkoutMap1.put("sender_name",""+response.body().getProductList().get(0).getProduct().getShop().getName());
                        checkoutMap1.put("sender_contact",""+response.body().getProductList().get(0).getProduct().getShop().getPhone());
                        checkoutMap1.put("pickup_address",""+response.body().getProductList().get(0).getProduct().getShop().getAddress());
                        checkoutMap1.put("pickup_lat",""+response.body().getProductList().get(0).getProduct().getShop().getLatitude());
                        checkoutMap1.put("pickup_lng",""+response.body().getProductList().get(0).getProduct().getShop().getLongitude());

                  */      restaurantName.setText(response.body().getProductList().get(0).getProduct().getShop().getName());
                        restaurantDescription.setText(response.body().getProductList().get(0).getProduct().getShop().getDescription());
                        String image_url = response.body().getProductList().get(0).getProduct().getShop().getAvatar();
                        Glide.with(context).load(image_url).placeholder(R.drawable.ic_restaurant_place_holder).dontAnimate()
                                .error(R.drawable.ic_restaurant_place_holder).into(restaurantImage);
                        deliveryChargeValue = response.body().getDeliveryCharges();
                        deliveryCharges.setText(response.body().getProductList().get(0).getProduct().getPrices().getCurrency() + "" + response.body().getDeliveryCharges().toString());
                        viewCartItemList.clear();
                        viewCartItemList=response.body().getProductList();
                        viewCartAdapter = new ViewCartAdapter(viewCartItemList, context);
                        orderItemRv.setAdapter(viewCartAdapter);
                    }}
                    catch (Exception e){
                        e.printStackTrace();
                    }

                }
            }

            @Override
            public void onFailure(Call<AddCart> call, Throwable t) {
                errorLayout.setVisibility(View.VISIBLE);
                dataLayout.setVisibility(View.GONE);
            }
        });
    }


    @Override
    public void onResume() {
        super.onResume();
        priceAmount = 0;
        discount = 0;
        itemCount = 0;
        itemQuantity = 0;
        if (GlobalData.profileModel != null) {
            int money = GlobalData.profileModel.getWalletBalance();
            dataLayout.setVisibility(View.VISIBLE);
            errorLayout.setVisibility(View.GONE);
            skeleton.show();
            errorLayoutDescription.setText(getResources().getString(R.string.cart_error_description));
            if (connectionHelper.isConnectingToInternet()) {
                getViewCart();
            } else {
                Utils.displayMessage(activity, context, getString(R.string.oops_connect_your_internet));
            }
            if (money > 0) {
//                amountTxt.setText(numberFormat.format(money));
                amountTxt.setText(GlobalData.currencySymbol+" "+money);
                walletLayout.setVisibility(View.VISIBLE);
            } else {
                walletLayout.setVisibility(View.INVISIBLE);
            }
        }
        else {
            dataLayout.setVisibility(View.GONE);
            errorLayout.setVisibility(View.VISIBLE);
            errorLayoutDescription.setText(getResources().getString(R.string.please_login_and_order_dishes));
        }
        if(bottomSheetDialogFragment!=null)
            bottomSheetDialogFragment.dismiss();

    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (toolbar != null) {
            toolbar.removeView(toolbarLayout);
        }
    }


    public void FeedbackDialog() {
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.feedback);
        EditText commentEdit = (EditText) dialog.findViewById(R.id.comment);

        Button submitBtn = (Button) dialog.findViewById(R.id.submit);
        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        System.out.println("CartFragment");
        toolbar = (ViewGroup) getActivity().findViewById(R.id.toolbar);
        if (toolbar != null) {
            toolbar.setVisibility(View.GONE);
            dummyImageView.setVisibility(View.VISIBLE);
        } else {

            dummyImageView.setVisibility(View.GONE);
        }

    }


    @OnClick({R.id.add_address_txt, R.id.add_address_btn, R.id.selected_address_btn, R.id.proceed_to_pay_btn})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.add_address_txt:
                /**  If address is empty */
                if (addAddressTxt.getText().toString().equalsIgnoreCase(getResources().getString(R.string.change_address))) {
                    startActivityForResult(new Intent(getActivity(), SetDeliveryLocationActivity.class).putExtra("get_address", true), ADDRESS_SELECTION);
                    getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.anim_nothing);
                }
                /**  If address is filled */
                else if (addAddressTxt.getText().toString().equalsIgnoreCase(getResources().getString(R.string.add_address))) {
                    startActivityForResult(new Intent(getActivity(), SaveDeliveryLocationActivity.class).putExtra("get_address", true), ADDRESS_SELECTION);
                    getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.anim_nothing);
                }
                break;
            case R.id.add_address_btn:
                /**  If address is empty */
                startActivityForResult(new Intent(getActivity(), SaveDeliveryLocationActivity.class).putExtra("get_address", true), ADDRESS_SELECTION);
                getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.anim_nothing);
                break;
            case R.id.selected_address_btn:
                /**  If address is filled */
                startActivityForResult(new Intent(getActivity(), SetDeliveryLocationActivity.class).putExtra("get_address", true), ADDRESS_SELECTION);
                getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.anim_nothing);

                break;

            case R.id.proceed_to_pay_btn:
                /**  If address is filled */
                if (connectionHelper.isConnectingToInternet()) {
//                    checkOut(GlobalData.getInstance().selectedAddress.getId());
                    checkoutMap= new HashMap<>();

                    Calendar c = Calendar.getInstance();
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    String strDate = sdf.format(c.getTime());
                    Log.d("Date","DATE : " + strDate);
                    // checkoutMap1.put("user_id",""+GlobalData.profileModel.getId());
               /*     checkoutMap1.put("user_id","5dbfc1da85c87d14e5115b63");
                    checkoutMap1.put("order_type","PICK");
                    checkoutMap1.put("item_description",""+customNotes.getText());
                    checkoutMap1.put("vehicle_type","BIKE");
                    checkoutMap1.put("receiver_name",""+GlobalData.profileModel.getName());
                    checkoutMap1.put("receiver_contact",""+GlobalData.profileModel.getPhone());
                    checkoutMap1.put("receiver_address",""+GlobalData.getInstance().selectedAddress.getMapAddress());
                    checkoutMap1.put("receiver_lat",""+GlobalData.getInstance().selectedAddress.getLatitude());
                    checkoutMap1.put("receiver_lng",""+GlobalData.getInstance().selectedAddress.getLongitude());
                    checkoutMap1.put("delivery_type","URGENT");
                    checkoutMap1.put("amount",""+tamt);
                    checkoutMap1.put("delivery_datetime",""+strDate);
                    checkoutMap1.put("remarks","Test");

 */
                    checkoutMap.put("user_address_id",""+GlobalData.getInstance().selectedAddress.getId());
                    checkoutMap.put("note",""+customNotes.getText());
                    if(useWalletChkBox.isChecked())
                        checkoutMap.put("wallet","1");
                    else
                        checkoutMap.put("wallet","0");
                    startActivity(new Intent(context, AccountPaymentActivity.class).putExtra("is_show_wallet",false).putExtra("is_show_cash",true).putExtra("tamount",tamt));
                    activity.overridePendingTransition(R.anim.anim_nothing, R.anim.slide_out_right);
                } else {
                    Utils.displayMessage(activity, context, getString(R.string.oops_connect_your_internet));
                }
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        System.out.print("CartFragment");
        if (requestCode == ADDRESS_SELECTION && resultCode == Activity.RESULT_OK) {
            System.out.print("CartFragment : Success");
            if (GlobalData.getInstance().selectedAddress != null) {
                locationErrorLayout.setVisibility(View.GONE);
                locationInfoLayout.setVisibility(View.VISIBLE);
                //Intialize address Value
                if (GlobalData.getInstance().selectedAddress != null && GlobalData.getInstance().selectedAddress.getLandmark() != null) {
                    if (GlobalData.getInstance().addressList.getAddresses().size() == 1)
                        addAddressTxt.setText(getString(R.string.add_address));
                    else
                        addAddressTxt.setText(getString(R.string.change_address));
                }
                addressHeader.setText(GlobalData.getInstance().selectedAddress.getType());
                addressDetail.setText(GlobalData.getInstance().selectedAddress.getMapAddress());
                addressDeliveryTime.setText(viewCartItemList.get(0).getProduct().getShop().getEstimatedDeliveryTime().toString() + " Mins");
            } else {
                locationErrorLayout.setVisibility(View.VISIBLE);
                locationInfoLayout.setVisibility(View.GONE);
            }
        } else if (requestCode == ADDRESS_SELECTION && resultCode == Activity.RESULT_CANCELED) {
            System.out.print("CartFragment : Failure");

        }
    }

    @OnClick(R.id.wallet_layout)
    public void onViewClicked() {
    }

    @OnClick(R.id.custom_notes)
    public void onAddCustomNotesClicked() {
        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

            final FrameLayout frameView = new FrameLayout(getActivity());
            builder.setView(frameView);

            final AlertDialog alertDialog = builder.create();
            LayoutInflater inflater = alertDialog.getLayoutInflater();
            View dialogView = inflater.inflate(R.layout.custom_note_popup, frameView);

            final EditText notes = (EditText) dialogView.findViewById(R.id.notes);
            notes.setText(customNotes.getText());
            Button submit = (Button) dialogView.findViewById(R.id.custom_note_submit);
            submit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    customNotes.setText(notes.getText());
                    alertDialog.dismiss();
                }
            });
            alertDialog.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}