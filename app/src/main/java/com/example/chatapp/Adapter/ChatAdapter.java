package com.example.chatapp.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.chatapp.ModalClass.Chat;
import com.example.chatapp.R;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.List;

import static androidx.constraintlayout.widget.Constraints.TAG;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ViewHolder> {
    private static final int IMAGE_TYPE_RIGHT = 2;
    private static final int IMAGE_TYPE_LEFT = 3;
    private Context context;
    private List<Chat> mchat;
    private String imageUrl;
    String myid;
    String userid;
    public static final int MSG_TYPE_RIGHT=1;
    public static final int MSG_TYPE_LEFT=0;
    FirebaseUser fuser;
    public ChatAdapter(String userid,String myid,List<Chat> mchat, Context context,String imageUrl){
        this.userid = userid;
        this.myid = myid;
        this.mchat = mchat;
        this.context = context;
    this.imageUrl = imageUrl;}
    @NonNull
    @Override
    public ChatAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == MSG_TYPE_RIGHT) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_item_right, parent, false);
            return new ViewHolder(view);
        } else if(viewType == MSG_TYPE_LEFT) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_item_left, parent, false);
            return new ViewHolder(view);
        }
        else if(viewType == IMAGE_TYPE_RIGHT){
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.image_item_right, parent, false);
            return new ViewHolder(view);
        }
        else{
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.image_item_left, parent, false);
            return new ViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull final ChatAdapter.ViewHolder holder, final int position) {
        final Chat u =mchat.get(position);
        if(u.getType().equals("text")){
        holder.show_message.setText(u.getMsg());
        Glide.with(context).load(imageUrl).into(holder.profile_image);}
        else{
            Glide.with(context).load(u.getMsg()).into(holder.image);
        }
        holder.time.setText(u.getTime());
        if(position==mchat.size()-1){
            if(u.isIsseen()){
                holder.seen.setText("Seen");
            }
            else
            {
                holder.seen.setText("Delivered");
            }
        }
        else{
            holder.seen.setVisibility(View.GONE);
        }
        if (getItemViewType(position) == MSG_TYPE_RIGHT){
        holder.mview.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                    new AlertDialog.Builder(context)
                            .setMessage("Do you want to delete this Message?")
                            .setTitle("Delete Message!")
                            .setCancelable(false)
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                  FirebaseDatabase.getInstance().getReference("Chats").child(u.getKey()).removeValue();
                                  notifyItemRemoved(position);
                                }
                            }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    }).show();
                    return true;
                }
            });}
    }

    @Override
    public int getItemCount() {
        return mchat.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView show_message,time,seen;
        ImageView profile_image,image;
        View mview;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            show_message = itemView.findViewById(R.id.show_message);
            profile_image = itemView.findViewById(R.id.profile_image);
            time = itemView.findViewById(R.id.time);
            seen = itemView.findViewById(R.id.seenMessage);
            image = itemView.findViewById(R.id.image);
            mview =itemView;
        }
    }

    @Override
    public int getItemViewType(int position) {
        fuser = FirebaseAuth.getInstance().getCurrentUser();
       if(mchat.get(position).getType().equals("text")){
           if(mchat.get(position).getSender().equals(fuser.getUid())){
               return  MSG_TYPE_RIGHT;
           }
           else{
               return MSG_TYPE_LEFT;
           }
       }
       else{
           if(mchat.get(position).getSender().equals(fuser.getUid())){
               return  IMAGE_TYPE_RIGHT;
           }
           else{
               return IMAGE_TYPE_LEFT;
           }
       }
    }
}
