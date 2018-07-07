package com.example.company.dailydoseofnews.network;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.util.Log;
import android.util.TimeUtils;

import com.example.company.dailydoseofnews.News;
import com.example.company.dailydoseofnews.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Time;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

public class NetworkUtils {

    private static final String TAG = "NetworkUtils";
    private static final long ONE_MINUTE = 60000;
    private static Context mContext;

    private NetworkUtils(){
    }

    public static boolean isConnectedToNetwork(Context context){
        ConnectivityManager connectivityManager =
                (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnectedOrConnecting()){
            return true;
        } else {
            return false;
        }
    }

    public static List<News> getNewsData(Context context, String stringUrl){
        mContext = context;
        URL url = createUrl(stringUrl);
        String jsonResponse;
        jsonResponse = makeHttpRequest(url);
        List<News> newsList = extractDataFromJson(jsonResponse);
        return newsList;
    }

    private static URL createUrl(String stringUrl){
        URL url = null;
        try{
            url = new URL(stringUrl);
        } catch (MalformedURLException e){
            Log.e(TAG, "createUrl: ", e);
        }
        return url;
    }

    private static String makeHttpRequest(URL url) {
        String jsonResponse = "";

        if (url == null) {
            return null;
        }

        HttpURLConnection urlConnection;
        InputStream inputStream;

        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            if (urlConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readStream(inputStream);
            }
        } catch (IOException e) {
            Log.e(TAG, "makeHttpRequest: Error ", e);
        }
        return jsonResponse;
    }

    private static String readStream(InputStream inputStream){
        StringBuilder stringBuilder = new StringBuilder();
        try {
            if (inputStream != null) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String line = bufferedReader.readLine();
                while (line != null) {
                    stringBuilder.append(line);
                    line = bufferedReader.readLine();
                }
            }
        }catch (IOException e){
            Log.e(TAG, "readStream: ", e);
        }
        return stringBuilder.toString();
    }

    private static List<News> extractDataFromJson(String newsJson){
        if (TextUtils.isEmpty(newsJson)){
            return null;
        }
        List<News> newsList = new ArrayList<>();

        try{
            JSONObject rootJson = new JSONObject(newsJson);
            JSONObject responseJson = rootJson.getJSONObject("response");
            JSONArray resultJsonArray = responseJson.getJSONArray("results");
            for (int i = 0; i < resultJsonArray.length(); i++){
                // Article Title
                JSONObject currentArticle = resultJsonArray.getJSONObject(i);
                String title = currentArticle.getString("webTitle");
                // Article Author
                JSONArray tagsArray = currentArticle.getJSONArray("tags");
                JSONObject tagsObj = tagsArray.optJSONObject(0);
                String author = getAuthor(tagsObj);
                // Gets Image and if none is available sets it to null to put placeholder Image.
                JSONObject fields = currentArticle.optJSONObject("fields");
                String image;
                if (fields != null){
                    image = fields.optString("thumbnail");
                } else {
                    image = null;
                }
                // Article Date
                String date = currentArticle.getString("webPublicationDate");
                String formattedDate = formatDate(date);
                // Article Web-page URL
                String webUrl = currentArticle.getString("webUrl");
                /* Title, Author, ImageURL, Date, Web-pageURL */
                newsList.add(new News(title, author, image, formattedDate, webUrl));
            }


        } catch (JSONException e){
            Log.e(TAG, "extractDataFromJson: rootJson = = = " + newsJson, e);
        }
        return newsList;
    }

    // Formats author name.
    // Sometimes the authors first and last name are reversed in the JSON.
    private static String getAuthor(JSONObject tagsObj){
        if (tagsObj == null){
            return null;
        }
        String firstName = tagsObj.optString("firstName");
        String lastName = tagsObj.optString("lastName");
        String author = buildString(firstName, lastName);
        return author;
    }

    private static String buildString(String firstName, String lastName){
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(firstName);
        stringBuilder.append(" ");
        stringBuilder.append(lastName);
        return stringBuilder.toString();
    }

    /* Returns a formatted date in seconds, minutes, hours, "a day ago", or a full date. */
    private static String formatDate(String dateString){
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
                return (seconds + " " + mContext.getString(R.string.seconds_ago));
            }
            // 1 minute ago
            else if (articleTimeInMilli < TimeUnit.MINUTES.toMillis(2)){
                long oneMinute = TimeUnit.MILLISECONDS.toMinutes(articleTimeInMilli);
                return (oneMinute + " " + mContext.getString(R.string.minute_ago));
            }
            // Minutes between 2-59
            else if (articleTimeInMilli < TimeUnit.HOURS.toMillis(1)){
                long minutes = TimeUnit.MILLISECONDS.toMinutes(articleTimeInMilli);
                return (minutes + " " + mContext.getString(R.string.minutes_ago));
            }
            // 1 hour ago
            else if (articleTimeInMilli < TimeUnit.HOURS.toMillis(2)){
                long hours = TimeUnit.MILLISECONDS.toHours(articleTimeInMilli);
                return (hours + " " + mContext.getString(R.string.hour_ago));

            }
            // Hours between 2 - 23
            else if (articleTimeInMilli < TimeUnit.DAYS.toMillis(1)){
                long hours = TimeUnit.MILLISECONDS.toHours(articleTimeInMilli);
                return (hours + " " + mContext.getString(R.string.hours_ago));
            }
            // One day ago
            else if (articleTimeInMilli < TimeUnit.DAYS.toMillis(2)){
                String oneDayAgo = mContext.getString(R.string.one_day_ago);
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
}
