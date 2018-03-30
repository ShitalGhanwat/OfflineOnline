package tecnodart.com.offlineonline;

import android.*;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telecom.Call;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class MarketPriceDetails extends AppCompatActivity {

    
    //Location Identifiers
    double latitude,longitude;

    private boolean mLocationPermissionGranted;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private static final String KEY_LOCATION = "location";
    // The geographical location where the device is currently located. That is, the last-known
    // location retrieved by the Fused Location Provider.
    private Location mLastKnownLocation;
    // The entry point to the Fused Location Provider.
    private FusedLocationProviderClient mFusedLocationProviderClient;
    public static final int REQUEST_ID_MULTIPLE_PERMISSIONS = 1;
    
    //
    
    private  static  final String TAG = "market";

    TextView stateTextView, address;
    int i=0, f1=0, f2=0;
    static int f3 = 0;
    ProgressDialog dialog;
    customadapter ca;
    PriceDetail dt;
    ListView list;
    int flag=0;
    ArrayList<String> commm , pricc, remained, arrived ;
   // String[] cityname = { "select" , "Bihar", "Haryana","Karnataka", "Kerala", "Maharashtra" , "Manipur", };
    String pincode, add, state;
    static String msg;
    //Spinner cityn;
    private FirebaseUser mFirebaseUser;
    private DatabaseReference mDatabase;
  //  Button nearShop;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference("users");
    String oo="ubi", AES="AES";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_market_price_details);
        Intent i = getIntent();
        if(checkAndRequestPermissions()){

        }else{
            getLocationPermission();
        }
        FirebaseAuth mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
        commm = new ArrayList<>();
        pricc = new ArrayList<>();
        remained = new ArrayList<>();
        arrived = new ArrayList<>();
        stateTextView = findViewById(R.id.state);
/*
        nearShop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MarketPriceDetails.this , NearFPSAddress.class));
            }
        });
*/

   //     cit = cityname[0];
        list = findViewById(R.id.listdone);
        address = findViewById(R.id.address);

        dt = new PriceDetail();
/*        cityn = findViewById(R.id.cityn);
        ArrayAdapter aa = new ArrayAdapter(this,android.R.layout.simple_spinner_item,cityname);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //Setting the ArrayAdapter data on the Spinner
        cityn.setAdapter(aa);
        cityn.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                //Toast.makeText(EducationDetailUpload.this, boardname[i], Toast.LENGTH_LONG).show();
                cit = cityname[i];
                if(cit.equals("select")){
                    return;
                }*/

        getLocationOffline();


                mDatabase= FirebaseDatabase.getInstance().getReference().child("fareprice");
                commm.clear();
                pricc.clear();
                remained.clear();
                arrived.clear();
                ca = new customadapter(MarketPriceDetails.this, commm,pricc, remained , arrived);

               dialog = ProgressDialog.show(MarketPriceDetails.this, "",
                        "Loading. Please wait...", true);
                if (isOnline()) {

//Geocoder for state and city

                    Geocoder geocoder = new Geocoder(this, Locale.ENGLISH);

                    try {
                        List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);

                        if (addresses.size() > 0) {



                            //     city.setText(addresses.get(0).getLocality());
                            state = addresses.get(0).getAdminArea();
                            pincode =addresses.get(0).getPostalCode();
                            stateTextView.setText(state);
                            //    addr.setText(addresses.get(0).getAddressLine(0));

                        } else {
                            Toast.makeText(MarketPriceDetails.this , "Searching for address" , Toast.LENGTH_LONG).show();
                        }

                    } catch (IOException e) {
                        e.printStackTrace();
                        Toast.makeText(MarketPriceDetails.this , "Could not found" , Toast.LENGTH_LONG).show();
                    }
                    Toast.makeText(MarketPriceDetails.this, "You are connected to Internet", Toast.LENGTH_SHORT).show();
                if(state==null)
                {
                    state="Maharashtra";
                }
                mDatabase.child(state).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot ) {
                        commm.clear();
                        pricc.clear();
                        remained.clear();
                        arrived.clear();
                        ca.add(pricc , commm, remained, arrived);
                            for(DataSnapshot gr:dataSnapshot.getChildren()) {
                                // String useridstr = usrid.getKey();

                                dt = gr.getValue(PriceDetail.class);
                                if (dt != null) {
                                    commm.add(dt.getCommodity());
                                    pricc.add(dt.getPrice());
                                    remained.add(dt.getRemained());
                                    arrived.add(dt.getArrived());
                                }
                            }
                        dialog.dismiss();
                        ca = new customadapter(MarketPriceDetails.this, commm,pricc, remained , arrived);
                        list.setAdapter(ca);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }

                });

                    mDatabase= FirebaseDatabase.getInstance().getReference().child("fpsaddress").child(pincode).child("address");
                    Toast.makeText(this, pincode, Toast.LENGTH_SHORT).show();
                    mDatabase.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot ) {
                            address.setText(dataSnapshot.getValue(String.class));
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }

                    });
                } else {
                    msg=messageCreator(latitude,longitude);
                   // String msg = "#ubimarket#" + latitude +"#"+longitude;


                    sendSMS("5556", msg);
                    Toast.makeText(MarketPriceDetails.this, "You are not connected to Internet", Toast.LENGTH_SHORT).show();

                }
/*            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });*/



    }

    public String messageCreator(Double latitude,Double longitude)
    {
        String intermediateMessage, finalSMS;
        intermediateMessage ="#ubimarket#" + latitude +"#"+longitude;
        try {
            finalSMS = encrpt(intermediateMessage, oo);
            return finalSMS;
        }catch(Exception e)
        {
            e.printStackTrace();
        }
        return "";
    }
    //Broadcast receiver

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equalsIgnoreCase("otp")) {
                final String message = intent.getStringExtra("message");
                final String sender = intent.getStringExtra("sender");
                dialog.dismiss();
                commm.clear();
                pricc.clear();
                remained.clear();
                arrived.clear();
                ca.add(pricc , commm, remained, arrived);
                if(!message.contains("ubi"))
                {
                    return;
                }
                String[] arr = message.split("#" );
                if(arr[1].equals("ubierror")){
                    Toast.makeText(MarketPriceDetails.this, "Service not available", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(!Objects.equals(arr[1], "ubimarket")){return;}
                Toast.makeText(MarketPriceDetails.this , arr[2], Toast.LENGTH_SHORT).show();

                for(int j=2,k=3,l=4,m=5 ;j< 15; j=j+4,k=k+4,l=l+4,m=m+4 ){

                    commm.add(arr[j]);
                    pricc.add(arr[k]);
                    remained.add(arr[l]);
                    arrived.add(arr[m]);


                }
                stateTextView.setText(arr[18]);
                address.setText(arr[19]);
                ca = new customadapter(MarketPriceDetails.this, commm,pricc, remained , arrived);
                list.setAdapter(ca);


            }   // sendSMS(sender,sms_send.toString());
        }
    };
    @Override
    public void onResume() {
        LocalBroadcastManager.getInstance(this).
                registerReceiver(receiver, new IntentFilter("otp"));
        super.onResume();
    }

    protected boolean isOnline() {

        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            return true;
        } else {
            return false;
        }
    }

    @SuppressWarnings("deprecation")
    private void sendSMS(String phoneNumber, String message) {
        Toast.makeText(MarketPriceDetails.this, "in sendSMS", Toast.LENGTH_SHORT).show();


        SmsManager sms = SmsManager.getDefault();

        sms.sendTextMessage(phoneNumber, null, message, null, null);

    }

    class customadapter extends BaseAdapter {

        private Context context;
        private LayoutInflater inflater;
        private ArrayList<String> commodity;
        private ArrayList<String> quantity;
        private ArrayList<String> arrived;
        private ArrayList<String> price;



        public void add(ArrayList<String> prices, ArrayList<String> commoditys, ArrayList<String> quantitys, ArrayList<String> arriveds) {
            this.commodity = commoditys;
            this.price= prices;
            this.quantity=quantitys;
            this.arrived = arriveds;
            notifyDataSetChanged();
        }


        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return commodity.size();
        }

        @Override
        public Object getItem(int i) {
            // TODO Auto-generated method stub

            return commodity.get(i);
        }

        public customadapter(Context context, ArrayList<String> commodity,ArrayList<String> quantity,ArrayList<String> price, ArrayList<String> arrived) {
            this.context = context;
            this.commodity = commodity;
            this.price= price;
            this.quantity=quantity;
            this.arrived=arrived;
            //line 31
            inflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }


        @Override
        public long getItemId(int i) {
            // TODO Auto-generated method stub
            return i;
        }
        public Object getCommodity(int position) {
            return commodity.get(position);
        }
        public Object getPrice(int position) {
            return price.get(position);
        }
        public Object getQuantity(int position) {
            return quantity.get(position);
        }
        public Object getArrived(int position) {
            return arrived.get(position);
        }

        @SuppressLint("ViewHolder")
        @Override
        public View getView(final int position, View convertview, ViewGroup arg2) {
            // TODO Auto-generated method stub
            LayoutInflater inflater = getLayoutInflater();
            convertview = inflater.inflate(R.layout.customer_list_adapter, null);
     /*       TextView namm = (TextView) convertview.findViewById(R.id.customer_name);
            TextView numm = (TextView) convertview.findViewById(R.id.customer_number);
            final ImageView customerprofile = convertview.findViewById(R.id.customer_profile_image);
            String plant = (String) getItem(position);
            String quantity = (String) getQuantity(position);
            namm.setText(plant);
            numm.setText(quantity);
            TextView sta = (TextView) convertview.findViewById(R.id.customer_status);
            sta.setText((String)getstat(position));
            TextView types = (TextView) convertview.findViewById(R.id.customer_insurance_type);
            types.setText((String)getType(position));
*/
            TextView commo = convertview.findViewById(R.id.commodity);

            TextView quan = convertview.findViewById(R.id.remained);
            TextView pri = convertview.findViewById(R.id.price);
            TextView arr = convertview.findViewById(R.id.arrived);

            arr.setText((String)getArrived(position));
            commo.setText((String)getCommodity(position));
            pri.setText((String)getPrice(position));
            quan.setText((String)getQuantity(position));



            return convertview;
        }


    }

    private void getLocationOffline()
    {
        Log.d(TAG,"getLocationOffline exectued!");
        LocationManager lm;
        Log.d(TAG,"#1");
        lm = (LocationManager) this .getSystemService(Context.LOCATION_SERVICE);
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
            latitude=19.025207;
            // latitude=18.5614668;
            Log.d(TAG,"#6");
            longitude=72.8503206;
            // longitude=73.9324918;
            Log.d(TAG,"#7");
        }catch(SecurityException s)
        {
            Log.d(TAG,"Permission Denied");
        }
    }
    /**
     * Gets the current location of the device, and positions the map's camera.
     */
/*    private void getDeviceLocation() {
        *//*
         * Get the best and most recent location of the device, which may be null in rare
         * cases when a location is not available.
         *//*
        Log.d(TAG,"getDeviceLocation() executed");

        try {
            if (mLocationPermissionGranted) {
                Task<Location> locationResult = mFusedLocationProviderClient.getLastLocation();
                locationResult.addOnCompleteListener(this  , new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        if (task.isSuccessful()) {
                            // Set the map's camera position to the current location of the device.
                            mLastKnownLocation = task.getResult();
                            latitude=mLastKnownLocation.getLatitude();
                            longitude=mLastKnownLocation.getLongitude();

                        } else {
                            Log.d(TAG, "Current location is null. Using defaults.");


                        }
                    }
                });
            }
        } catch (SecurityException e)  {
            Log.e("Exception: %s", e.getMessage());
        }
    }*/
    private void getLocationPermission() {
        /*
         * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */
        Log.d(TAG,"getLocationPermission executed");
        if (ContextCompat.checkSelfPermission(this  ,
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(this  ,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }

    private  boolean checkAndRequestPermissions() {
        int permissionSendMessage = ContextCompat.checkSelfPermission(this ,
                android.Manifest.permission.SEND_SMS);

        int receiveSMS = ContextCompat.checkSelfPermission(this ,
                android.Manifest.permission.RECEIVE_SMS);

        int readSMS = ContextCompat.checkSelfPermission(this ,
                android.Manifest.permission.READ_SMS);
        int getLocation=ContextCompat.checkSelfPermission(this , android.Manifest.permission.ACCESS_FINE_LOCATION);
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
            ActivityCompat.requestPermissions(this  ,
                    listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]),
                    REQUEST_ID_MULTIPLE_PERMISSIONS);
            return false;
        }
        return true;
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
    private String decrpt(String out, String s) throws Exception {
        Log.d(TAG,"decrypt called ");
        SecretKeySpec key=generateKey(s);
        Cipher c=Cipher.getInstance(AES);
        c.init(Cipher.DECRYPT_MODE,key);
        byte[] dval= Base64.decode(out,Base64.DEFAULT);
        byte[] decval=c.doFinal(dval);
        String  dvalue=new String(decval);
        Log.d(TAG,"dvalue = "+dvalue);
        return dvalue;

    }

}
