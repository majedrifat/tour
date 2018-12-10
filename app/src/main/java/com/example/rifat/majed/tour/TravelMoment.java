package com.example.rifat.majed.tour;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

public class TravelMoment extends AppCompatActivity {
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
        setContentView(R.layout.activity_travel_moment);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar); // get the reference of Toolbar
        setSupportActionBar(toolbar); // Setting/replace toolbar as the ActionBar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.back));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TravelMoment.this, TravelExpenseRecord.class);
                startActivity(intent);
                finish();
            }
        });

        auth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Blog_moment");
        mDatabaseUser = FirebaseDatabase.getInstance().getReference().child("Users");
        String CurrentUserId = auth.getCurrentUser().getUid();
        mDatabaseCurrentUser = FirebaseDatabase.getInstance().getReference().child("Blog_moment");
        queryCurrentUser = mDatabaseCurrentUser.orderByChild("userId").equalTo(CurrentUserId);

        mDatabaseUser.keepSynced(true);
        mDatabase.keepSynced(true);

        authListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user == null) {
                    startActivity(new Intent(TravelMoment.this, LoginActivity.class));
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
        auth.addAuthStateListener(authListener);
        FirebaseRecyclerOptions<BlogReference> options=
                new FirebaseRecyclerOptions.Builder<BlogReference>()
                        .setQuery(mDatabase,BlogReference.class)
                        .setLifecycleOwner(this)
                        .build();
        FirebaseRecyclerAdapter<BlogReference, TravelMoment.BlogviewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<BlogReference, TravelMoment.BlogviewHolder>(
                options
        )
         {

             @Override
             protected void onBindViewHolder(@NonNull TravelMoment.BlogviewHolder holder, int position, @NonNull BlogReference model) {
                 String post_key = getRef(position).getKey();



                 holder.setMoment(model.getMoment());
                 holder.setMoments_date(model.getMoments_date());
                 holder.setImage(getApplicationContext(),model.getImage());

   /*              holder.mView.setOnClickListener(new View.OnClickListener() {
                     @Override
                     public void onClick(View view) {
//                        Intent showIndent = new Intent(UserProfile.this, ShowDataActivity.class);
//                        showIndent.putExtra("blog_id", post_key);
//                        startActivity(showIndent);
                     }
                 });*/
             }

             @NonNull
             @Override
             public TravelMoment.BlogviewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                 View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.moment_row, parent, false);
                 return new TravelMoment.BlogviewHolder(view);
             }

         };


        try {
            mBlogList.setAdapter(firebaseRecyclerAdapter);
        } catch (Exception e){
            Log.e("LOGGG", e.getLocalizedMessage());
        }
    }
    public  static class BlogviewHolder extends RecyclerView.ViewHolder{

        FirebaseAuth auth;

        View mView;
        public BlogviewHolder(View itemView) {
            super(itemView);
            mView = itemView;
            auth = FirebaseAuth.getInstance();
        }

        public void setMoment(String moment){
            TextView moments = (TextView) mView.findViewById(R.id.moment_details);
            moments.setText(moment);
        }
        public void setMoments_date(String moments_date){
            TextView post_rent = (TextView) mView.findViewById(R.id.moment_date);
            post_rent.setText(moments_date);
        }
        public void setImage(final Context context, final String image){
            final ImageView post_image = (ImageView) mView.findViewById(R.id.Post_image);
            Log.e("LOGGG", image);


            Picasso.with(context).load(image).networkPolicy(NetworkPolicy.OFFLINE).into(post_image, new Callback() {
                @Override
                public void onSuccess() {
                }
                @Override
                public void onError() {
                    Picasso.with(context).load(image).into(post_image);
                }
            });
        }


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
      /*  if (id == R.id.weather) {

            return true;
        } else if (id == R.id.nearLocation) {
            return true;*/
        if (id == R.id.add) {
            Intent i = new Intent(TravelMoment.this, AddTravelMoment.class);
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
