package com.example.khem.javaproject;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.khem.javaproject.Model.Food;
import com.example.khem.javaproject.Model.list_food;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;


public class add_food extends AppCompatActivity {
    private DatabaseReference mDatabase;
    private StorageReference mStorageRef;
    private TextView text_name, text_price, text_des;
    private Button btn_save, btn_image;
    private Spinner spinnercat;

    private static final int PICK_IMAGE_REQUEST = 1;
    private ImageView imageView;

    private Uri mImageUri;

    private StorageTask mUploadTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_food);

        mDatabase = FirebaseDatabase.getInstance().getReference("menu");
        mStorageRef = FirebaseStorage.getInstance().getReference("menu");

        text_name = (TextView) findViewById(R.id.text_name);
        text_price = (TextView) findViewById(R.id.text_price);
        text_des = (TextView) findViewById(R.id.text_des);

        btn_save = (Button) findViewById(R.id.btn_save);
        btn_image = (Button) findViewById(R.id.btn_image);

        imageView = (ImageView) findViewById(R.id.imageView);
        spinnercat = (Spinner) findViewById(R.id.spinnercat);

        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addFood();
            }
        });

        btn_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFileChooser();
            }
        });

    }

    // choose file
    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            mImageUri = data.getData();

            Picasso.get().load(mImageUri).into(imageView);
        }
    }


    private void addFood() {

        final String name = text_name.getText().toString().trim();
        final String price = text_price.getText().toString().trim();
        final String desc = text_des.getText().toString().trim();
        final String category = spinnercat.getSelectedItem().toString();

        if (!TextUtils.isEmpty(name)) {

            final String id = mDatabase.push().getKey();
            if (mImageUri != null) {
                StorageReference fileReference = mStorageRef.child(System.currentTimeMillis() + "." + getFileExtension(mImageUri));

                mUploadTask = fileReference.putFile(mImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Task<Uri> urlTask = taskSnapshot.getStorage().getDownloadUrl();
                        while (!urlTask.isSuccessful());
                        Uri downloadUrl = urlTask.getResult();
                        Food food = new Food(id, name, desc, price, category,downloadUrl.toString());
                        mDatabase.push().setValue(food);
                    }
                        });
            } else {
                Toast.makeText(this, "No file selected", Toast.LENGTH_SHORT).show();
            }

            Toast.makeText(this, "Food Added", Toast.LENGTH_LONG).show();
            startActivity(new Intent(add_food.this, list_food.class));
        } else {
            Toast.makeText(this, "You should enter a name", Toast.LENGTH_LONG).show();
        }
    }

    private String getFileExtension(Uri uri) {
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }

}
