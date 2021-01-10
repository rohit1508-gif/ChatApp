package com.example.chatapp.Fragment;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.chatapp.Activity.HomeActivity;
import com.example.chatapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.SignInMethodQueryResult;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.app.Activity.RESULT_OK;

public class RegistrationFragment extends Fragment {
    EditText editText,editText2,editText3;
    Button button;
    FirebaseAuth auth;
    TextView textView2;
    CircleImageView imageView;
    int Pick_image =1;
    Uri imageUri,resultUri;
    String uid,imageUrl;
    DatabaseReference databasenote;
    private StorageReference mStorageRef;
    private StorageTask mUploadTask;
    String name,email,password;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_registration, container, false);
        editText3 = view.findViewById(R.id.editText3);
        editText = view.findViewById(R.id.edittext);
        editText2 = view.findViewById(R.id.editText2);
        button = view.findViewById(R.id.button);
        textView2 = view.findViewById(R.id.textView2);
        imageView = view.findViewById(R.id.imageView);
        auth = FirebaseAuth.getInstance();
        databasenote = FirebaseDatabase.getInstance().getReference("Users");
        mStorageRef = FirebaseStorage.getInstance().getReference("Users");
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signupFunction();
            }
        });
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ContextCompat.checkSelfPermission(Objects.requireNonNull(getContext()), Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED)
                {
                    // Log.e(TAG, "setxml: peremission prob");
                    ActivityCompat.requestPermissions((Activity) getContext(),new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},0);


                }
                else{
                Intent gallery = new Intent();
                gallery.setType("image/*");
                gallery.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(gallery,"Select Picture"),Pick_image);}
            }
        });
        textView2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment someFragment = new LoginFragment();
                assert getFragmentManager() != null;
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.fragment_container1, someFragment );
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });
        return view;
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Pick_image && resultCode == RESULT_OK) {
            assert data != null;
            imageUri = data.getData();
            CropImage.activity(imageUri)
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(1,1)
                    .start(getActivity());
        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if(resultCode == RESULT_OK){
                assert result != null;
                resultUri = result.getUri();
                imageView.setImageURI(resultUri);
            }
        }
    }
    private String getFileExtension(Uri uri) {
        ContentResolver cR = getActivity().getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }

    public void signupFunction()
    {
        name =  editText3.getText().toString();
        email = editText.getText().toString();
        password = editText2.getText().toString();
        FirebaseDatabase.getInstance().getReference("Users")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists())
                            for(DataSnapshot dataSnapshot:snapshot.getChildren()){
                                String uname = dataSnapshot.child("name").getValue().toString();
                                if(uname.equals(name)){
                                    Toast.makeText(getActivity(),"Username is already in use,Please try another!",Toast.LENGTH_SHORT).show();
                                }
                                else{
                                    auth.fetchSignInMethodsForEmail(email)
                                            .addOnCompleteListener(new OnCompleteListener<SignInMethodQueryResult>() {
                                                @Override
                                                public void onComplete(@NonNull Task<SignInMethodQueryResult> task) {
                                                    boolean isNew = task.getResult().getSignInMethods().isEmpty();
                                                    if(isNew){
                                                        startFunction(name,email,password);
                                                    }
                                                    else{
                                                        Toast.makeText(getActivity(),"Email is already registered,Please try another!",Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            });
                                }
                            }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }
    public void startFunction(String name,String email,String password)
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
        else if(resultUri==null)
        {
            Toast.makeText(getActivity(),"Please select a profile picture",Toast.LENGTH_SHORT).show();
            imageView.requestFocus();
        }
        else {
            auth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                Log.d("EmailPassword", "signUpWithEmail:success");
                                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                                if (user != null) {
                                    uid = user.getUid();
                                    StorageReference fileReference = mStorageRef.child(uid).child(getFileExtension(imageUri));
                                    mUploadTask = fileReference.putFile(resultUri)
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
                                                                    HashMap<String, Object> hashMap = new HashMap<>();
                                                                    hashMap.put("uid", uid);
                                                                    hashMap.put("name", name);
                                                                    hashMap.put("imageUrl", imageUrl);
                                                                    databasenote.child(uid).setValue(hashMap);
                                                                    sendEmailVerification();
                                                                    Toast.makeText(getActivity(), "Registered Successfully", Toast.LENGTH_SHORT).show();
                                                                    Intent intent = new Intent(getContext(), HomeActivity.class);
                                                                    startActivity(intent);
                                                                }
                                                            });
                                                        }
                                                    }
                                                }
                                            });
                                }}
                            else {
                                Log.w("EmailPassword", "signUpWithEmail:failure", task.getException());
                                Toast.makeText(getActivity(), "Authentication failed",Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }
    private void sendEmailVerification() {
        final FirebaseUser user = auth.getCurrentUser();
        if(user!=null)
            user.sendEmailVerification()
                    .addOnCompleteListener( getActivity(), new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if (task.isSuccessful()) {
                                Toast.makeText(getActivity(),
                                        "Verification email sent to " + user.getEmail(),
                                        Toast.LENGTH_SHORT).show();
                            } else {
                                Log.e("EmailPassword", "sendEmailVerification", task.getException());
                                Toast.makeText(getActivity(),
                                        "Failed to send verification email.",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
    }
}
