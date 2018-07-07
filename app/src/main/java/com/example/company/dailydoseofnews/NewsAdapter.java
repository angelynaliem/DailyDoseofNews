package com.example.company.dailydoseofnews;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.company.dailydoseofnews.interfaces.NewsInterface;
import com.squareup.picasso.Picasso;
import java.util.List;

public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.ViewHolder> {

    private List<News> newsArrayList;
    private NewsInterface newsInterface;
    private Context context;

    private static final String TAG = "NewsAdapter";

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        private TextView articleTitle;
        private TextView articleAuthor;
        private TextView articleDate;
        private ImageView articleImage;
        private ImageButton shareButton;
        private ImageButton bookmarkButton;

        public ViewHolder(View itemView) {
            super(itemView);
            articleTitle = itemView.findViewById(R.id.news_article_title);
            articleAuthor = itemView.findViewById(R.id.news_article_author);
            articleDate = itemView.findViewById(R.id.news_article_date);
            articleImage = itemView.findViewById(R.id.news_image_view);
            shareButton = itemView.findViewById(R.id.news_share_button);
            bookmarkButton = itemView.findViewById(R.id.news_bookmark);
            shareButton.setOnClickListener(this);
            bookmarkButton.setOnClickListener(this);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            newsInterface.onItemClick(v, getAdapterPosition());
        }
    }

    public NewsAdapter(Context context, List<News> newsArrayList, NewsInterface newsInterface){
        this.context = context;
        this.newsArrayList = newsArrayList;
        this.newsInterface = newsInterface;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View listItems = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_items_layout, parent, false);
        return new ViewHolder(listItems);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        News currentNews = newsArrayList.get(position);
        holder.articleTitle.setText(currentNews.getNewsTitle());
        holder.articleDate.setText(currentNews.getNewsDate());
        if (currentNews.getNewsAuthor() != null){
            String authorString =
                    context.getString(R.string.author) + " " + currentNews.getNewsAuthor();
            holder.articleAuthor.setText(authorString);
        } else {
            holder.articleAuthor.setText(R.string.author_unknown);
        }
        if (currentNews.getNewsImageUrl() != null) {
            Picasso.get().load(currentNews.getNewsImageUrl()).into(holder.articleImage);
        } else {
            Picasso.get().load(R.drawable.no_image_available).into(holder.articleImage);
        }
    }

    /* Under rare circumstances a NullPointerException is thrown. */
    @Override
    public int getItemCount() {
        try {
            return newsArrayList.size();
        } catch (NullPointerException e){
            Log.d(TAG, "getItemCount: ", e);
            return 0;
        }
    }
}
