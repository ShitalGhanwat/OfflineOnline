package tecnodart.com.offlineonline;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;

import java.util.ArrayList;

import me.relex.circleindicator.CircleIndicator;


public class Home extends Fragment {


    private static final Integer[] XMEN = {R.drawable.food,R.drawable.food2};
    private ArrayList<Integer> XMENArray = new ArrayList<Integer>();
    private static int currentPage = 0;
    private static ViewPager mPager;
    Fragment f = null;
    private ImageView share, msp, fairprice, weather, schemes , faqs;
    View v;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.home , container , false) ;
        final TableLayout grd_overview = v.findViewById(R.id.tbl_overview);
        grd_overview.setShrinkAllColumns(true);


        faqs = v.findViewById(R.id.faqtiles);
        share = v.findViewById(R.id.sharetiles);
        schemes = v.findViewById(R.id.schemeforme);
        msp = v.findViewById(R.id.msp);
        weather = v.findViewById(R.id.weatherforecast);
        fairprice = v.findViewById(R.id.fairprice);

        return v;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init();


        faqs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                f= new FaQs();
                FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.content_after_login_home, f);
                ft.commit();
            }
        });
        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
                    Intent sh = new Intent(Intent.ACTION_SEND);
                    sh.setType("text/plain");
                    sh.putExtra(Intent.EXTRA_SUBJECT, "Ubi Quotes");
                    String sAux = "\nWe invite you to join Ubi Quotes\nDownload and Install Ubi Quotes\n";
                    sAux = sAux + "  \nClick below link to download Ubi Quotes App \n " +
                            "https://drive.google.com/open?id=1aTHwV78iFO_xRn_GTAccpdAkucS5FzCO";
                    sh.putExtra(Intent.EXTRA_TEXT, sAux);
                    startActivity(Intent.createChooser(sh, "choose one"));
                } catch(Exception e) {
                    //e.toString();
                }
            }
        });
        schemes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                f= new Schemes();
                FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.content_after_login_home, f);
                ft.commit();
            }
        });
        msp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                f= new MinimumSupportPrice();
                FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.content_after_login_home, f);
                ft.commit();
            }
        });
        weather.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                f= new WeatherForecastFragment();
                FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.content_after_login_home, f);
                ft.commit();
            }
        });
        fairprice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), MarketPriceDetails.class));
            }
        });


        getActivity().setTitle("Home");
    }
    private void init() {
        for(int i=0;i<XMEN.length;i++)
            XMENArray.add(XMEN[i]);

        mPager = (ViewPager) v.findViewById(R.id.pager);
        mPager.setAdapter(new MyAdapter(getContext(),XMENArray));
        CircleIndicator indicator = (CircleIndicator) v.findViewById(R.id.indicator);
        indicator.setViewPager(mPager);

        // Auto start of viewpager



        mPager.setCurrentItem(currentPage, true);

    }

}