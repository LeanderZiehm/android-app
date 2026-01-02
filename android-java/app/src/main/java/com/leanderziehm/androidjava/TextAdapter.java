package com.leanderziehm.androidjava;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class TextAdapter extends RecyclerView.Adapter<TextAdapter.TextViewHolder> {

    private List<String> data;

    public TextAdapter(List<String> data) {
        this.data = data;
    }

    public void updateData(List<String> newData) {
        this.data = newData;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public TextViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(android.R.layout.simple_list_item_1, parent, false);
        return new TextViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TextViewHolder holder, int position) {
        holder.textView.setText(data.get(position));
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    static class TextViewHolder extends RecyclerView.ViewHolder {
        TextView textView;

        TextViewHolder(View itemView) {
            super(itemView);
            textView = itemView.findViewById(android.R.id.text1);
        }
    }
}
