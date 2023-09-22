package com.inprog.main;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.inprog.fragments.community.CommunityFragment;
import com.inprog.fragments.dashboard.DashboardFragment;
import com.inprog.fragments.news.NewsFragment;
import com.inprog.R;

public class MainActivity extends AppCompatActivity {
    ProgressBar progressBar;
//    private Toolbar mToolbar;

    private boolean doubleBackToExitPressedOnce = false;
    private Toast backPressToast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Bottom navigation
        BottomNavigationView BottomNav = findViewById(R.id.bottom_nav); // ref
        BottomNav.setOnNavigationItemSelectedListener(navListener);// initialisation
        getSupportFragmentManager().beginTransaction().replace(R.id.Fragment_container, new NewsFragment()).commit();
        //Progressbar
        progressBar = findViewById(R.id.progressBarMain);

    }


    private BottomNavigationView.OnNavigationItemSelectedListener navListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Fragment selectedFragment = null;

            switch (item.getItemId()) {
                case R.id.nav_news:
                    selectedFragment = new NewsFragment();
                    //getSupportActionBar().setTitle("News");
                    break;
                case R.id.nav_dashboard:
                    selectedFragment = new DashboardFragment();
                    //getSupportActionBar().setTitle("Dashboard");
                    break;
                case R.id.nav_community:
                    selectedFragment = new CommunityFragment();
                    //getSupportActionBar().setTitle("Community");
                    break;
            }

            getSupportFragmentManager().beginTransaction().replace(R.id.Fragment_container, selectedFragment).commit();

            return true;
        }
    };


    // double press back button to exit from the app
    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            backPressToast.cancel();
            super.onBackPressed();
            return;
        }
        doubleBackToExitPressedOnce = true;
        backPressToast = Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT);
        backPressToast.show();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, 2000);
    }

}



   /* public void LogOut(View view) {
        progressBar.setVisibility(View.VISIBLE);
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(getApplicationContext(), Login.class));
        finish();
        progressBar.setVisibility(View.GONE);
    }*/
