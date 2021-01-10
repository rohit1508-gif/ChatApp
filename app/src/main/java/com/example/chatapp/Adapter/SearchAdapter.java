package com.example.chatapp.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.chatapp.ModalClass.User;
import com.example.chatapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.ImageViewHolder> {
    List<User> muser;
    Context ctx;
    FirebaseUser fuser = FirebaseAuth.getInstance().getCurrentUser();
    public SearchAdapter(List<User> muser, Context ctx){
        this.muser = muser;
        this.ctx = ctx; }
    @NonNull
    @Override
    public SearchAdapter.ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.note_item2,parent,false);
        return new SearchAdapter.ImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SearchAdapter.ImageViewHolder holder, int position) {
        User u = muser.get(position);
        holder.myTextView.setText(u.getName());
        Glide.with(ctx).load(u.getImageUrl()).into(holder.myImageView);
        FirebaseDatabase.getInstance().getReference("Users").child(fuser.getUid()).child("Friends").child(u.getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    holder.myButton.setEnabled(false);
                    holder.myButton.setText("Friends");
                    }
                else{
                    holder.myButton.setEnabled(true);
                    holder.myButton.setText("Request!");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        holder.myButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(ctx,"Request Sent",Toast.LENGTH_SHORT).show();
              FirebaseDatabase.getInstance().getReference("Users").child(u.getUid()).child("Request").child(fuser.getUid()).setValue(fuser.getUid());
            }
        });
    }

    @Override
    public int getItemCount() {
        return muser.size();
    }
    static class ImageViewHolder extends RecyclerView.ViewHolder {
        TextView myTextView;
        CircleImageView myImageView;
        Button myButton;
        View mview;
        ImageViewHolder(@NonNull View itemView) {
            super(itemView);
            myTextView = itemView.findViewById(R.id.username1);
            myImageView = itemView.findViewById(R.id.userimage2);
            myButton = itemView.findViewById(R.id.requestbutton);
            mview =itemView;
        }
    }
}
