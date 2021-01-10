package com.example.chatapp.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.example.chatapp.Fragment.LoginFragment;
import com.example.chatapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {
    SharedPreferences sp;
    String uid;
    FirebaseUser fuser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sp = getSharedPreferences("login",MODE_PRIVATE);
        if(sp.getBoolean("logged",true)){
            fuser = FirebaseAuth.getInstance().getCurrentUser();
            if(fuser!= null)
                uid = fuser.getUid();
            Intent intent = new Intent(MainActivity.this,HomeActivity.class);
            intent.putExtra("UID",uid);
            startActivity(intent);
        }else{
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container1,
                new LoginFragment()).commit();}
    }
    @Override
    public void onBackPressed() {
        Intent a = new Intent(Intent.ACTION_MAIN);
        a.addCategory(Intent.CATEGORY_HOME);
        a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(a);
    }
}