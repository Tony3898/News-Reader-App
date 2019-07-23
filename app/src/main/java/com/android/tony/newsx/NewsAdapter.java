package com.android.tony.newsx;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.MyViewHolder> {

    private List<NewsDatabase> newsDatabaseList;

    NewsAdapter(List<NewsDatabase> newsDatabaseList) {
        this.newsDatabaseList = newsDatabaseList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.news_layout,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        NewsDatabase newsDatabase = newsDatabaseList.get(position);
        Picasso.get().load(newsDatabase.getUriImage()).into(holder.newsImage);
        holder.newsTitle.setText(newsDatabase.getNewsTitle());
        holder.newsCat.setText(newsDatabase.getNewsCategories());
        holder.newsDT.setText(newsDatabase.getNewsDate());
    }

    @Override
    public int getItemCount() {
        return newsDatabaseList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView newsTitle, newsCat, newsDT;
        ImageView newsImage;

        MyViewHolder(View v) {
            super(v);

            this.newsImage = v.findViewById(R.id.newsImageView);
            this.newsTitle = v.findViewById(R.id.newsTitleTextView);
            this.newsCat = v.findViewById(R.id.newsCatTextView);
            this.newsDT = v.findViewById(R.id.newsDTTextView);
        }
    }
}
