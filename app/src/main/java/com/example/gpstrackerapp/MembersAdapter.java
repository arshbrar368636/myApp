package com.example.gpstrackerapp;

import android.content.Context;
import android.icu.text.Transliterator;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.common.api.internal.ConnectionCallbacks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class MembersAdapter extends RecyclerView.Adapter<MembersAdapter.MembersViewHolder> {

    ArrayList<CreateUser> namelist;
    Context c;
    MembersAdapter(ArrayList<CreateUser> namelist, Context c)
    {
        this.namelist = namelist;
        this.c= c;
    }

    @Override
    public int getItemCount() {
        return namelist.size();
    }

    @NonNull
    @Override
    public MembersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View  v= LayoutInflater.from(parent.getContext()).inflate(R.layout.card_layout,parent,false);
        MembersViewHolder membersViewHolder= new MembersViewHolder(v,c,namelist);
        return membersViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MembersViewHolder holder, int position) {

        CreateUser createUserObj=  namelist.get(position);
        holder.name_text.setText(createUserObj.name);
        Picasso.get().load(createUserObj.imageUrl).placeholder(R.drawable.personnew).into(holder.circleImageView);

    }

    public static class MembersViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
    {
        TextView name_text;
        CircleImageView circleImageView;
        Context c;
        ArrayList<CreateUser> nameArrayList;
        FirebaseAuth auth;
        FirebaseUser user;

        public MembersViewHolder(@NonNull View itemView, Context c, ArrayList<CreateUser> nameArrayList) {
            super(itemView);
            this.c = c;
            this.nameArrayList = nameArrayList;

            itemView.setOnClickListener(this);
            auth=FirebaseAuth.getInstance();
            user=auth.getCurrentUser();

            name_text = itemView.findViewById(R.id.text_title);
            circleImageView = itemView.findViewById(R.id.i11);



        }

        @Override
        public void onClick(View v) {
            Toast.makeText(c, "You have clicked this user", Toast.LENGTH_SHORT).show();
        }
    }
}
