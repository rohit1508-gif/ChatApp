package com.example.chatapp.Fragment;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chatapp.Adapter.UserAdapter;
import com.example.chatapp.ModalClass.Chat;
import com.example.chatapp.ModalClass.User;
import com.example.chatapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class UsernameFragment extends Fragment {
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
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fargment_username, container, false);
        fuser = FirebaseAuth.getInstance().getCurrentUser();
        if(fuser!=null)
            uid = fuser.getUid();
        databasenote = FirebaseDatabase.getInstance().getReference("Users");
        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        muser= new ArrayList<>();
        ctx = getActivity();
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
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
                                @Override public void onDataChange(@NonNull DataSnapshot snapshot) {
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
        return view;}
}
