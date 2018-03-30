package tecnodart.com.offlineonline;

import android.*;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.telephony.SmsManager;
import android.text.Html;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.List;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link WeatherForecastFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link WeatherForecastFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class WeatherForecastFragment extends Fragment {
    TextView cityField, detailsField, currentTemperatureField, humidity_field, pressure_field, weatherIcon, updatedField;
    private static final String TAG="weather";
    Double latitude, longitude;
    Typeface weatherFont;
    private boolean mLocationPermissionGranted;
    String sms;
    String oo="ubi", AES="AES";

    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    public static final int REQUEST_ID_MULTIPLE_PERMISSIONS = 1;

    private OnFragmentInteractionListener mListener;

    public WeatherForecastFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment WeatherForecastFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static WeatherForecastFragment newInstance(String param1, String param2) {
        WeatherForecastFragment fragment = new WeatherForecastFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v= inflater.inflate(R.layout.fragment_weather_forecast, container, false);
        weatherFont = Typeface.createFromAsset(v.getContext().getAssets(), "fonts/weathericons-regular-webfont.ttf");

        cityField = (TextView)v.findViewById(R.id.city_field);
        updatedField = (TextView)v.findViewById(R.id.updated_field);
        detailsField = (TextView)v.findViewById(R.id.details_field);
        currentTemperatureField = (TextView)v.findViewById(R.id.current_temperature_field);
        humidity_field = (TextView)v.findViewById(R.id.humidity_field);
        pressure_field = (TextView)v.findViewById(R.id.pressure_field);
        weatherIcon = (TextView)v.findViewById(R.id.weather_icon);
        weatherIcon.setTypeface(weatherFont);
        if (checkAndRequestPermissions()) {
            // carry on the normal flow, as the case of  permissions  granted.
            if(isOnline()) {
                Function.placeIdTask asyncTask = new Function.placeIdTask(new Function.AsyncResponse() {
                    public void processFinish(String weather_city, String weather_description, String weather_temperature, String weather_humidity, String weather_pressure, String weather_updatedOn, String weather_iconText, String sun_rise) {

                        cityField.setText(weather_city);
                        updatedField.setText(weather_updatedOn);
                        detailsField.setText(weather_description);
                        currentTemperatureField.setText(weather_temperature);
                        humidity_field.setText("Humidity: " + weather_humidity);
                        pressure_field.setText("Pressure: " + weather_pressure);
                        weatherIcon.setText(Html.fromHtml(weather_iconText));

                    }
                });
                getLocationPermission();
                getLocationOffline();
                asyncTask.execute(Double.toString(latitude), Double.toString(longitude)); //  asyncTask.execute("Latitude", "Longitude")
            }
            else
            {
                Log.d(TAG,"else executed");
                // Prompt the user for permission.
                getLocationPermission();
                getLocationOffline();
                sms=smsCreator(latitude,longitude);
                sendSMS("7507205926", sms);
                Log.d(TAG,"control back in else");
                Toast.makeText(this.getContext(), "You are not connected to Internet", Toast.LENGTH_SHORT).show();

            }
        }
        return v;
    }
    private String smsCreator(Double latitude,Double longitude)
    {
        String sms, intermediateSMS;

        intermediateSMS="#ubiweather#"+latitude+"#"+longitude;
        try {
            sms = encrpt(intermediateSMS, oo);
            return sms;
        }catch(Exception e)
        {
            e.printStackTrace();
        }
        return "";
      //  return sms;
    }
    @SuppressWarnings("deprecation")
    private void sendSMS(String phoneNumber, String message)
    {
        Log.v("phoneNumber",phoneNumber);
        Log.v("message",message);
        // Log.v("i",Integer.toString(i));
        Log.d(TAG,"sendSMS executed");
        PendingIntent pi = PendingIntent.getActivity(this.getContext(), 0,
                new Intent(this.getContext(),Dummy.class), 0);

        SmsManager sms = SmsManager.getDefault();

        sms.sendTextMessage(phoneNumber, null, message, pi, null);


    }
    protected boolean isOnline() {

        ConnectivityManager cm = (ConnectivityManager) this.getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            return true;
        } else {
            return false;
        }
    }
    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }
    private  boolean checkAndRequestPermissions() {
        int permissionSendMessage = ContextCompat.checkSelfPermission(this.getContext(),
                android.Manifest.permission.SEND_SMS);

        int receiveSMS = ContextCompat.checkSelfPermission(this.getContext(),
                android.Manifest.permission.RECEIVE_SMS);

        int readSMS = ContextCompat.checkSelfPermission(this.getContext(),
                android.Manifest.permission.READ_SMS);
        int getLocation=ContextCompat.checkSelfPermission(this.getContext(), android.Manifest.permission.ACCESS_FINE_LOCATION);
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
            ActivityCompat.requestPermissions(this.getActivity(),
                    listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]),
                    REQUEST_ID_MULTIPLE_PERMISSIONS);
            return false;
        }
        return true;
    }
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        getActivity().setTitle("Weather Forecast");
    }
    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
    private void getLocationOffline()
    {
        Log.d(TAG,"getLocationOffline exectued!");
        LocationManager lm;
        Log.d(TAG,"#1");
        lm = (LocationManager) this.getContext().getSystemService(Context.LOCATION_SERVICE);
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
        if (ContextCompat.checkSelfPermission(this.getActivity(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(this.getActivity(),
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }
    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equalsIgnoreCase("otp")) {
                final String message = intent.getStringExtra("message");
                final String sender = intent.getStringExtra("sender");
                String s = message;
                String[] words = s.split("#");
                for (int i = 0; i < words.length; i++) {
                    // You may want to check for a non-word character before blindly
                    // performing a replacement
                    // It may also be necessary to adjust the character class
                    // words[i] = words[i].replaceAll("[^\\w]", "");
                    Log.d(TAG,"words["+i+"]="+words[i]);
                }
                if(words[1].equalsIgnoreCase("ubiweather"))
                {

                    cityField.setText(words[2]);
                 updatedField.setText("See time in your phone");
                    detailsField.setText(words[3]);
                    currentTemperatureField.setText(words[4]);
                    humidity_field.setText("Humidity: " + words[5]);
                    pressure_field.setText("Pressure: Pressure is not available offline");
                    weatherIcon.setText("weather icon unavailable offline");
                }

            }
        }
    };
    @Override
    public void onResume() {
        LocalBroadcastManager.getInstance(this.getContext()).
                registerReceiver(receiver, new IntentFilter("otp"));
        super.onResume();
    }
    private String encrpt(String in,String p) throws Exception {
        SecretKeySpec key= generateKey(p);
        Cipher c=Cipher.getInstance(AES);
        c.init(Cipher.ENCRYPT_MODE,key);
        byte[] encv=c.doFinal(in.getBytes());
        String eval= Base64.encodeToString(encv,Base64.DEFAULT);
        return eval;

    }
    private SecretKeySpec generateKey(String pas) throws Exception {
        final MessageDigest digest=MessageDigest.getInstance("SHA-256");
        byte[] bytes =pas.getBytes("UTF-8");
        digest.update(bytes,0,bytes.length);
        byte[] key=digest.digest();
        SecretKeySpec secretKeySpec=new SecretKeySpec(key,"AES");
        return secretKeySpec;
    }
}
