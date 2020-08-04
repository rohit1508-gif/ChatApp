package com.example.chatapp.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.chatapp.ModalClass.User;
import com.example.chatapp.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.TimeZone;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {
TextView textView;
CircleImageView imageView2;
FirebaseUser fuser;
String name,imageUrl,uid,imageUrl1;
ImageButton imageButton;
EditText editText4;
int Pick_image =1;
Uri imageUri;
private StorageReference mStorageRef;
private StorageTask mUploadTask;
Button button2,button3;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
       textView = findViewById(R.id.textView);
       imageView2 = findViewById(R.id.imageView2);
       imageButton =findViewById(R.id.imageButton);
       button2 =findViewById(R.id.button2);
       button3 = findViewById(R.id.button3);
       editText4 = findViewById(R.id.editText4);
       mStorageRef = FirebaseStorage.getInstance().getReference("Users");
       button2.setEnabled(false);
       button3.setEnabled(false);
       imageButton.setEnabled(true);
       fuser = FirebaseAuth.getInstance().getCurrentUser();
        uid = fuser.getUid();
       FirebaseDatabase.getInstance().getReference("Users").addValueEventListener(new ValueEventListener() {
           @Override
           public void onDataChange(@NonNull DataSnapshot snapshot) {
               for(DataSnapshot dataSnapshot:snapshot.getChildren()){
                   User u  = dataSnapshot.getValue(User.class);
                   if(u!=null)
                       if(u.getUid().equals(uid))
                       {
                           name = u.getName();
                           imageUrl=u.getImageUrl();
                       }
               }try{
               textView.setText(name);
               editText4.setText(name);
               Glide.with(ProfileActivity.this).load(imageUrl).centerCrop().into(imageView2);}
               catch (IllegalArgumentException e){
                   e.printStackTrace();
               }
           }

           @Override
           public void onCancelled(@NonNull DatabaseError error) {

           }
       });
       imageButton.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               textView.setAlpha(0);
               imageButton.setAlpha(0f);
               editText4.setAlpha(1);
               button2.setAlpha(1);
               button2.setEnabled(true);
               imageButton.setEnabled(false);
           }
       });
       button2.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               String a = editText4.getText().toString();
               FirebaseDatabase.getInstance().getReference("Users").child(uid).child("name").setValue(a);
               editText4.setAlpha(0);
               button2.setAlpha(0);
               textView.setAlpha(1);
               imageButton.setAlpha(1f);
               button2.setEnabled(false);
               imageButton.setEnabled(true);
           }
       });
imageView2.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        Intent gallery = new Intent();
        gallery.setType("image/*");
        gallery.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(gallery,"Select Picture"),Pick_image);
        button3.setAlpha(1);
        button3.setEnabled(true);
    }
});
button3.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        saveFunction();
    }
});
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==Pick_image && resultCode==RESULT_OK) {
            assert data != null;
            imageUri = data.getData();
            imageView2.setImageURI(imageUri);
        }
    }
    private String getFileExtension(Uri uri) {
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }
    public void saveFunction(){
        StorageReference fileReference = mStorageRef.child(fuser.getUid()).child(getFileExtension(imageUri));
        mUploadTask = fileReference.putFile(imageUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        if (taskSnapshot.getMetadata() != null)
                            if (taskSnapshot.getMetadata().getReference() != null) {
                                Task<Uri> result = taskSnapshot.getStorage().getDownloadUrl();
                                result.addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        imageUrl1 = uri.toString();
                                      FirebaseDatabase.getInstance().getReference("Users").child(fuser.getUid()).child("imageUrl").setValue(imageUrl1);
                                      button3.setAlpha(0);
                                      button3.setEnabled(false);
                                    }
                                });
                            }
                    }
                });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent i = new Intent(ProfileActivity.this,UsernameActivity.class);
        startActivity(i);
    }
    private void status(String status){
        FirebaseDatabase.getInstance().getReference("Users").child(fuser.getUid()).child("status").setValue(status);
    }

    @Override
    protected void onResume() {
        super.onResume();
        status("online");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT+5:30"));
        Date currentLocalTime = cal.getTime();
        DateFormat date = new SimpleDateFormat("dd MMM,yyyy hh:mm a");
        date.setTimeZone(TimeZone.getTimeZone("GMT+5:30"));
        String time = date.format(currentLocalTime);
        status(time);
    }
}
