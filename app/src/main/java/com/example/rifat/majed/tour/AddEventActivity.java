package com.example.rifat.majed.tour;

import android.app.DatePickerDialog;
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
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
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

import java.util.Calendar;

public class AddEventActivity extends AppCompatActivity implements View.OnClickListener{
    private ProgressBar progressBar;
    private FirebaseAuth.AuthStateListener authListener;
    private FirebaseAuth auth;

    //defining a database reference
    private DatabaseReference mDatabase;
    private DatabaseReference mDatabaseUser;

    private FirebaseUser mCurrentUser;

    //our new views
    private EditText editTextDestination, editTextBudget, editTextFromDate, editTextToDate;
    private Button btnCreateEvent, fromDatePicker, toDatePicker;
    private int mYear, mMonth, mDay;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_event);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar); // get the reference of Toolbar
        setSupportActionBar(toolbar); // Setting/replace toolbar as the ActionBar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.back));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AddEventActivity.this, HomePage.class);
                startActivity(intent);
                finish();
            }
        });
        auth = FirebaseAuth.getInstance();
        mCurrentUser = auth.getCurrentUser();

        mDatabase = FirebaseDatabase.getInstance().getReference().child("Blog");
        mDatabaseUser = FirebaseDatabase.getInstance().getReference().child("Users").child(mCurrentUser.getUid());

        editTextDestination = (EditText) findViewById(R.id.editTextDestination);
        editTextBudget = (EditText) findViewById(R.id.editTextBudget);
        editTextFromDate = (EditText) findViewById(R.id.fromDate);
        editTextToDate = (EditText) findViewById(R.id.toDate);
        btnCreateEvent = (Button) findViewById(R.id.createEvent);
        fromDatePicker = (Button) findViewById(R.id.from_date);
        toDatePicker = (Button) findViewById(R.id.to_date);

        fromDatePicker.setOnClickListener(this);
        toDatePicker.setOnClickListener(this);
        btnCreateEvent.setOnClickListener(this);

        authListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user == null) {
                    startActivity(new Intent(AddEventActivity.this, LoginActivity.class));
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
        final String destination = editTextDestination.getText().toString().trim();
        final String budget = editTextBudget.getText().toString().trim();
        final String fromDate = editTextFromDate.getText().toString().trim();
        final String toDate = editTextToDate.getText().toString().trim();

        if(!TextUtils.isEmpty(destination) && !TextUtils.isEmpty(budget) && !TextUtils.isEmpty(fromDate) &&
                !TextUtils.isEmpty(toDate) ){
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Uploading");
            progressDialog.show();


                            final DatabaseReference newPost = mDatabase.push();

                            mDatabaseUser.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {

                                    newPost.child("destination").setValue(destination);
                                    newPost.child("budget").setValue(budget);
                                    newPost.child("fromDate").setValue(fromDate);
                                    newPost.child("toDate").setValue(toDate);
                                    newPost.child("userId").setValue(mCurrentUser.getUid());
                                    newPost.child("username").setValue(dataSnapshot.child("contact").getValue())
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if(task.isSuccessful()){
                                                        progressDialog.dismiss();
                                                        Toast.makeText(getApplicationContext(), "Post Uploaded ", Toast.LENGTH_LONG).show();
                                                        startActivity(new Intent(AddEventActivity.this, HomePage.class));
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
        if (TextUtils.isEmpty(destination)) {
            Toast.makeText(this, "Please enter destination", Toast.LENGTH_LONG).show();
            return;
        }

        if (TextUtils.isEmpty(budget)) {
            Toast.makeText(this, "Please enter budget", Toast.LENGTH_LONG).show();
            return;
        }
        if (TextUtils.isEmpty(fromDate)) {
            Toast.makeText(this, "Please enter from Date", Toast.LENGTH_LONG).show();
            return;
        }
        if (TextUtils.isEmpty(toDate)) {
            Toast.makeText(this, "Please enter to Date", Toast.LENGTH_LONG).show();
            return;
        }
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
    }

    @Override
    public void onStop() {
        super.onStop();
        if (authListener != null) {
            auth.removeAuthStateListener(authListener);
        }
    }
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(), HomePage.class);
        startActivity(intent);
        finish();
    }
    @Override
    public void onClick(View view) {
        if (view == btnCreateEvent) {
            saveInformation();
        }if (view == fromDatePicker) {

            // Get Current Date
            final Calendar c = Calendar.getInstance();
            mYear = c.get(Calendar.YEAR);
            mMonth = c.get(Calendar.MONTH);
            mDay = c.get(Calendar.DAY_OF_MONTH);


            DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                    new DatePickerDialog.OnDateSetListener() {

                        @Override
                        public void onDateSet(DatePicker view, int year,
                                              int monthOfYear, int dayOfMonth) {

                            editTextFromDate.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);

                        }
                    }, mYear, mMonth, mDay);
            datePickerDialog.show();
        }if (view == toDatePicker) {

            // Get Current Date
            final Calendar c = Calendar.getInstance();
            mYear = c.get(Calendar.YEAR);
            mMonth = c.get(Calendar.MONTH);
            mDay = c.get(Calendar.DAY_OF_MONTH);


            DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                    new DatePickerDialog.OnDateSetListener() {

                        @Override
                        public void onDateSet(DatePicker view, int year,
                                              int monthOfYear, int dayOfMonth) {

                            editTextToDate.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);

                        }
                    }, mYear, mMonth, mDay);
            datePickerDialog.show();
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
