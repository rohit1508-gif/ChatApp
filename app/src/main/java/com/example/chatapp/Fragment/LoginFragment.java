package com.example.chatapp.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.chatapp.Activity.HomeActivity;
import com.example.chatapp.Activity.ResetPasswordActivity;
import com.example.chatapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginFragment extends Fragment {
    EditText editText, editText2;
    Button button;
    FirebaseAuth auth;
    TextView textView2,textView3;
    String uid;
    FirebaseUser fuser;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);
        editText = view.findViewById(R.id.edittext);
        editText2 = view.findViewById(R.id.editText2);
        textView3= view.findViewById(R.id.textView3);
        button = view.findViewById(R.id.button);
        auth = FirebaseAuth.getInstance();
        textView2 = view.findViewById(R.id.textView2);
        textView3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               Intent i = new Intent(getActivity(), ResetPasswordActivity.class);
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
        textView2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment someFragment = new RegistrationFragment();
                assert getFragmentManager() != null;
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.fragment_container1, someFragment );
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });
        return view;
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
                    .addOnCompleteListener( getActivity(), new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(getActivity(),"Login In Successful",Toast.LENGTH_SHORT).show();
                                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                                if(user != null)
                                {uid = user.getUid();}
                                Intent intent = new Intent(getContext(), HomeActivity.class);
                                startActivity(intent);

                            } else {
                                Toast.makeText(getActivity(), "Authentication failed.",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }

}
