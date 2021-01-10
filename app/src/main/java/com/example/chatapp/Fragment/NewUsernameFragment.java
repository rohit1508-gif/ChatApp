package com.example.chatapp.Fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chatapp.Adapter.NewUserAdapter;
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
import java.util.Objects;

public class NewUsernameFragment extends Fragment {
    private NewUserAdapter adapter;
    RecyclerView recyclerView;
    private Context ctx;
    String uid;
    FirebaseUser fuser;
    List<User> muser;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_newusername, container, false);
        fuser = FirebaseAuth.getInstance().getCurrentUser();
        recyclerView = view.findViewById(R.id.recycler_view1);
        recyclerView.setHasFixedSize(true);
        muser= new ArrayList<>();
        ctx = getActivity();
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
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
        return view;
    }
}
