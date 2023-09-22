package com.inprog.fragments.news;

import android.app.Dialog;
import android.icu.text.CaseMap;
import android.icu.util.ULocale;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.inprog.R;
import com.inprog.fragments.news.model.Articles;
import com.inprog.fragments.news.model.Headlines;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class NewsFragment extends Fragment {
    RecyclerView recyclerView;
    SwipeRefreshLayout swipeRefreshLayout;
    EditText etQuery;
    Button btnSearch;
    Dialog dialog;
    final String API_KEY = "22c63c0238d7413e9f71e3f026558738";
    Adapter adapter;
    List<Articles> articles = new ArrayList<>();

    private Toolbar mToolbar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_news, container, false);

        mToolbar = view.findViewById(R.id.Toolbar_news);
        mToolbar.setTitle("Inprog");

        swipeRefreshLayout = view.findViewById(R.id.swipeRefresh_news);
        recyclerView = view.findViewById(R.id.recyclerView);

        etQuery = view.findViewById(R.id.etQuery);
        btnSearch = view.findViewById(R.id.btnSearch);
        dialog = new Dialog(getActivity());

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        final String country = getCountry();

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                retrieveJson("", country, API_KEY);
            }
        });


        retrieveJson("", country, API_KEY);

        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!etQuery.getText().toString().equals("")) {
                    swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                        @Override
                        public void onRefresh() {
                            retrieveJson(etQuery.getText().toString(), country, API_KEY);
                        }
                    });
                    retrieveJson(etQuery.getText().toString(), country, API_KEY);
                } else {
                    swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                        @Override
                        public void onRefresh() {
                            retrieveJson("", country, API_KEY);
                        }
                    });
                    retrieveJson("", country, API_KEY);
                }
            }
        });
        return view;
    }


    public void retrieveJson(String query, String country, String apiKey) {
        swipeRefreshLayout.setRefreshing(true);
        Call<Headlines> call;
        if (!etQuery.getText().toString().equals("")) {
            call = ApiClient.getInstance().getApi().getSpecificData(query, apiKey);
        } else {
            call = ApiClient.getInstance().getApi().getHeadlines(country, apiKey);
        }


        call.enqueue(new Callback<Headlines>() {
            @Override
            public void onResponse(Call<Headlines> call, Response<Headlines> response) {
                if (response.isSuccessful()) {
                    assert response.body() != null;
                    if (response.body().getArticles() != null) {
                        swipeRefreshLayout.setRefreshing(false);
                        articles.clear();
                        articles = response.body().getArticles();
                        adapter = new Adapter(getActivity(), articles);
                        recyclerView.setAdapter(adapter);
                    }
                }
            }

            @Override
            public void onFailure(Call<Headlines> call, Throwable t) {
                swipeRefreshLayout.setRefreshing(false);
                Toast.makeText(getActivity(), t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    public String getCountry() {
        Locale locale = Locale.getDefault(); //Locale.getDefault gets the locale based on the input language of users smartphone
        String country = locale.getCountry();
        return country.toLowerCase();
    }


}