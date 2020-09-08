package com.example.test_mqtt;

import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ChatHolder> {

    private List<User> listChat;
    private Context context;

    public ChatAdapter(List<User> listChat, Context context) {
        this.listChat = listChat;
        this.context = context;
    }

    @NonNull
    @Override
    public ChatHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.chat, parent, false);
        ChatHolder chatHolder = new ChatHolder(view);
        return chatHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ChatHolder holder, int position) {
        User user=listChat.get(position);
        if(user.getUser()=="true"){
            holder.tv2.setVisibility(View.VISIBLE);
            holder.tv2.setText(user.getMessage());
        }
        else{
            holder.tv1.setVisibility(View.VISIBLE);
            holder.tv1.setText(user.getMessage());
        }
    }

    @Override
    public int getItemCount() {
        return listChat.size();
    }

    class ChatHolder extends RecyclerView.ViewHolder {
        TextView tv1, tv2;

        public ChatHolder(@NonNull View itemView) {
            super(itemView);
            tv1 = itemView.findViewById(R.id.tv1);
            tv2 = itemView.findViewById(R.id.tv2);
        }
    }

}
