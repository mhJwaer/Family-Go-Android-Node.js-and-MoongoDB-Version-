package com.mh.jwaer.familygo.adapters;

import android.content.Context;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.mh.jwaer.familygo.R;
import com.mh.jwaer.familygo.util.CONSTANTS;
import com.mh.jwaer.familygo.util.Converters;
import com.xwray.groupie.GroupieViewHolder;
import com.xwray.groupie.Item;

import de.hdodenhof.circleimageview.CircleImageView;

public class ReceiveMessageItem extends Item {
    private String messageBody;
    private String senderName;
    private String senderPhotoUrl;
    private long createdAt;
    private Context context;

    public ReceiveMessageItem(String messageBody, String senderName, String senderPhotoUrl, long createdAt, Context context){
        this.messageBody = messageBody;
        this.senderName = senderName;
        this.senderPhotoUrl = senderPhotoUrl;
        this.createdAt = createdAt;
        this.context = context;
    }


    @Override
    public void bind(@NonNull GroupieViewHolder viewHolder, int position) {
        TextView messageBodyTv = viewHolder.itemView.findViewById(R.id.text_message_body);
        TextView messageTime = viewHolder.itemView.findViewById(R.id.text_message_time);
        TextView senderNameTv = viewHolder.itemView.findViewById(R.id.text_message_name);
        CircleImageView senderImage = viewHolder.itemView.findViewById(R.id.image_message_profile);


        messageBodyTv.setText(messageBody);
        senderNameTv.setText(senderName);

        messageTime.setText(Converters.chatUnixToDateConverter(createdAt));

        if (senderPhotoUrl.equals(CONSTANTS.NULL_IMAGE)) return;
        try {
            Glide.with(context)
                    .load(senderPhotoUrl)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .centerCrop()
                    .into(senderImage);
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public int getLayout() {
        return R.layout.item_message_received;
    }
}
