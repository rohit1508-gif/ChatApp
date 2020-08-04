package com.example.chatapp.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.chatapp.R;
import com.example.chatapp.ModalClass.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.SignInMethodQueryResult;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity {
    EditText editText,editText2,editText3;
    Button button;
    FirebaseAuth auth;
    TextView textView2;
    CircleImageView imageView;
    int Pick_image =1,a;
    Uri imageUri;
    String uid,imageUrl;
    DatabaseReference databasenote;
    private StorageReference mStorageRef;
    private StorageTask mUploadTask;
    String name,email,password;
    ImageButton imageButton2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        editText3 = findViewById(R.id.editText3);
         a=1;
        editText = findViewById(R.id.editText);
        imageButton2 = findViewById(R.id.imageButton2);
        editText2 = findViewById(R.id.editText2);
        button = findViewById(R.id.button);
        textView2 = findViewById(R.id.textView2);
        imageView = findViewById(R.id.imageView);
        auth = FirebaseAuth.getInstance();
        databasenote = FirebaseDatabase.getInstance().getReference("Users");
        mStorageRef = FirebaseStorage.getInstance().getReference("Users");
        imageButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(a==1)
                {
                    editText2.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    a=0;
                }
               else if(a==0){
                    editText2.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    a=1;
                }
            }
        });
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent gallery = new Intent();
                gallery.setType("image/*");
                gallery.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(gallery,"Select Picture"),Pick_image);
            }
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Pick_image && resultCode == RESULT_OK) {
            assert data != null;
            imageUri = data.getData();
            imageView.setImageURI(imageUri);
        }
    }
    private String getFileExtension(Uri uri) {
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }

    public void signupFunction(View v)
    {
        name =  editText3.getText().toString();
        email = editText.getText().toString();
        password = editText2.getText().toString();
        auth.fetchSignInMethodsForEmail(email)
                .addOnCompleteListener(new OnCompleteListener<SignInMethodQueryResult>() {
                    @Override
                    public void onComplete(@NonNull Task<SignInMethodQueryResult> task) {
                        boolean isNew = task.getResult().getSignInMethods().isEmpty();
                        if(isNew){
                            startFunction();
                        }
                        else{
                            Toast.makeText(MainActivity.this,"Email is already registred,Please try another!",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
    public void startFunction()
    {
        if(email.isEmpty())
        {
            editText.setError("Please enter Email Id");
            editText.requestFocus();
        }
        else if(password.isEmpty())
        {
            editText2.setError("Please enter Password");
            editText2.requestFocus();
        }
        else if(name.isEmpty())
        {
            editText3.setError("Please enter Name");
            editText3.requestFocus();
        }
        else {
            auth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                Log.d("EmailPassword", "signUpWithEmail:success");
                               FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                                if(user != null)
                                uid = user.getUid();
                                StorageReference fileReference = mStorageRef.child(uid).child(getFileExtension(imageUri));
                                mUploadTask = fileReference.putFile(imageUri)
                                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                            @Override
                                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                                if (taskSnapshot.getMetadata() != null) {
                                                    if (taskSnapshot.getMetadata().getReference() != null) {
                                                        Task<Uri> result = taskSnapshot.getStorage().getDownloadUrl();
                                                        result.addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                            @Override
                                                            public void onSuccess(Uri uri) {
                                                                imageUrl = uri.toString();
                                                                HashMap<String,Object> hashMap = new HashMap<>();
                                                                hashMap.put("uid",uid);
                                                                hashMap.put("name",name);
                                                                hashMap.put("imageUrl",imageUrl);
                                                                databasenote.child(uid).setValue(hashMap);
                                                                sendEmailVerification();
                                                                Intent intent = new Intent(getApplicationContext(), UsernameActivity.class);
                                                                startActivity(intent);
                                                            }
                                                        });
                                                    }
                                                }
                                            }
                                        });

                            } else {
                                Log.w("EmailPassword", "signUpWithEmail:failure", task.getException());
                                Toast.makeText(MainActivity.this, "Authentication failed.",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }
    private void sendEmailVerification() {
        final FirebaseUser user = auth.getCurrentUser();
        if(user!=null)
        user.sendEmailVerification()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if (task.isSuccessful()) {
                            Toast.makeText(MainActivity.this,
                                    "Verification email sent to " + user.getEmail(),
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            Log.e("EmailPassword", "sendEmailVerification", task.getException());
                            Toast.makeText(MainActivity.this,
                                    "Failed to send verification email.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
    public void signinFunction(View v)
    {
        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        startActivity(intent);
    }

}
