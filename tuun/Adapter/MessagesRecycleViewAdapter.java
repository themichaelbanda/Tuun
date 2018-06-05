package com.penguinsonabeach.tuun.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.penguinsonabeach.tuun.Object.ChatMessage;
import com.penguinsonabeach.tuun.R;

import java.util.ArrayList;

public class MessagesRecycleViewAdapter extends RecyclerView.Adapter<MessagesRecycleViewAdapter.MessageViewHolder> {

    private final ArrayList<ChatMessage> messages;
    private final Context mContext;
    private CustomClickListener onClick;

    public interface CustomClickListener {
        void onMessageClicked(int position);
    }

    public MessagesRecycleViewAdapter(ArrayList<ChatMessage> messages, Context mContext) {
        this.messages = messages;
        this.mContext = mContext;
    }

    @Override
    public MessageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.messages_card, parent, false);
        return new MessageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final MessageViewHolder holder, int position) {
        ChatMessage currentMessage = messages.get(holder.getAdapterPosition());
        String currentPhotoUrl = currentMessage.getMessagePhoto();
        holder.messageTextView.setText(currentMessage.getMessageText());
        holder.nameTextView.setText(currentMessage.getMessageUser());
        holder.messagesDate.setText(currentMessage.getMessageDate());
        holder.messageRead.setText(currentMessage.getMessageRead());

        if(currentPhotoUrl != null){
            Glide.with(mContext)
                    .load(currentPhotoUrl.toString())
                    .apply(RequestOptions
                            .diskCacheStrategyOf(DiskCacheStrategy.ALL)
                            .apply(RequestOptions.circleCropTransform())
                            .override(250, 250))
                    .into(holder.messagesUserThumbnail);}

        holder.itemView.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v){
                onClick.onMessageClicked(holder.getAdapterPosition());
            }
        });
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    public static class MessageViewHolder extends RecyclerView.ViewHolder {
        public TextView nameTextView, messageTextView, messagesDate, messageRead;
        public ImageView messagesUserThumbnail;

        public MessageViewHolder(View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.messagesUserNameTV);
            messageTextView = itemView.findViewById(R.id.messagesLastMessageTV);
            messagesUserThumbnail = itemView.findViewById(R.id.messages_user_thumbnail);
            messagesDate= itemView.findViewById(R.id.messageDateTV);
            messageRead = itemView.findViewById(R.id.messageReadStatusTV);
        }
    }

    public void setOnClick(CustomClickListener onClick)
    {
        this.onClick=onClick;
    }
}

