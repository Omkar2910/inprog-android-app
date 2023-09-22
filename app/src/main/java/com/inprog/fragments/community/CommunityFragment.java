package com.inprog.fragments.community;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.inprog.CreatePost;
import com.inprog.Login;
import com.inprog.ProfileActivity;
import com.inprog.R;
import com.inprog.fragments.community.adapters.AdapterPost;
import com.inprog.fragments.community.model.Postmodel;
import com.inprog.main.MainActivity;

import java.util.ArrayList;
import java.util.List;


public class CommunityFragment extends Fragment {

    ProgressBar progressBar;
    private SwipeRefreshLayout swipeRefreshLayout;
    private FloatingActionButton createpost;

    private Toolbar mToolbar;

    RecyclerView recyclerview;
    List<Postmodel> postList;
    AdapterPost adapterPost;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_community, container, false);

        /*swipeRefreshLayout = view.findViewById(R.id.swipeRefresh_community);
        swipeRefreshLayout.setRefreshing(false);
*/

        recyclerview =view.findViewById(R.id.postrecycle);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setStackFromEnd(true);
        layoutManager.setReverseLayout(true);
        recyclerview.setLayoutManager(layoutManager);

        postList = new ArrayList<>();

        loadposts();

        createpost = view.findViewById(R.id.createpost);

        createpost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), CreatePost.class);
                startActivity(intent);
            }
        });

        mToolbar = view.findViewById(R.id.Toolbar_community);
        mToolbar.setTitle("Inprog");
        return view;
    }

    private void loadposts() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Posts");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                postList.clear();
                for (DataSnapshot ds : snapshot.getChildren())
                {
                    Postmodel postmodel = ds.getValue(Postmodel.class);

                    postList.add(postmodel);

                    adapterPost = new AdapterPost(getActivity(), postList);
                    recyclerview.setAdapter(adapterPost);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        progressBar = view.findViewById(R.id.progressBarCommunity);
        mToolbar.inflateMenu(R.menu.community_menu);

        mToolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.option_profile_community:
                        Intent profile = new Intent(getActivity(), ProfileActivity.class);
                        startActivity(profile);
                        return true;

                    case R.id.option_followingPosts_community:
                        Toast.makeText(getActivity(), "Following post", Toast.LENGTH_SHORT).show();
                    /* Intent followingPosts = new Intent(getActivity(), FollowingPostsActivity.class);
                    startActivity(followingPosts);*/
                        return true;

                    case R.id.option_search_community:
                        Toast.makeText(getActivity(), "Search", Toast.LENGTH_SHORT).show();
                    /*Intent searchIntent = new Intent(getActivity(), SearchActivity.class);
                    startActivity(searchIntent);*/
                        return true;

                    case R.id.option_Logout:
//                        Toast.makeText(getActivity(), "", Toast.LENGTH_SHORT).show();
                        progressBar.setVisibility(View.VISIBLE);
                        FirebaseAuth.getInstance().signOut();
                        startActivity(new Intent(getActivity(), Login.class));
                        getActivity().finish();
                        progressBar.setVisibility(View.GONE);
                        return true;
                    default:
                        return false;
                }
            }
        });
    }

}