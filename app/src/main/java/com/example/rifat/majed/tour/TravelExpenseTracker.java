package com.example.rifat.majed.tour;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.util.Date;

public class TravelExpenseTracker extends AppCompatActivity implements View.OnClickListener{
    private TextView date;
    private ProgressBar progressBar;
    private FirebaseAuth.AuthStateListener authListener;
    private FirebaseAuth auth;

    //defining a database reference
    private DatabaseReference mDatabase;
    private DatabaseReference mDatabaseUser;
    private DatabaseReference mBalance;

    private FirebaseUser mCurrentUser;

    //our new views
    private EditText editTextItemName, editTextExpenses;
    private Button saveRecord;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_travel_expense_tracker);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar); // get the reference of Toolbar
        setSupportActionBar(toolbar); // Setting/replace toolbar as the ActionBar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.back));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TravelExpenseTracker.this, TravelExpenseRecord.class);
                startActivity(intent);
                finish();
            }
        });
        auth = FirebaseAuth.getInstance();
        mCurrentUser = auth.getCurrentUser();

        mDatabase = FirebaseDatabase.getInstance().getReference().child("Expenses");
        mDatabaseUser = FirebaseDatabase.getInstance().getReference().child("Users").child(mCurrentUser.getUid());
        mBalance = FirebaseDatabase.getInstance().getReference().child("Blog").child("budget");

        editTextItemName = (EditText) findViewById(R.id.editTextItemName);
        editTextExpenses = (EditText) findViewById(R.id.editTextExpenses);
        saveRecord = (Button) findViewById(R.id.saveRecord);
        date = (TextView) findViewById(R.id.txtDate);
        String mDate= DateFormat.getDateTimeInstance().format(new Date());
        date.setText(mDate);

        saveRecord.setOnClickListener(this);

        authListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user == null) {
                    startActivity(new Intent(TravelExpenseTracker.this, LoginActivity.class));
                    finish();
                }
            }
        };
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        if (progressBar != null) {
            progressBar.setVisibility(View.GONE);
        }
    }

    private void saveInformation() {
        final String itemDetails = editTextItemName.getText().toString().trim();
        final String expense = editTextExpenses.getText().toString().trim();
        final String mDate = date.getText().toString().trim();

        if(!TextUtils.isEmpty(itemDetails) && !TextUtils.isEmpty(expense) && !TextUtils.isEmpty(mDate)){
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Uploading");
            progressDialog.show();


            final DatabaseReference newPost = mDatabase.push();

            mDatabaseUser.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    newPost.child("itemDetails").setValue(itemDetails);
                    newPost.child("expense").setValue(expense);
                    newPost.child("mDate").setValue(mDate);
                    newPost.child("userId").setValue(mCurrentUser.getUid());
                    newPost.child("username").setValue(dataSnapshot.child("contact").getValue())
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        progressDialog.dismiss();
                                        Toast.makeText(getApplicationContext(), "Post Uploaded ", Toast.LENGTH_LONG).show();
                                        startActivity(new Intent(TravelExpenseTracker.this, TravelExpenseRecord.class));
                                    }
                                    else {
                                        progressDialog.dismiss();
                                        Toast.makeText(getApplicationContext(), "Post Uploading error ", Toast.LENGTH_LONG).show();
                                    }
                                }
                            });
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }

            });

        }
        if (TextUtils.isEmpty(itemDetails)) {
            Toast.makeText(this, "Please enter item Details", Toast.LENGTH_LONG).show();
            return;
        }

        if (TextUtils.isEmpty(expense)) {
            Toast.makeText(this, "Please enter expense", Toast.LENGTH_LONG).show();
            return;
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(), TravelExpenseRecord.class);
        startActivity(intent);
        finish();
    }
    @Override
    public void onClick(View view) {
        if (view == saveRecord) {
            saveInformation();
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_add, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
      /*  if (id == R.id.weather) {
//            Intent i = new Intent(.this, AddEventActivity.class);
//            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//            startActivity(i);
            return true;
        } else if (id == R.id.nearLocation) {
            return true;*/
      if (id == R.id.logout) {
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
