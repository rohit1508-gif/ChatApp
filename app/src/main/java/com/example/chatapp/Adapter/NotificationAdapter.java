package com.example.chatapp.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.chatapp.ModalClass.User;
import com.example.chatapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ConcurrentModificationException;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ImageViewHolder> {
    List<User> muser;
    Context ctx;
    FirebaseUser fuser = FirebaseAuth.getInstance().getCurrentUser();
    public  NotificationAdapter(List<User> muser, Context ctx){
        this.muser = muser;
        this.ctx = ctx; }
    @NonNull
    @Override
    public NotificationAdapter.ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.note_item3,parent,false);
        return new NotificationAdapter.ImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NotificationAdapter.ImageViewHolder holder, int position) {
        User u = muser.get(position);
        holder.myTextView.setText(u.getName());
        Glide.with(ctx).load(u.getImageUrl()).into(holder.myImageView);
        holder.myButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseDatabase.getInstance().getReference("Users").child(fuser.getUid()).child("Friends").child(u.getUid()).setValue(u.getUid());
                FirebaseDatabase.getInstance().getReference("Users").child(u.getUid()).child("Friends").child(fuser.getUid()).setValue(fuser.getUid());
                FirebaseDatabase.getInstance().getReference("Users").child(fuser.getUid()).child("Request").child(u.getUid()).removeValue();
            }
        });
        holder.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseDatabase.getInstance().getReference("Users").child(fuser.getUid()).child("Request").child(u.getUid()).removeValue();
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
        ImageButton myButton;
        ImageButton button;
        View mview;
        ImageViewHolder(@NonNull View itemView) {
            super(itemView);
            myTextView = itemView.findViewById(R.id.requestName);
            myImageView = itemView.findViewById(R.id.requestImage);
            myButton = itemView.findViewById(R.id.acceptButton);
            button = itemView.findViewById(R.id.rejectButton);
            mview =itemView;
        }
    }
}
