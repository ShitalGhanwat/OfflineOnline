package tecnodart.com.offlineonline;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.security.MessageDigest;
import java.util.Objects;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link NotificationsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link NotificationsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class NotificationsFragment extends Fragment {
    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    RecyclerView.Adapter adapter;
    Notifications notification;
    String title, body, sms, AES="AES",oo="ubi", decryptedMessage;
    final String TAG="Debug";
    CommonClass commonClass;
    DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference();
    DatabaseReference mNotificationRef = mRootRef.child("notifications");
    TextView tv;
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    long notificationCount;

    private OnFragmentInteractionListener mListener;

    public NotificationsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment NotificationsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static NotificationsFragment newInstance(String param1, String param2) {
        NotificationsFragment fragment = new NotificationsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        commonClass=new CommonClass(this.getContext(),this.getActivity());
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v= inflater.inflate(R.layout.fragment_notifications, container, false);
        tv=v.findViewById(R.id.demoView);
        tv.setText("");
        if(commonClass.isOnline()) {
            mNotificationRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        //  Log.d("xyzr22",commodity);
                        Log.d(TAG, "this executes!");
                        try {
                            title = snapshot.getKey();
                        } catch (Exception e) {
                            Log.d(TAG, e.toString());
                        }
                        Log.d(TAG, "no problem here");
                        body = snapshot.getValue(String.class);
                        Log.d(TAG, "body gets value " + body + "title gets value " + title);
                        tv.append("\n" + title + "\n" + body + "\n\n");
                        Log.d(TAG, "append works fine");

                    }

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });
        }
        else
        {
             sms=smsCreator();
             commonClass.sendSMS("5556",sms);
        }
        return v;
    }
public String smsCreator()
{
    String result, intermediateResult;
    intermediateResult="#ubinotifications";
    try {
        result = encrpt(intermediateResult, oo);
        return result;
    }catch(Exception e)
    {
        e.printStackTrace();
    }
    return "";
}

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
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
                if(words[1].equalsIgnoreCase("ubinotifications"))
                {


                        tv.append("\n" + words[2] + "\n" + words[3] + "\n\n");

                }
            }

            }   // sendSMS(sender,sms_send.toString());
        };

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
    @Override
    public void onResume() {
        LocalBroadcastManager.getInstance(this.getContext()).
                registerReceiver(receiver, new IntentFilter("otp"));
        super.onResume();
    }
}
