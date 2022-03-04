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

public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.ViewHolder> {

    private final ArrayList<DataModel> newsList;
    private final Context context;
    private final ItemClickListener clickListener;
    public NewsAdapter(ArrayList<DataModel> newsList, Context context,
                       ItemClickListener clickListener) {
        this.newsList = newsList;
        this.context = context;
        this.clickListener = clickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.single_news_item, parent, false);
        return new ViewHolder(view, clickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        DataModel dataModel = newsList.get(position);
        holder.title.setText(dataModel.getTitle());
        holder.content.setText(dataModel.getDescription());
        String url = (dataModel.getUrlToImage() == null) ? "" + (R.drawable.image_error) : dataModel.getUrlToImage();
        Glide.with(context).load(url).centerCrop().into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return newsList.size();
    }


    interface ItemClickListener {
        void showDetailNews(int position);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView title;
        private final TextView content;
        private final ImageView imageView;
        public ViewHolder(@NonNull View itemView, ItemClickListener clickListener) {
            super(itemView);
            title = itemView.findViewById(R.id.title);
            content = itemView.findViewById(R.id.desc);
            imageView = itemView.findViewById(R.id.image_view);
            itemView.setOnClickListener(v ->
                    clickListener.showDetailNews(getBindingAdapterPosition()));
        }
    }
}
