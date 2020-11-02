package com.mh.jwaer.familygo.adapters;

import android.widget.TextView;

import androidx.annotation.NonNull;

import com.mh.jwaer.familygo.R;
import com.mh.jwaer.familygo.util.Converters;
import com.xwray.groupie.GroupieViewHolder;
import com.xwray.groupie.Item;


public class SendMessageItem extends Item {
    private String content;
    private Long createdAt;

    public SendMessageItem(String message, Long  createdAt) {
        this.content = message;
        this.createdAt = createdAt;
    }


    @Override
    public void bind(@NonNull GroupieViewHolder viewHolder, int position) {
        TextView messageBodyTv = viewHolder.itemView.findViewById(R.id.text_message_body);
        TextView messageTime = viewHolder.itemView.findViewById(R.id.text_message_time);

        messageBodyTv.setText(content);

        //should cast the time
        messageTime.setText(Converters.chatUnixToDateConverter(createdAt));
    }

    @Override
    public int getLayout() {
        return R.layout.item_message_sent;
    }
}