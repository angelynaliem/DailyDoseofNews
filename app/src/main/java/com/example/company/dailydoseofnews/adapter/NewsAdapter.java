package com.example.company.dailydoseofnews.adapter;

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

import com.example.company.dailydoseofnews.News;
import com.example.company.dailydoseofnews.R;
import com.example.company.dailydoseofnews.interfaces.NewsInterface;
import com.squareup.picasso.Picasso;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.ViewHolder> {

    private static final long ONE_MINUTE = 60000;
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
        // Title
        holder.articleTitle.setText(currentNews.getNewsTitle());
        // Date
        String formattedDate = formatDate(currentNews.getNewsDate());
        holder.articleDate.setText(formattedDate);
        // Author
        if (currentNews.getNewsAuthor() != null){
            String authorString =
                    context.getString(R.string.author) + " " + currentNews.getNewsAuthor();
            holder.articleAuthor.setText(authorString);
        } else {
            holder.articleAuthor.setText(R.string.author_unknown);
        }
        // Image
        if (currentNews.getNewsImageUrl() != null) {
            Picasso.get().load(currentNews.getNewsImageUrl()).into(holder.articleImage);
        } else {
            Picasso.get().load(R.drawable.no_image_available).into(holder.articleImage);
        }
    }

    /* Returns a formatted date in seconds, minutes, hours, "a day ago", or a full date. */
    private String formatDate(String dateString){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss'Z'", Locale.getDefault());
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
        try{
            Date articleDate = simpleDateFormat.parse(dateString);
            Calendar calendar = Calendar.getInstance();
            long currentTimeInMilli = calendar.getTimeInMillis();
            long articleTimeInMilli = currentTimeInMilli - articleDate.getTime();
            // Seconds ago
            if (articleTimeInMilli < ONE_MINUTE){
                long seconds = TimeUnit.MILLISECONDS.toSeconds(articleTimeInMilli);
                return (seconds + " " + context.getString(R.string.seconds_ago));
            }
            // 1 minute ago
            else if (articleTimeInMilli < TimeUnit.MINUTES.toMillis(2)){
                long oneMinute = TimeUnit.MILLISECONDS.toMinutes(articleTimeInMilli);
                return (oneMinute + " " + context.getString(R.string.minute_ago));
            }
            // Minutes between 2-59
            else if (articleTimeInMilli < TimeUnit.HOURS.toMillis(1)){
                long minutes = TimeUnit.MILLISECONDS.toMinutes(articleTimeInMilli);
                return (minutes + " " + context.getString(R.string.minutes_ago));
            }
            // 1 hour ago
            else if (articleTimeInMilli < TimeUnit.HOURS.toMillis(2)){
                long hours = TimeUnit.MILLISECONDS.toHours(articleTimeInMilli);
                return (hours + " " + context.getString(R.string.hour_ago));

            }
            // Hours between 2 - 23
            else if (articleTimeInMilli < TimeUnit.DAYS.toMillis(1)){
                long hours = TimeUnit.MILLISECONDS.toHours(articleTimeInMilli);
                return (hours + " " + context.getString(R.string.hours_ago));
            }
            // One day ago
            else if (articleTimeInMilli < TimeUnit.DAYS.toMillis(2)){
                String oneDayAgo = context.getString(R.string.one_day_ago);
                return oneDayAgo;
            } else {
                // if > 1 day, show full date
                simpleDateFormat.applyPattern("MMM, dd, yyyy");
                dateString = simpleDateFormat.format(articleDate);
                return dateString;
            }
        } catch (ParseException e){
            return null;
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
