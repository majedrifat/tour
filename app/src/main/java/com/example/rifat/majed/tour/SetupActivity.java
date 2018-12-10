package com.example.rifat.majed.tour;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;

public class SetupActivity extends AppCompatActivity {
    private ImageButton mImageButton;
    private Button mSubmit;
    private EditText mEditName, editTextNumber, editTextAddress;
    private Uri mImageUri;

    private FirebaseAuth mAuth;

    private DatabaseReference mDatabaseUser;

    private StorageReference mStorage;

    private static final int PICK_IMAGE_REQUEST = 234;

    private ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar); // get the reference of Toolbar
        toolbar.setLogo(R.mipmap.ic_launcher);
        setSupportActionBar(toolbar); // Setting/replace toolbar as the ActionBar

        mAuth = FirebaseAuth.getInstance();
        mDatabaseUser = FirebaseDatabase.getInstance().getReference().child("Users");
        mStorage= FirebaseStorage.getInstance().getReference().child("Profile_images");
        progressDialog = new ProgressDialog(this);
        mImageButton = (ImageButton) findViewById(R.id.addImage);
        editTextNumber = (EditText) findViewById(R.id.editTextContact);
        editTextAddress = (EditText) findViewById(R.id.editTextAddress);
        mEditName = (EditText) findViewById(R.id.addName);
        mSubmit = (Button) findViewById(R.id.upade);
        mSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startSetupAccount();
            }
        });
        mImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
            }
        });
    }

    private void startSetupAccount() {
        final String name = mEditName.getText().toString().trim();
        final String userContact = editTextNumber.getText().toString().trim();
        final String address = editTextAddress.getText().toString().trim();
        final String user_id = mAuth.getCurrentUser().getUid();
        if(!TextUtils.isEmpty(name) && mImageUri != null && !TextUtils.isEmpty(userContact) && !TextUtils.isEmpty(address)){
            progressDialog.setMessage("Finishing account...");
            progressDialog.show();
            StorageReference filepath = mStorage.child(mImageUri.getLastPathSegment());
            filepath.putFile(mImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    progressDialog.dismiss();
                    String downloadUri =taskSnapshot.getStorage().getDownloadUrl().toString();
                    mDatabaseUser.child(user_id).child("contact").setValue(name);
                    mDatabaseUser.child(user_id).child("number").setValue(userContact);
                    mDatabaseUser.child(user_id).child("address").setValue(address);
                    mDatabaseUser.child(user_id).child("image").setValue(downloadUri);
                    progressDialog.dismiss();
                    Intent intent = new Intent(SetupActivity.this, HomePage.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);


                }
            });
        }else {
            progressDialog.dismiss();
            Toast.makeText(this, "Please enter name and upload profile image.", Toast.LENGTH_LONG).show();
            return;
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK ) {
            mImageUri = data.getData();
            Bitmap bitmap = null;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), mImageUri);
                mImageButton.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }
 /*   @Override*/
   /* public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.weather) {

            return true;
        }else if (id == R.id.nearLocation) {

            return true;
        }
        return super.onOptionsItemSelected(item);
    }*/
}