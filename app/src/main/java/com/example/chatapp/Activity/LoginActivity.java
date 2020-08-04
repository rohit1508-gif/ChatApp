package com.example.chatapp.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.chatapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {
    EditText editText, editText2;
    Button button;
    FirebaseAuth auth;
    TextView textView2,textView3;
   String uid;
    SharedPreferences sp;
    FirebaseUser fuser;
    ImageButton imageButton3;
    int b;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        editText = findViewById(R.id.editText);
        b=1;
        imageButton3 = findViewById(R.id.imageButton3);
        editText2 = findViewById(R.id.editText2);
        textView3= findViewById(R.id.textView3);
        button = findViewById(R.id.button);
        auth = FirebaseAuth.getInstance();
        textView2 = findViewById(R.id.textView2);
        sp = getSharedPreferences("login",MODE_PRIVATE);
       if(sp.getBoolean("logged",true)){
           fuser = FirebaseAuth.getInstance().getCurrentUser();
           if(fuser!= null)
          uid = fuser.getUid();
           Intent intent = new Intent(getApplicationContext(), UsernameActivity.class);
            intent.putExtra("UID",uid);
          startActivity(intent);
       }
       textView3.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               Intent i = new Intent(LoginActivity.this,ResetPasswordActivity.class);
               startActivity(i);
           }
       });
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               String email = editText.getText().toString();
                String password = editText2.getText().toString();
                signin1Function(email,password);
            }
        });
       imageButton3.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               if(b==1)
               {
                   editText2.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                   b=0;
               }
               else if(b==0){
                   editText2.setTransformationMethod(PasswordTransformationMethod.getInstance());
                   b=1;
               }
           }
       });
    }
    public void signin1Function(String email,String password) {
        if (email.isEmpty()) {
            editText.setError("Please enter Email Id");
            editText.requestFocus();
        } else if (password.isEmpty()) {
            editText2.setError("Please enter Password");
            editText2.requestFocus();
        } else {
            auth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(LoginActivity.this,"Login In Successful",Toast.LENGTH_SHORT).show();
                                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                                if(user != null)
                                {uid = user.getUid();}
                                Intent intent = new Intent(getApplicationContext(), UsernameActivity.class);
                                startActivity(intent);
                                sp.edit().putBoolean("logged",true).apply();

                            } else {
                                Log.w("EmailPassword", "signInWithEmail:failure", task.getException());
                                Toast.makeText(LoginActivity.this, "Authentication failed.",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }
    public void signup1Function(View v){
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
    }
    @Override
    public void onBackPressed(){
        Intent a = new Intent(Intent.ACTION_MAIN);
        a.addCategory(Intent.CATEGORY_HOME);
        a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(a);
    }
}
