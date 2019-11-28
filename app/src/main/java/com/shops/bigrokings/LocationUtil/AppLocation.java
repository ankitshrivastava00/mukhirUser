package com.shops.chiggys.LocationUtil;

import android.annotation.SuppressLint;
import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.widget.TextView;


import com.shops.chiggys.R;
import com.shops.chiggys.fragments.HomeFragment;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * @author Ajay Singh Lodhi
 *
 * Created on 0025 25-December-2017.
 */

public class AppLocation {

    private Context context;

    /**
     * Constructs a AppLocation with the Application context
     *
     * @param context the app context
     */
    public AppLocation(Context context) {
        this.context = context;
    }

    public boolean isGPSEnabled(){
        LocationManager lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        boolean gps_enabled = false;

        try {
            if (lm != null) {
                gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
            }
        } catch(Exception ignored) {}

        return gps_enabled;
    }

    /**
     * get address
     *
     * @param latitude coordinate
     * @param longitude coordinate
     * @return complete address in String format
     */
    public String getAddress(double latitude, double longitude){
        String errorMessage = "";

        Geocoder geocoder = new Geocoder(context, Locale.getDefault());

        List<Address> addresses = null;

        try {
            addresses = geocoder.getFromLocation(
                    latitude,
                    longitude,
                    // In this code, get just a single address.
                    1);
        } catch (IOException ioException) {
            // Catch network or other I/O problems.
            errorMessage = context.getString(R.string.error_address_operation_failed);
        } catch (IllegalArgumentException illegalArgumentException) {
            // Catch invalid latitude or longitude values.
            errorMessage = context.getString(R.string.error_invalid_location_used);
        }
        // Handle case where no address was found.
        if (addresses != null && addresses.size() > 0) {
            Address address = addresses.get(0);
            ArrayList<String> addressFragments = new ArrayList<>();

            // Fetch the address lines using getAddressLine,
            // join them, and send them to the thread.
            if (address.getMaxAddressLineIndex() > 0){
                for(int i = 0; i < address.getMaxAddressLineIndex(); i++) {
                    addressFragments.add(address.getAddressLine(i));
                }
            }else {
                try{
                    addressFragments.add(address.getAddressLine(0));
                }
                catch (Exception ignored){}
            }

            errorMessage = TextUtils.join(System.getProperty("line.separator"),
                    addressFragments);
        }

        return errorMessage;
    }

    @SuppressLint("StaticFieldLeak")
    public static class AddressBackgroundTask extends AsyncTask<Double, Void, String> {

        private Context context;
        private TextView tvAddress;

        public AddressBackgroundTask(Context context, TextView address) {
            this.context = context;
            this.tvAddress = address;
        }

        @Override
        protected String doInBackground(Double... doubles) {
            double latitude = doubles[0];
            double longitude = doubles[1];

            return new AppLocation(context).getAddress(latitude, longitude);
        }

        @Override
        protected void onPostExecute(String address) {
            super.onPostExecute(address);
            tvAddress.setText(address);
        }
    }
}
