package com.ashwinsaxena.newsapplication;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class ViewPagerAdapter extends RecyclerView.Adapter<ViewPagerAdapter.ViewHolder> {

    private final ArrayList<DataModel> newsList;
    private final Context context;

    public ViewPagerAdapter(ArrayList<DataModel> newsList, Context context) {
        this.newsList = newsList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_main, parent,
                false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        DataModel dataModel = newsList.get(position);
        holder.mainHeading.setText(dataModel.getTitle());
        holder.author.setText(dataModel.getAuthor());
        holder.publishedAt.setText(dataModel.getPublishedAt());
        holder.content.setText(dataModel.getContent());
        holder.mainHeading.setText(dataModel.getTitle());
        String url = dataModel.getUrlToImage();
        if (url != null) {
            Glide.with(context).load(url).centerCrop().into(holder.imageView);
        }



    }

    @Override
    public int getItemCount() {
        return newsList.size();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView mainHeading, publishedAt, author, content;
        private final ImageView imageView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            author = itemView.findViewById(R.id.author);
            publishedAt = itemView.findViewById(R.id.pub_date);
            content = itemView.findViewById(R.id.content);
            mainHeading = itemView.findViewById(R.id.main_heading);
            imageView = itemView.findViewById(R.id.img_view);
        }
    }
}
