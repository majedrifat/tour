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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

public class TravelExpenseRecord extends AppCompatActivity {

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
        setContentView(R.layout.activity_travel_expense_record);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar); // get the reference of Toolbar
        setSupportActionBar(toolbar); // Setting/replace toolbar as the ActionBar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.back));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TravelExpenseRecord.this, HomePage.class);
                startActivity(intent);
                finish();
            }
        });
        auth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Expenses");
        mDatabaseUser = FirebaseDatabase.getInstance().getReference().child("Users");

        String CurrentUserId = auth.getCurrentUser().getUid();
        mDatabaseCurrentUser = FirebaseDatabase.getInstance().getReference().child("Expenses");
        queryCurrentUser = mDatabaseCurrentUser.orderByChild("userId").equalTo(CurrentUserId);


        mDatabaseUser.keepSynced(true);
        mDatabase.keepSynced(true);

        authListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user == null) {
                    startActivity(new Intent(TravelExpenseRecord.this, LoginActivity.class));
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
        FirebaseRecyclerAdapter<BlogReference, TravelExpenseRecord.BlogviewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<BlogReference, TravelExpenseRecord.BlogviewHolder>(
                options
        ) {



            @Override
            protected void onBindViewHolder(@NonNull BlogviewHolder holder, int position, @NonNull BlogReference model) {
                final String post_key = getRef(position).getKey();

                holder.setItemDetails(model.getItemDetails());
                holder.setExpense(model.getExpense() + " tk");
                holder.setmDate(model.getmDate());
                //viewHolder.setBalance(model.getBalance());

                holder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent showIndent = new Intent(TravelExpenseRecord.this, TravelMoment.class);
                        showIndent.putExtra("blog_id", post_key);
                        startActivity(showIndent);
                    }
                });
            }
            @NonNull
            @Override
            public TravelExpenseRecord.BlogviewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.expense_row, parent, false);
                return new TravelExpenseRecord.BlogviewHolder(view);
            }


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
        public void setItemDetails(String itemDetails){
            TextView item_Name = (TextView) mView.findViewById(R.id.itemName);
            item_Name.setText(itemDetails);
        }
        public void setExpense(String expense){
            TextView item_expense = (TextView) mView.findViewById(R.id.expenses);
            item_expense.setText(expense);
        }
        public void setmDate(String mDate){
            TextView post_date = (TextView) mView.findViewById(R.id.Post_date);
            post_date.setText(mDate);
        }
        public void setBalance(String balance){
            TextView new_balance = (TextView) mView.findViewById(R.id.balance);
            new_balance.setText(balance);
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
     /*   if (id == R.id.weather) {

            return true;
        } else if (id == R.id.nearLocation) {
            return true;*/
        if (id == R.id.add) {
            Intent i = new Intent(TravelExpenseRecord.this, TravelExpenseTracker.class);
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
