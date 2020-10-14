package com.example.chatapp.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import com.example.chatapp.Adapter.NewUserAdapter;
import com.example.chatapp.Adapter.NotificationAdapter;
import com.example.chatapp.Adapter.UserAdapter;
import com.example.chatapp.ModalClass.User;
import com.example.chatapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.TimeZone;

public class NewUserActivity extends AppCompatActivity {
    private NewUserAdapter adapter;
    RecyclerView recyclerView;
    private Context ctx;
    String uid;
    FirebaseUser fuser;
    List<User> muser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_user);
        fuser = FirebaseAuth.getInstance().getCurrentUser();
        recyclerView = findViewById(R.id.recycler_view1);
        recyclerView.setHasFixedSize(true);
        muser= new ArrayList<>();
        ctx = NewUserActivity.this;
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        FirebaseDatabase.getInstance().getReference("Users").child(fuser.getUid()).child("Friends").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                muser.clear();
                if(snapshot.exists())
                      for(DataSnapshot dataSnapshot:snapshot.getChildren()){
                          uid = Objects.requireNonNull(dataSnapshot.getValue()).toString();
                          FirebaseDatabase.getInstance().getReference("Users").child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
                              @Override
                              public void onDataChange(@NonNull DataSnapshot snapshot) {
                                      User u = snapshot.getValue(User.class);
                                      if(u!=null)
                                          muser.add(u);
                                  adapter = new NewUserAdapter(muser,ctx);
                                  recyclerView.setAdapter(adapter);
                              }
                              @Override
                              public void onCancelled(@NonNull DatabaseError error) {

                              }
                          });
                      }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
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
