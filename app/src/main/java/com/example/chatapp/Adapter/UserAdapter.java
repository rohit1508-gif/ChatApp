package com.example.chatapp.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.chatapp.Activity.ChatActivity;
import com.example.chatapp.ModalClass.Chat;
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

import de.hdodenhof.circleimageview.CircleImageView;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ImageViewHolder>{
    private List<User> muser;
    private Context context;
    private String uid;
    private String Lastmessage;
    private FirebaseUser fuser  = FirebaseAuth.getInstance().getCurrentUser();
    public UserAdapter(List<User> muser, Context context){
        this.muser = muser;
        this.context = context; }
    @NonNull
    @Override
    public UserAdapter.ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.note_item,parent,false);
        return new ImageViewHolder(view);
    }
    @Override
    public void onBindViewHolder(@NonNull final UserAdapter.ImageViewHolder holder, int position) {
        final User u =muser.get(position);
        Lastmessage = ".%f9e%>?<:1!e|??";
        holder.myTextView.setText(u.getName());
        FirebaseDatabase.getInstance().getReference("Chats").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for(DataSnapshot dataSnapshot:snapshot.getChildren()){
                            Chat chat = dataSnapshot.getValue(Chat.class);
                            if(chat!=null)
                            if(chat.getReceiver().equals(fuser.getUid()) && chat.getSender().equals(u.getUid()) ||
                                    chat.getReceiver().equals(u.getUid()) && chat.getSender().equals(fuser.getUid())){
                        Lastmessage = chat.getMsg();}
                    }
                if(Lastmessage.equals(".%f9e%>?<:1!e|??"))
                    holder.lastMessage.setText("");
                else
                    holder.lastMessage.setText(Lastmessage);
                Lastmessage = ".%f9e%>?<:1!e|??";
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
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
        return muser.size();
    }

    static class ImageViewHolder extends RecyclerView.ViewHolder {
        TextView myTextView,lastMessage;
        CircleImageView myImageView;
        View mview;
        ImageViewHolder(@NonNull View itemView) {
            super(itemView);
            myTextView = itemView.findViewById(R.id.username);
            myImageView = itemView.findViewById(R.id.userimage);
            lastMessage = itemView.findViewById(R.id.lastmessage);
            mview =itemView;
        }
    }
}
