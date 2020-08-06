package com.example.chatapp.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.os.Bundle;

import com.example.chatapp.Adapter.NotificationAdapter;
import com.example.chatapp.ModalClass.User;
import com.example.chatapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class NotificationActivity extends AppCompatActivity {
    List<User> muser;
    NotificationAdapter adapter;
    RecyclerView recyclerView;
    Context ctx;
    String uid;
    FirebaseUser fuser = FirebaseAuth.getInstance().getCurrentUser();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);
        muser = new ArrayList<>();
        ctx = NotificationActivity.this;
        recyclerView = findViewById(R.id.recycler_view3);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        FirebaseDatabase.getInstance().getReference("Users").child(fuser.getUid()).child("Request")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists())
                      for(DataSnapshot dataSnapshot:snapshot.getChildren()){
                          uid = dataSnapshot.getValue().toString();
                          FirebaseDatabase.getInstance().getReference("Users").addValueEventListener(new ValueEventListener() {
                              @Override
                              public void onDataChange(@NonNull DataSnapshot snapshot) {
                                  for(DataSnapshot dataSnapshot1:snapshot.getChildren()){
                                      User u = dataSnapshot1.getValue(User.class);
                                      if(u!=null)
                                          if(u.getUid().equals(uid))
                                              muser.add(u);
                                  }
                                  adapter = new NotificationAdapter(muser,ctx);
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
}
