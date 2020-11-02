package com.mh.jwaer.familygo.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.mh.jwaer.familygo.R;
import com.mh.jwaer.familygo.data.models.CircleMember;
import com.mh.jwaer.familygo.data.network.RetrofitClient;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MembersAdapter extends RecyclerView.Adapter<MembersAdapter.UsersHolder> {

    private List<CircleMember> usersList;
    private OnItemClickListener mListener;
    private Context context;

    public interface OnItemClickListener {
        void onItemClicked(int position);
    }


    public MembersAdapter(OnItemClickListener listener, Context context) {
        this.mListener = listener;
        this.context = context;
    }

    public void setUsersList(List<CircleMember> usersList) {
        this.usersList = usersList;
        notifyDataSetChanged();
    }

    public static class UsersHolder extends RecyclerView.ViewHolder {

        CircleImageView userImageView;
        TextView usernameTextView;
        TextView userStatus;

        public UsersHolder(@NonNull View itemView, final OnItemClickListener listener) {
            super(itemView);
            userImageView = itemView.findViewById(R.id.memberRecycler_userImage);
            usernameTextView = itemView.findViewById(R.id.memberRecycler_userName);
            userStatus = itemView.findViewById(R.id.memberRecycler_userState);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            listener.onItemClicked(position);
                        }
                    }
                }
            });
        }
    }

    @NonNull
    @Override
    public UsersHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View row = LayoutInflater.from(parent.getContext()).inflate(R.layout.member_recycler_item,
                parent, false);
        return new UsersHolder(row, mListener);
    }

    @Override
    public void onBindViewHolder(@NonNull UsersHolder holder, int position) {
        CircleMember model = usersList.get(position);
        holder.usernameTextView.setText(model.getName());
        if (model.isAdmin())
            holder.userStatus.setText(R.string.admin);
        else
            holder.userStatus.setText(R.string.user);

        if (!model.getPhotoUrl().equals("N/A"))
            try {
                String modelPhotoUrl = model.getPhotoUrl().replace("\\", "/");
                String photoUrl = RetrofitClient.BASE_URL + modelPhotoUrl;
                Glide.with(context)
                        .load(photoUrl)
//                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .thumbnail(0.1f)
                        .centerInside()
                        .placeholder(R.drawable.add_profile_image)
                        .into(holder.userImageView);
            } catch (NullPointerException e) {
                e.printStackTrace();
            }

    }

    @Override
    public int getItemCount() {
        return usersList.size();
    }
}