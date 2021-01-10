package com.example.chatapp.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.example.chatapp.Fragment.NewUsernameFragment;
import com.example.chatapp.Fragment.NotificationFragment;
import com.example.chatapp.Fragment.ProfileFragment;
import com.example.chatapp.Fragment.SearchFragment;
import com.example.chatapp.Fragment.UsernameFragment;
import com.example.chatapp.Notification.Token;
import com.example.chatapp.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.TimeZone;

public class HomeActivity extends AppCompatActivity {
    SharedPreferences sp;
    String uid;
    FirebaseUser fuser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        sp = getSharedPreferences("login",MODE_PRIVATE);
        sp.edit().putBoolean("logged",true).apply();
        BottomNavigationView btview = findViewById(R.id.bottom_navigation);
        btview.setOnNavigationItemSelectedListener(navListener);
        fuser = FirebaseAuth.getInstance().getCurrentUser();
        if(fuser!=null)
            uid = fuser.getUid();
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                new UsernameFragment()).commit();
        updateToken(FirebaseInstanceId.getInstance().getToken());
    }
    BottomNavigationView.OnNavigationItemSelectedListener navListener=
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    switch (item.getItemId()){
                        case R.id.nav_home:
                            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                                    new UsernameFragment()).commit();
                            break;
                        case R.id.nav_search:
                            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                                    new SearchFragment()).commit();
                            break;
                        case R.id.nav_notification:
                            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                                    new NotificationFragment()).commit();
                            break;
                        case R.id.nav_profile:
                            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                                    new ProfileFragment()).commit();
                            break;
                        case R.id.nav_newUser:
                            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                                    new NewUsernameFragment()).commit();
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
                Intent intent  = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                return true;
            case R.id.resetPassword:
                Intent i = new Intent(getApplicationContext(),ResetPasswordActivity.class);
                startActivity(i);
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
        private void updateToken(String token){
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Tokens");
            Token token1 = new Token(token);
            reference.child(fuser.getUid()).setValue(token1);
        }
}