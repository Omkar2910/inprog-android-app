package com.inprog.fragments.dashboard;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.inprog.R;

import org.eazegraph.lib.charts.PieChart;
import org.eazegraph.lib.models.PieModel;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.ocpsoft.prettytime.PrettyTime;

import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


public class DashboardFragment extends Fragment {

    private TextView tv_confirmed, tv_confirmed_new, tv_active, tv_active_new, tv_recovered, tv_recovered_new, tv_death,
            tv_death_new, tv_tests, tv_tests_new, tv_date, tv_time;

    private String str_confirmed, str_confirmed_new, str_active, str_recovered, str_recovered_new,
            str_death, str_death_new, str_tests, str_tests_new, str_last_update_time;

    private int int_active_new;

    private Toolbar mToolbar;

    private SwipeRefreshLayout swipeRefreshLayout;

    private PieChart pieChart;

    private LinearLayout lin_state_data, lin_world_data;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_dashboard, container, false);

       //Toolbar
        mToolbar=view.findViewById(R.id.Toolbar_dashboard);
        mToolbar.setTitle("Inprog");


        //initialise
        tv_confirmed = view.findViewById(R.id.tv_confirmed_fdb);
        tv_confirmed_new = view.findViewById(R.id.tv_confirmed_new_fdb);
        tv_active = view.findViewById(R.id.tv_active_fdb);
        tv_active_new = view.findViewById(R.id.tv_active_new_fdb);
        tv_recovered = view.findViewById(R.id.tv_recovered_fdb);
        tv_recovered_new = view.findViewById(R.id.tv_recovered_new_fdb);
        tv_death = view.findViewById(R.id.tv_death_fdb);
        tv_death_new = view.findViewById(R.id.tv_death_new_fdb);
        tv_tests = view.findViewById(R.id.tv_samples_fdb);
        tv_tests_new = view.findViewById(R.id.tv_samples_new_fdb);
        tv_date = view.findViewById(R.id.tv_date_fdb);
        tv_time = view.findViewById(R.id.tv_time_fdb);

        pieChart = view.findViewById(R.id.piechart_fdb);
        swipeRefreshLayout = view.findViewById(R.id.swipeRefresh_dashboard);

        lin_state_data = view.findViewById(R.id.layout_statewise_fdb);
        lin_world_data = view.findViewById(R.id.layout__world_data_fdb);


        lin_state_data.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(getActivity(), StateWiseDataActivity.class));
            }
        });

        lin_world_data.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(getActivity(), WorldDataActivity.class));
            }
        });

        FetchDataCovidApi();

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                FetchDataCovidApi();
            }
        });

        return view;
    }


    private void FetchDataCovidApi() {
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        String apiUrl = "https://api.covid19india.org/data.json";

        pieChart.clearChart();         //This is for reloading piechart after every refresh

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET,
                apiUrl, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        //As the data of the json are in a nested array, so we need to define the array from which we want to fetch the data.
                        JSONArray all_state_jsonArray = null;
                        JSONArray testData_jsonArray = null;

                        try {
                            swipeRefreshLayout.setRefreshing(true);
                            all_state_jsonArray = response.getJSONArray("statewise");
                            testData_jsonArray = response.getJSONArray("tested");
                            JSONObject data_india = all_state_jsonArray.getJSONObject(0);
                            JSONObject test_data_india = testData_jsonArray.getJSONObject(testData_jsonArray.length()-1);

                            //Fetching data for India and storing it in String
                            str_confirmed = data_india.getString("confirmed");   //Confirmed cases in India
                            str_confirmed_new = data_india.getString("deltaconfirmed");   //New Confirmed cases from last update time

                            str_active = data_india.getString("active");    //Active cases in India

                            str_recovered = data_india.getString("recovered");  //Total recovered cased in India
                            str_recovered_new = data_india.getString("deltarecovered"); //New recovered cases from last update time

                            str_death = data_india.getString("deaths");     //Total deaths in India
                            str_death_new = data_india.getString("deltadeaths");    //New death cases from last update time

                            str_last_update_time = data_india.getString("lastupdatedtime"); //Last update date and time

                            str_tests = test_data_india.getString("totalsamplestested"); //Total samples tested in India
                            str_tests_new = test_data_india.getString("samplereportedtoday");   //New samples tested today

                            Handler delayToshowProgess = new Handler();

                            delayToshowProgess.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    //Setting text in the textview

                                    tv_confirmed.setText(NumberFormat.getInstance().format(Long.parseLong(str_confirmed)));
                                    tv_confirmed_new.setText("+" + NumberFormat.getInstance().format(Long.parseLong(str_confirmed_new)));

                                    tv_active.setText(NumberFormat.getInstance().format(Long.parseLong(str_active)));

                                    int_active_new = Integer.parseInt(str_confirmed_new)
                                            - (Integer.parseInt(str_recovered_new) + Integer.parseInt(str_death_new));
                                    tv_active_new.setText("+"+NumberFormat.getInstance().format(int_active_new));

                                    tv_recovered.setText(NumberFormat.getInstance().format(Long.parseLong(str_recovered)));
                                    tv_recovered_new.setText("+"+NumberFormat.getInstance().format(Long.parseLong(str_recovered_new)));

                                    tv_death.setText(NumberFormat.getInstance().format(Long.parseLong(str_death)));

                                    tv_tests.setText(NumberFormat.getInstance().format(Long.parseLong(str_tests)));
                                    tv_tests_new.setText("+"+NumberFormat.getInstance().format(Long.parseLong(str_tests_new)));

                                    tv_date.setText(FormatDate(str_last_update_time,1));
                                    tv_time.setText(FormatDate(str_last_update_time,2));

                                    pieChart.addPieSlice(new PieModel("Active", Long.parseLong(str_active), Color.parseColor("#03A9F4")));
                                    pieChart.addPieSlice(new PieModel("Recovered", Long.parseLong(str_recovered), Color.parseColor("#8BC34A")));
                                    pieChart.addPieSlice(new PieModel("Deceased", Long.parseLong(str_death), Color.parseColor("#F44336")));

                                    pieChart.startAnimation();

                                    swipeRefreshLayout.setRefreshing(false);
                                   // DismissDialog();

                                }
                            }, 1000);


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                    }
                });

        requestQueue.add(jsonObjectRequest);
    }

    @SuppressLint("SimpleDateFormat")
    public String FormatDate(String date, int testCase) {
        Date mDate;
        String dateFormat;
        try {
            mDate = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.US).parse(date);
            if (testCase == 0) {
                dateFormat = new SimpleDateFormat("dd MMM yyyy, hh:mm a").format(mDate);
                return dateFormat;
            } else if (testCase == 1) {
                dateFormat = new SimpleDateFormat("dd MMM yyyy").format(mDate);
                return dateFormat;
            } else if (testCase == 2) {
                dateFormat = new SimpleDateFormat("hh:mm a").format(mDate);
                return dateFormat;
            } else {
                Log.d("error", "Wrong input! Choose from 0 to 2");
                return "Error";
            }
        } catch (ParseException e) {
            e.printStackTrace();
            return date;
        }
    }
}