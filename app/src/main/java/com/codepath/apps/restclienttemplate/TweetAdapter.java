package com.codepath.apps.restclienttemplate;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.codepath.apps.restclienttemplate.models.Tweet;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import cz.msebera.android.httpclient.Header;

/**
 * Created by kathu228 on 6/26/17.
 */

public class TweetAdapter extends RecyclerView.Adapter<TweetAdapter.ViewHolder> {
    private List<Tweet> mTweets;
    Context context;
    private TweetAdapterListener mListener;

    // define an interface required by the ViewHolder
    public interface TweetAdapterListener {
        public void onItemSelected(View view, int position);
    }
    // pass in the Tweets array in the constructor
    public TweetAdapter(List<Tweet> tweets, TweetAdapterListener listener){
        mTweets = tweets;
        mListener = listener;
    }
    TwitterClient client = TwitterApp.getRestClient();
    // for each row, inflate the layout and cache references into ViewHolder
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View tweetView = inflater.inflate(R.layout.item_tweet,parent,false);
        ViewHolder viewholder = new ViewHolder(tweetView);
        return viewholder;
    }



    // bind the values based on the position of the element

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        // get the data according to posi.tion
        final Tweet tweet = mTweets.get(position);

        // populate the views according to this data
        holder.tvUsername.setText(tweet.user.name);
        holder.tvBody.setText(tweet.body);
        holder.tvScreenName.setText("@"+tweet.user.screenName);
        holder.tvRelativeDate.setText(getRelativeTimeAgo(tweet.createdAt));
        holder.tvFavCt.setText(tweet.favoritedCt+"");
        holder.tvRetweetCt.setText(tweet.retweetCt+"");
        if (tweet.favorited){
            holder.ibFav.setPressed(true);
            holder.ibFav.setColorFilter(ContextCompat.getColor(context,R.color.medium_red));
        }
        else{
            holder.ibFav.setPressed(false);
            holder.ibFav.setColorFilter(ContextCompat.getColor(context,R.color.light_gray));
        }
        if (tweet.retweeted){
            holder.ibRetweet.setColorFilter(ContextCompat.getColor(context,R.color.medium_green));
        }
        else{
            holder.ibRetweet.setColorFilter(ContextCompat.getColor(context,R.color.light_gray));
        }

        Glide.with(context).load(tweet.user.profileImageUrl).into(holder.ivProfileImage);

        holder.ivProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(context, ProfileActivity.class);
                i.putExtra("screen_name", tweet.user.screenName);
                i.putExtra("user_ID", String.valueOf(tweet.user.uid));
                i.putExtra("is_me", false);
                i.putExtra("tweet", tweet);
                ((Activity) context).startActivity(i);
            }
        });
        holder.ibReply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(context, ComposeActivity.class);
                i.putExtra("replying", true);
                i.putExtra("currentTweet", tweet);
                ((Activity) context).startActivity(i);
            }
        });
        holder.tvBody.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, DetailActivity.class);
                intent.putExtra("tweet", tweet);
                intent.putExtra("favoriteCt", tweet.favoritedCt);
                ((Activity)context).startActivity(intent);

            }
        });
        holder.ibFav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!tweet.favorited){
                    tweet.favorited = true;
                    client.favoriteTweet(tweet.uid, new JsonHttpResponseHandler(){
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                            holder.ibFav.setPressed(true);
                            holder.ibFav.setColorFilter(ContextCompat.getColor(context,R.color.medium_red));
                            holder.tvFavCt.setText(tweet.favoritedCt+1+"");
                            tweet.favoritedCt += 1;
                            Toast.makeText(context, "favorited", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                            Log.d("TwitterClient", errorResponse.toString());
                        }
                    });

                }
                if (tweet.favorited){
                    tweet.favorited = false;
                    client.unfavoriteTweet(tweet.uid, new JsonHttpResponseHandler(){
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                            holder.ibFav.setPressed(false);
                            holder.ibFav.setColorFilter(ContextCompat.getColor(context,R.color.light_gray));
                            holder.tvFavCt.setText(tweet.favoritedCt-1+"");
                            tweet.favoritedCt -= 1;
                            Toast.makeText(context, "unfavorited", Toast.LENGTH_SHORT).show();
                        }
                        @Override
                        public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                            Log.d("TwitterClient", errorResponse.toString());
                        }
                    });
                }
            }
        });
        holder.ibRetweet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!tweet.retweeted){
                    client.retweet(tweet.uid, new JsonHttpResponseHandler(){
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                            tweet.retweeted = true;
                            holder.ibRetweet.setColorFilter(ContextCompat.getColor(context,R.color.medium_green));
                            holder.tvRetweetCt.setText(tweet.retweetCt+1+"");
                            tweet.retweetCt += 1;
                            Toast.makeText(context, "retweeted", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                            Log.d("TwitterClient", errorResponse.toString());
                        }
                    });

                }
                if (tweet.retweeted){
                    client.unretweet(tweet.uid, new JsonHttpResponseHandler(){
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                            tweet.retweeted = false;
                            holder.ibRetweet.setColorFilter(ContextCompat.getColor(context,R.color.light_gray));
                            holder.tvRetweetCt.setText(tweet.retweetCt-1+"");
                            tweet.retweetCt -= 1;
                            Toast.makeText(context, "unretweeted", Toast.LENGTH_SHORT).show();
                        }
                        @Override
                        public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                            Log.d("TwitterClient", errorResponse.toString());
                        }
                    });
                }
            }
        });
    }


    @Override
    public int getItemCount() {
        return mTweets.size();
    }

    // create ViewHolder class

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView ivProfileImage;
        public TextView tvUsername;
        public TextView tvBody;
        public TextView tvScreenName;
        public TextView tvRelativeDate;
        public ImageButton ibReply;
        public ImageButton ibFav;
        public ImageButton ibRetweet;
        public TextView tvFavCt;
        public TextView tvRetweetCt;

        public ViewHolder(View itemView) {
            super(itemView);
            ivProfileImage = (ImageView) itemView.findViewById(R.id.ivProfileImage);
            tvUsername = (TextView) itemView.findViewById(R.id.tvUserName);
            tvBody = (TextView) itemView.findViewById(R.id.tvBody);
            tvRelativeDate = (TextView) itemView.findViewById(R.id.tvRelativeDate);
            tvScreenName = (TextView) itemView.findViewById(R.id.tvScreenName);
            ibReply = (ImageButton) itemView.findViewById(R.id.ibReply);
            ibRetweet = (ImageButton) itemView.findViewById(R.id.ibRetweet);
            ibFav = (ImageButton) itemView.findViewById(R.id.ibFav);
            tvFavCt = (TextView) itemView.findViewById(R.id.tvFavCt);
            tvRetweetCt = (TextView) itemView.findViewById(R.id.tvRetweetCt);

            //handle row click event
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mListener != null) {
                        //get the position of row element
                        int position = getAdapterPosition();
                        // fire the listener callback
                        mListener.onItemSelected(view, position);
                    }
                }
            });
        }
    }
    public void clear() {
        mTweets.clear();
        notifyDataSetChanged();
    }

    // Add a list of items -- change to type used
    public void addAll(List<Tweet> list) {
        mTweets.addAll(list);
        notifyDataSetChanged();
    }

    public static String getRelativeTimeAgo(String rawJsonDate) {
        String twitterFormat = "EEE MMM dd HH:mm:ss ZZZZZ yyyy";
        SimpleDateFormat sf = new SimpleDateFormat(twitterFormat, Locale.ENGLISH);
        sf.setLenient(true);

        String relativeDate = "";
        try {
            long dateMillis = sf.parse(rawJsonDate).getTime();
            relativeDate = DateUtils.getRelativeTimeSpanString(dateMillis,
                    System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS).toString();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return relativeDate;
    }



}

