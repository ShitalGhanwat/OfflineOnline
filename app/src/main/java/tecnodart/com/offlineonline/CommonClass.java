package tecnodart.com.offlineonline;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.telephony.SmsManager;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class CommonClass {
    private static final String TAG="common";
    Context context;
    private boolean mLocationPermissionGranted;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    public static final int REQUEST_ID_MULTIPLE_PERMISSIONS = 1;

    public Double latitude,longitude;
    Activity activity;
    public CommonClass(Context context, Activity activity)
    {
        this.context=context;
        this.activity=activity;
        if(checkAndRequestPermissions());

    }
    public void initializeLocation()

    {
        getLocationPermission();
        getLocationOffline();
    }
    public boolean isOnline() {

        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            return true;
        } else {
            return false;
        }
    }
    public void sendSMS(String phoneNumber, String message)
    {
        Log.v("phoneNumber",phoneNumber);
        Log.v("message",message);
        // Log.v("i",Integer.toString(i));
        Log.d(TAG,"sendSMS executed");
        PendingIntent pi = PendingIntent.getActivity(context, 0,
                new Intent(context,Dummy.class), 0);

        SmsManager sms = SmsManager.getDefault();

        sms.sendTextMessage(phoneNumber, null, message, pi, null);


    }
    private void getLocationOffline()
    {
        Log.d(TAG,"getLocationOffline exectued!");
        LocationManager lm;
        Log.d(TAG,"#1");
        lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        Log.d(TAG,"#2");
        Location net_loc = null;
        Log.d(TAG,"#3");
        try {
            Log.d(TAG,"#4");
            // lm.requestLocationUpdates();
            net_loc = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            if(net_loc==null)
            {
                Log.d(TAG,"net_loc is null");
            }
            Log.d(TAG,"#5");
            latitude=net_loc.getLatitude();
            // latitude=18.5614668;
            Log.d(TAG,"#6");
            longitude=net_loc.getLongitude();
            // longitude=73.9324918;
            Log.d(TAG,"#7");
        }catch(SecurityException s)
        {
            Log.d(TAG,"Permission Denied");
        }
    }
    /**
     * Prompts the user for permission to use the device location.
     */
    private void getLocationPermission() {
        /*
         * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */
        Log.d(TAG,"getLocationPermission executed");
        if (ContextCompat.checkSelfPermission(activity,
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(activity,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }
    private  boolean checkAndRequestPermissions() {
        int permissionSendMessage = ContextCompat.checkSelfPermission(context,
                android.Manifest.permission.SEND_SMS);

        int receiveSMS = ContextCompat.checkSelfPermission(context,
                android.Manifest.permission.RECEIVE_SMS);

        int readSMS = ContextCompat.checkSelfPermission(context,
                android.Manifest.permission.READ_SMS);
        int getLocation=ContextCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION);
        List<String> listPermissionsNeeded = new ArrayList<>();

        if (receiveSMS != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(android.Manifest.permission.RECEIVE_MMS);
        }
        if (readSMS != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(android.Manifest.permission.READ_SMS);
        }
        if (permissionSendMessage != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(android.Manifest.permission.SEND_SMS);
        }
        if(getLocation!=PackageManager.PERMISSION_GRANTED)
        {
            listPermissionsNeeded.add(android.Manifest.permission.ACCESS_FINE_LOCATION);
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(activity,
                    listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]),
                    REQUEST_ID_MULTIPLE_PERMISSIONS);
            return false;
        }
        return true;
    }
}
