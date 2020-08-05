package com.example.chatapp.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.chatapp.ModalClass.Chat;
import com.example.chatapp.R;
import com.example.chatapp.ModalClass.User;
import com.example.chatapp.Adapter.UserAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.TimeZone;

public class UsernameActivity extends AppCompatActivity {
    private UserAdapter adapter;
    RecyclerView recyclerView;
    private Context ctx;
    DatabaseReference databasenote;
    List<User> muser;
    String uid;
    SharedPreferences sp;
    FirebaseUser fuser;
    String email;
    int r;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        fuser = FirebaseAuth.getInstance().getCurrentUser();
        if(fuser!=null)
        uid = fuser.getUid();
        BottomNavigationView btview = findViewById(R.id.bottom_navigation);
        btview.setOnNavigationItemSelectedListener(navListener);
        sp = getSharedPreferences("login",MODE_PRIVATE);
        databasenote = FirebaseDatabase.getInstance().getReference("Users");
        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        muser= new ArrayList<>();
        ctx = UsernameActivity.this;
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        databasenote.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                muser.clear();
                if(snapshot.exists()){
                    for(DataSnapshot napshot : snapshot.getChildren()){
                        User l =napshot.getValue(User.class);
                        if(l!=null && fuser!=null && !(l.getUid().equals(uid)))
                        {
                            FirebaseDatabase.getInstance().getReference("Chats").addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    r=0;
                                    for(DataSnapshot dataSnapshot:snapshot.getChildren())
                                    {
                                        Chat chat = dataSnapshot.getValue(Chat.class);
                                        if(chat!=null)
                                            if(chat.getReceiver().equals(fuser.getUid()) && chat.getSender().equals(l.getUid()) ||
                                                    chat.getSender().equals(fuser.getUid()) && chat.getReceiver().equals(l.getUid()))
                                                r++;
                                    }
                                    if(r>0)
                                        muser.add(l);
                                    adapter = new UserAdapter(muser,ctx);
                                    recyclerView.setAdapter(adapter);
                                }
                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                }
                            });
                        }
                    } }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    BottomNavigationView.OnNavigationItemSelectedListener navListener=
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    switch (item.getItemId()){
                        case R.id.nav_search:
                            Intent i = new Intent(UsernameActivity.this,SearchActivity.class);
                            startActivity(i);
                            break;
                        case R.id.nav_notification:
                            Intent c = new Intent(UsernameActivity.this,NotificationActivity.class);
                            startActivity(c);
                            break;
                        case R.id.nav_profile:
                        Intent b = new Intent(UsernameActivity.this,ProfileActivity.class);
                        startActivity(b);
                        break;
                        case R.id.nav_newUser:
                            Intent a = new Intent(UsernameActivity.this,NewUserActivity.class);
                            startActivity(a);
                            break;
                    }
                        return true;
                }
            };
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.logout_menu, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch(item.getItemId()) {
            case R.id.subitem1:
               sp.edit().putBoolean("logged",false).apply();
                Intent intent  = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
                return true;
            case R.id.resetPassword:
                email = FirebaseAuth.getInstance().getCurrentUser().getEmail();
                FirebaseAuth.getInstance().sendPasswordResetEmail(email)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(UsernameActivity.this,"Password Reset Request Sent to your email",Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    @Override
    public void onBackPressed(){
        Intent a = new Intent(Intent.ACTION_MAIN);
        a.addCategory(Intent.CATEGORY_HOME);
        a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(a);
    }
private void status(String status){
    HashMap<String,Object> hashMap = new HashMap<>();
    hashMap.put("status",status);
    try{
    FirebaseDatabase.getInstance().getReference("Users").child(uid).updateChildren(hashMap);}
    catch (Exception e)
    {
        e.printStackTrace();
    }
}

    @Override
    protected void onResume() {
        super.onResume();
        try{
        status("online");}
        catch (Exception e)
        {
            e.printStackTrace();
        }
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
