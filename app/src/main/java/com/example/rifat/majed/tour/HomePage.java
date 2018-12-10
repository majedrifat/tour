package com.example.rifat.majed.tour;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class HomePage extends AppCompatActivity {
    private ProgressBar progressBar;
    private FirebaseAuth.AuthStateListener authListener;
    private FirebaseAuth auth;
    private RecyclerView mBlogList;
    private DatabaseReference mDatabase;
    private DatabaseReference mDatabaseUser;
    private DatabaseReference mDatabaseCurrentUser;
    private Query queryCurrentUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar); // get the reference of Toolbar
        toolbar.setTitle("Tour Mate"); // set Title for Toolbar
        setSupportActionBar(toolbar); // Setting/replace toolbar as the ActionBar

        auth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Blog");
        mDatabaseUser = FirebaseDatabase.getInstance().getReference().child("Users");

        String CurrentUserId = auth.getCurrentUser().getUid();
        mDatabaseCurrentUser = FirebaseDatabase.getInstance().getReference().child("Blog");
        queryCurrentUser = mDatabaseCurrentUser.orderByChild("userId").equalTo(CurrentUserId);


        mDatabaseUser.keepSynced(true);
        mDatabase.keepSynced(true);

        checkUserExist();
        authListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user == null) {
                    startActivity(new Intent(HomePage.this, LoginActivity.class));
                    finish();
                }
            }
        };
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        if (progressBar != null) {
            progressBar.setVisibility(View.GONE);
        }

        mBlogList = (RecyclerView) findViewById(R.id.blog_list);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        mBlogList.setHasFixedSize(true);
        mBlogList.setLayoutManager(layoutManager);
    }


    @Override
    protected void onResume() {
        super.onResume();
        progressBar.setVisibility(View.GONE);
    }
    @Override
    public void onStart() {
        super.onStart();
        FirebaseRecyclerOptions<BlogReference> options=
                new FirebaseRecyclerOptions.Builder<BlogReference>()
                        .setQuery(mDatabase,BlogReference.class)
                        .setLifecycleOwner(this)
                        .build();

        auth.addAuthStateListener(authListener);
        FirebaseRecyclerAdapter<BlogReference, HomePage.BlogviewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<BlogReference, HomePage.BlogviewHolder>(
                options) {
            @NonNull
            @Override
            public HomePage.BlogviewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.blog_row, parent, false);
                return new HomePage.BlogviewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull BlogviewHolder holder, int position, @NonNull BlogReference model) {
                final String post_key = getRef(position).getKey();

                holder.setDestination(model.getDestination());
                holder.setBudget(model.getBudget() + " tk");
                holder.setFromDate(model.getFromDate());
                holder.setToDate(model.getToDate());

                holder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent showIndent = new Intent(HomePage.this, TravelExpenseRecord.class);
                        showIndent.putExtra("blog_id", post_key);
                        startActivity(showIndent);
                    }
                });
            }

        /*    @Override
            protected void populateViewHolder(HomePage.BlogviewHolder viewHolder, final BlogReference model, int position) {

             *//*   final String post_key = getRef(position).getKey();

                viewHolder.setDestination(model.getDestination());
                viewHolder.setBudget(model.getBudget() + " tk");
                viewHolder.setFromDate(model.getFromDate());
                viewHolder.setToDate(model.getToDate());

                viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent showIndent = new Intent(HomePage.this, TravelExpenseRecord.class);
                        showIndent.putExtra("blog_id", post_key);
                        startActivity(showIndent);
                    }
                });*//*
            }*/
        };
        mBlogList.setAdapter(firebaseRecyclerAdapter);
    }
    public  static class BlogviewHolder extends RecyclerView.ViewHolder{

        FirebaseAuth auth;

        View mView;
        public BlogviewHolder(View itemView) {
            super(itemView);
            mView = itemView;
            auth = FirebaseAuth.getInstance();
        }
        public void setDestination(String destination){
            TextView post_destination = (TextView) mView.findViewById(R.id.Post_destination);
            post_destination.setText(destination);
        }
        public void setBudget(String budget){
            TextView post_rent = (TextView) mView.findViewById(R.id.est_budget);
            post_rent.setText(budget);
        }
        public void setFromDate(String fromDate){
            TextView post_details = (TextView) mView.findViewById(R.id.from_date);
            post_details.setText(fromDate);
        }
        public void setToDate(String toDate){
            TextView post_location = (TextView) mView.findViewById(R.id.to_date);
            post_location.setText(toDate);
        }
    }

    private void checkUserExist() {
        if(auth.getCurrentUser() != null){
            final String user_id = auth.getCurrentUser().getUid();
            mDatabaseUser.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if(!dataSnapshot.hasChild(user_id)){

                        Intent i = new Intent(HomePage.this, SetupActivity.class);
                        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(i);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle("Exit Application?");
        alertDialogBuilder
                .setMessage("Click yes to exit!")
                .setCancelable(false)
                .setPositiveButton("Yes",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                moveTaskToBack(true);
                                android.os.Process.killProcess(android.os.Process.myPid());
                                System.exit(1);
                                finish();
                            }
                        })

                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        dialog.cancel();
                    }
                });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }
    @Override
    public void onStop() {
        super.onStop();
        if (authListener != null) {
            auth.removeAuthStateListener(authListener);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
     /*   if (id == R.id.weather) {

            return true;
        } else if (id == R.id.nearLocation) {
            return true;*/
      if (id == R.id.add) {
            Intent i = new Intent(HomePage.this, AddEventActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(i);
            return true;
        } else if (id == R.id.logout) {
            auth.signOut();
            return true;
        }else if (id == R.id.exit) {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
            alertDialogBuilder.setTitle("Exit Application?");
            alertDialogBuilder
                    .setMessage("Click yes to exit!")
                    .setCancelable(false)
                    .setPositiveButton("Yes",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    moveTaskToBack(true);
                                    android.os.Process.killProcess(android.os.Process.myPid());
                                    System.exit(1);
                                    finish();
                                }
                            })

                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {

                            dialog.cancel();
                        }
                    });

            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}