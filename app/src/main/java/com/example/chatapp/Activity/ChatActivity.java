package com.example.chatapp.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.chatapp.ModalClass.Chat;
import com.example.chatapp.Adapter.ChatAdapter;
import com.example.chatapp.Notification.APIService;
import com.example.chatapp.Notification.Client;
import com.example.chatapp.Notification.Data;
import com.example.chatapp.Notification.MyResponse;
import com.example.chatapp.Notification.Sender;
import com.example.chatapp.Notification.Token;
import com.example.chatapp.R;
import com.example.chatapp.ModalClass.User;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.TimeZone;
import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatActivity extends AppCompatActivity {
    String imageUrl,message;
    int Pick_image =1;
    Toolbar mActionBarToolbar;
    String uid;
    String Name,ImageUrl,key,status;
    CircleImageView userImage;
    TextView userName,status1;
    EditText sendTxt;
    ImageView sendImage;
    ImageButton image;
    FirebaseUser fuser;
    ChatAdapter chatAdapter;
    List<Chat> mchat;
    RecyclerView recyclerView;
    Context ctx;
    ValueEventListener seenListener;
    DatabaseReference reference;
    APIService apiService;
    boolean notify = false;
    Uri imageUri,resultUri;
    int z=1;
    private StorageReference mStorageRef;
    private StorageTask mUploadTask;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat2);
        ChatActivity.this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        Intent i = getIntent();
        uid = i.getStringExtra("UID");
        Name = i.getStringExtra("Name");
        ImageUrl = i.getStringExtra("ImageUrl");
        apiService = Client.getClient("https://fcm.googleapis.com").create(APIService.class);
        userImage = findViewById(R.id.userImage);
        userName = findViewById(R.id.userName);
        status1 = findViewById(R.id.status1);
        sendImage = findViewById(R.id.sendButton);
        image = findViewById(R.id.sendimage);
        sendTxt = findViewById(R.id.sendTxt);
        mStorageRef = FirebaseStorage.getInstance().getReference("Chats");
        mActionBarToolbar = findViewById(R.id.toolbar_actionbar);
        setSupportActionBar(mActionBarToolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle("");
        recyclerView  = findViewById(R.id.recycler_view1);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        ctx = ChatActivity.this;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        fuser = FirebaseAuth.getInstance().getCurrentUser();
        mActionBarToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ChatActivity.this,HomeActivity.class);
                startActivity(i);
            }
        });
        Glide.with(ChatActivity.this).load(ImageUrl).into(userImage);
        FirebaseDatabase.getInstance().getReference("Users").child(uid)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        User user = snapshot.getValue(User.class);
                        assert user != null;
                        userName.setText(user.getName());
                        status1.setText("Last seen at : " + user.getStatus());
                        readmessage( fuser.getUid(), user.getUid(), user.getImageUrl());
//                        seenMessage(uid);
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
        sendImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                notify = true;
                String msg = sendTxt.getText().toString();
                msg = msg.trim();
                if(!msg.equals("")){
                    sendMessage(fuser.getUid(),uid,msg,"text");
                }
                else{
                    Toast.makeText(ChatActivity.this,"You can't send empty msg",Toast.LENGTH_SHORT).show(); }
                sendTxt.setText("");
             }
        });
        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent gallery = new Intent();
                gallery.setType("image/*");
                gallery.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(gallery,Pick_image);            }
        });
}
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Pick_image && resultCode == RESULT_OK) {
            assert data != null;
            imageUri = data.getData();
            CropImage.activity(imageUri)
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(1,1)
                    .start(this);
        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if(resultCode == RESULT_OK){
                assert result != null;
                resultUri = result.getUri();
                saveImageFuntion();
            }
        }
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
       Intent i = new Intent(ChatActivity.this, HomeActivity.class);
       startActivity(i);
       finish();
    }

    public void seenMessage(String useruid){
        reference = FirebaseDatabase.getInstance().getReference("Chats");
       seenListener=reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot:snapshot.getChildren()){
                    Chat c = dataSnapshot.getValue(Chat.class);
                    assert c != null;
                    if(c.getReceiver().equals(fuser.getUid()) && c.getSender().equals(useruid)){
                        HashMap<String,Object> hashMap = new HashMap<>();
                        hashMap.put("isseen",true);
                        dataSnapshot.getRef().updateChildren(hashMap);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    public void sendMessage(String sender, final String receiver, String msg,String type){
        HashMap<String,Object> hashMap = new HashMap<>();
        hashMap.put("sender",sender);
        hashMap.put("receiver",receiver);
        hashMap.put("msg",msg);
        hashMap.put("isseen",false);
        hashMap.put("type",type);
        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT+5:30"));
        Date currentLocalTime = cal.getTime();
        DateFormat date = new SimpleDateFormat("dd MMM,yyyy hh:mm a");
        date.setTimeZone(TimeZone.getTimeZone("GMT+5:30"));
        String time = date.format(currentLocalTime);
        hashMap.put("time",time);
        key = FirebaseDatabase.getInstance().getReference("Chats").push().getKey();
       hashMap.put("key",key);
        FirebaseDatabase.getInstance().getReference("Chats").child(key).setValue(hashMap);
        if(type=="text"){
         message = msg;}
        else{
            message = "Sent you a Photo";
        }
        reference = FirebaseDatabase.getInstance().getReference("Users").child(fuser.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                if(notify) {
                    sendNotification(receiver, user.getName(), message);
                }
                notify = false;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    public void sendNotification(String receiver,String username,String message){
        DatabaseReference tokens = FirebaseDatabase.getInstance().getReference("Tokens");
        Query query = tokens.orderByKey().equalTo(receiver);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot:snapshot.getChildren()){
                    Token token = dataSnapshot.getValue(Token.class);
                    Data data = new Data(fuser.getUid(),R.mipmap.ic_launcher,username+": "+message,"Chat App",uid);
                    Sender sender = new Sender(data,token.getToken());
                    apiService.sendNotification(sender)
                            .enqueue(new Callback<MyResponse>() {
                                @Override
                                public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {
                                    if(response.code() == 200){
                                        if(response.body().success!=1){
                                            Toast.makeText(ChatActivity.this,"Failed!",Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                }

                                @Override
                                public void onFailure(Call<MyResponse> call, Throwable t) {

                                }
                            });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    public void readmessage(final String myid, final String userid, final String imageUrl){
        mchat= new ArrayList<>();
        FirebaseDatabase.getInstance().getReference("Chats")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        mchat.clear();
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            Chat chat = dataSnapshot.getValue(Chat.class);
                                if (chat != null)
                                    if (chat.getReceiver().equals(myid) && chat.getSender().equals(userid) ||
                                            chat.getReceiver().equals(userid) && chat.getSender().equals(myid)) {
                                        mchat.add(chat);
                                    }
                                chatAdapter = new ChatAdapter(userid, myid, mchat, ctx, imageUrl);
                                recyclerView.setAdapter(chatAdapter);
                                recyclerView.scrollToPosition(mchat.size() - 1);
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.delete_message, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.delete_message) {
            FirebaseDatabase.getInstance().getReference("Chats")
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if(snapshot.exists()){
                                for(DataSnapshot dataSnapshot:snapshot.getChildren()){
                                    Chat l = dataSnapshot.getValue(Chat.class);
                                    if(l!=null)
                                        if(l.getReceiver().equals(fuser.getUid()) && l.getSender().equals(uid) ||
                                                l.getReceiver().equals(uid) && l.getSender().equals(fuser.getUid())){
                                           FirebaseDatabase.getInstance().getReference("Chats").child(l.getKey()).removeValue();
                                        }
                                }
                                Toast.makeText(ChatActivity.this,"Msg deleted",Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
            return false;
        }
        return super.onOptionsItemSelected(item);
    }
    private void status(String status){
        FirebaseDatabase.getInstance().getReference("Users").child(fuser.getUid()).child("status").setValue(status);
    }

    @Override
    protected void onResume() {
        super.onResume();
        status("online");
        Log.i("TAG", "onResume: ");
        seenMessage(uid);
    }

    @Override
    protected void onPause() {
        super.onPause();
        reference.removeEventListener(seenListener);
        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT+5:30"));
        Date currentLocalTime = cal.getTime();
        DateFormat date = new SimpleDateFormat("dd MMM,yyyy hh:mm a");
        date.setTimeZone(TimeZone.getTimeZone("GMT+5:30"));
        String time = date.format(currentLocalTime);
        status(time);
    }
    private String getFileExtension(Uri uri) {
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }
    public void saveImageFuntion(){
        String random = UUID.randomUUID().toString();
        StorageReference fileReference = mStorageRef.child(random);
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
                                        notify = true;
                                        sendMessage(fuser.getUid(),uid,imageUrl,"image");
                                    }
                                });
                            }
                        }
                    }
                });
    }

}
