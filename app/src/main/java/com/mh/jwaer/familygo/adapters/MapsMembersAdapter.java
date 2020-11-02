package com.mh.jwaer.familygo.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.signature.ObjectKey;
import com.mh.jwaer.familygo.R;
import com.mh.jwaer.familygo.data.models.CircleMember;
import com.mh.jwaer.familygo.data.network.RetrofitClient;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MapsMembersAdapter  extends RecyclerView.Adapter<MapsMembersAdapter.UsersHolder> {

    private List<CircleMember> usersList;
    private OnItemClickListener mListener;
    private Context context;

    public interface OnItemClickListener {
        void onItemClicked(int position);
    }


    public MapsMembersAdapter(OnItemClickListener listener, Context context) {
        this.mListener = listener;
        this.context = context;
    }

    public void setUsersList(List<CircleMember> usersList) {
        this.usersList = usersList;
        notifyDataSetChanged();
    }

    public static class UsersHolder extends RecyclerView.ViewHolder {

        CircleImageView userImageView;
        CircleImageView greenDotImageView;

        public UsersHolder(@NonNull View itemView, final OnItemClickListener listener) {
            super(itemView);
            userImageView = itemView.findViewById(R.id.maps_recycler_item_image_view);
            greenDotImageView = itemView.findViewById(R.id.green_dot);
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
        View row = LayoutInflater.from(parent.getContext()).inflate(R.layout.maps_fragment_recycler_item,
                parent, false);
        return new UsersHolder(row, mListener);
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void onBindViewHolder(@NonNull UsersHolder holder, int position) {
        CircleMember model = usersList.get(position);
        if (model.isSharing()){
            holder.greenDotImageView.setVisibility(View.VISIBLE);
        }
        if (!model.getPhotoUrl().equals("N/A"))
            try {
                String modelPhotoUrl = model.getPhotoUrl().replace("\\", "/");
                String photoUrl = RetrofitClient.BASE_URL + modelPhotoUrl;
                Log.d("photoUrl", "onBindViewHolder: "+photoUrl);
                Glide.with(context)
                        .load(photoUrl)
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .apply(new RequestOptions().signature(new ObjectKey("signature string")))
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