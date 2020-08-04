package com.example.chatapp.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.chatapp.Activity.ChatActivity;
import com.example.chatapp.ModalClass.User;
import com.example.chatapp.R;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class NewUserAdapter extends RecyclerView.Adapter<NewUserAdapter.ImageViewHolder> {
    private List<User> auser;
    private Context context;
    public NewUserAdapter(List<User> auser, Context context){
        this.auser = auser;
        this.context = context; }
    @NonNull
    @Override
    public NewUserAdapter.ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.note_item1,parent,false);
        return new NewUserAdapter.ImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NewUserAdapter.ImageViewHolder holder, int position) {
         User u =auser.get(position);
        holder.myTextView.setText(u.getName());
        Glide.with(context).load(u.getImageUrl()).into(holder.myImageView);
        holder.mview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i= new Intent(context, ChatActivity.class);
                i.putExtra("UID",u.getUid());
                i.putExtra("Name",u.getName());
                i.putExtra("ImageUrl",u.getImageUrl());
                i.putExtra("status",u.getStatus());
                context.startActivity(i);
            }
        });
    }

    @Override
    public int getItemCount() {
        return auser.size();
    }
    static class ImageViewHolder extends RecyclerView.ViewHolder {
        TextView myTextView;
        CircleImageView myImageView;
        View mview;
        ImageViewHolder(@NonNull View itemView) {
            super(itemView);
            myTextView = itemView.findViewById(R.id.username);
            myImageView = itemView.findViewById(R.id.userimage);
            mview =itemView;
        }
    }
}
