package tecnodart.com.offlineonline;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telecom.Call;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MarketPriceDetails extends AppCompatActivity {


    int i=0, f1=0, f2=0;
    static int f3 = 0;
    ProgressDialog dialog;
    customadapter ca;
    PriceDetail dt;
    ListView list;
    int flag=0;
    ArrayList<String> commm , pricc, quann ;
    String[] cityname = { "nagpur", "pune", };
    String cit, add;
    static String msg;
    Spinner cityn;
    private FirebaseUser mFirebaseUser;
    private DatabaseReference mDatabase;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference("users");
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_market_price_details);
        Intent i = getIntent();

        FirebaseAuth mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();

        commm = new ArrayList<>();
        pricc = new ArrayList<>();
        quann = new ArrayList<>();
        cit = cityname[0];
        list = findViewById(R.id.listdone);

        dt = new PriceDetail();
        cityn = findViewById(R.id.cityn);
        ArrayAdapter aa = new ArrayAdapter(this,android.R.layout.simple_spinner_item,cityname);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //Setting the ArrayAdapter data on the Spinner
        cityn.setAdapter(aa);
        cityn.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                //Toast.makeText(EducationDetailUpload.this, boardname[i], Toast.LENGTH_LONG).show();
                cit = cityname[i];
                mDatabase= FirebaseDatabase.getInstance().getReference().child("fareprice").child(cit);

                ca = new customadapter(MarketPriceDetails.this, commm,pricc, quann );

               dialog = ProgressDialog.show(MarketPriceDetails.this, "",
                        "Loading. Please wait...", true);
                if (isOnline()) {


                    Toast.makeText(MarketPriceDetails.this, "You are connected to Internet", Toast.LENGTH_SHORT).show();

                mDatabase.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot ) {
                        commm.clear();
                        pricc.clear();
                        quann.clear();
                        ca.add(pricc , commm, quann);
                            for(DataSnapshot gr:dataSnapshot.getChildren()) {
                                // String useridstr = usrid.getKey();

                                dt = gr.getValue(PriceDetail.class);
                                if (dt != null) {
                                    commm.add(dt.getCommodity());
                                    pricc.add(dt.getPrice());
                                    quann.add(dt.getRemained());
                                }
                            }
                        dialog.dismiss();
                        ca = new customadapter(MarketPriceDetails.this, commm,pricc, quann );
                        list.setAdapter(ca);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }

                });
                } else {
                    sendSMS("9013085353", cit);
                    Toast.makeText(MarketPriceDetails.this, "You are not connected to Internet", Toast.LENGTH_SHORT).show();
                    if(f3!=0) {
                        Toast.makeText(MarketPriceDetails.this, msg, Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });



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
        private ArrayList<String> price;



        public void add(ArrayList<String> prices, ArrayList<String> commoditys, ArrayList<String> quantitys) {
            this.commodity = commoditys;
            this.price= prices;
            this.quantity=quantitys;

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

        public customadapter(Context context, ArrayList<String> commodity,ArrayList<String> quantity,ArrayList<String> price) {
            this.context = context;
            this.commodity = commodity;
            this.price= price;
            this.quantity=quantity;
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

            TextView quan = convertview.findViewById(R.id.quantity);
            TextView pri = convertview.findViewById(R.id.price);

            commo.setText((String)getCommodity(position));
            pri.setText((String)getPrice(position));
            quan.setText((String)getQuantity(position));



            return convertview;
        }


    }
    public class SmsReceiver extends BroadcastReceiver {
        private static final String Tag="Message Receivied";

        public void onReceive(Context context, Intent intent) {
            final Bundle pdubundle = intent.getExtras();

            Object[]  pdus=(Object[])pdubundle.get("pdus");

            SmsMessage message=SmsMessage.createFromPdu((byte[])pdus[0]);
            //  Toast.makeText(context,"sms rec from"+message.getOriginatingAddress()+message.getMessageBody(),Toast.LENGTH_LONG).show();

            // tv.setText(add + num);

            msg =message.getMessageBody();
            func(msg);
            f3=1;
            // Toast.makeText(context,"sms rec from"+message.getOriginatingAddress()+message.getMessageBody(),Toast.LENGTH_LONG).show();

        }
    }
    public void func(String mm){

    }
}
