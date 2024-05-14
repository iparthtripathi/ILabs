package com.pratik.iiits.notes.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.FirebaseDatabase;
import com.pratik.iiits.Models.UserModel;
import com.pratik.iiits.R;
import com.pratik.iiits.chatapp.ChatAppHome;
import com.pratik.iiits.chatapp.ChatScreen;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserlistAdapter extends RecyclerView.Adapter <UserlistAdapter.ViewHolder>{
    Context homeactivity;
    ArrayList<UserModel> userModelArrayList;
    public UserlistAdapter(@NotNull ChatAppHome chatAppHome, @NotNull ArrayList<UserModel> usersArrayList) {
        this.homeactivity = chatAppHome;
        this.userModelArrayList = usersArrayList;

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(homeactivity).inflate(R.layout.item_user_row,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        UserModel userModel = userModelArrayList.get(position);

        holder.user_name.setText(userModel.getName());
        holder.user_status.setText(userModel.getStatus());
        Picasso.get().load(userModel.getImageUri()).into(holder.user_profile);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(homeactivity, ChatScreen.class);
                intent.putExtra("name",userModel.getName());
                intent.putExtra("ReciverImage",userModel.getImageUri());
                intent.putExtra("uid",userModel.getUid());
                homeactivity.startActivity(intent);


            }
        });


    }

    @Override
    public int getItemCount() {
        return userModelArrayList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        CircleImageView user_profile;
        TextView user_name,user_status;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);


            user_profile = itemView.findViewById(R.id.users_image);
            user_name = itemView.findViewById(R.id.users_name);
            user_status = itemView.findViewById(R.id.users_status);

        }
    }
}
