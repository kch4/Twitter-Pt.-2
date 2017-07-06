package com.codepath.apps.restclienttemplate;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.codepath.apps.restclienttemplate.models.Tweet;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

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
    public void onBindViewHolder(ViewHolder holder, int position) {
        // get the data according to posi.tion
        final Tweet tweet = mTweets.get(position);

        // populate the views according to this data
        holder.tvUsername.setText(tweet.user.name);
        holder.tvBody.setText(tweet.body);
        holder.tvScreenName.setText("@"+tweet.user.screenName);
        holder.tvRelativeDate.setText(getRelativeTimeAgo(tweet.createdAt));

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
//                i.putExtra("screen_name", tweet.user.screenName);
//                i.putExtra("user_ID", String.valueOf(tweet.user.uid));
                i.putExtra("currentTweet", tweet);
                ((Activity) context).startActivity(i);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mTweets.size();
    }

    // create ViewHolder class

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        public ImageView ivProfileImage;
        public TextView tvUsername;
        public TextView tvBody;
        public TextView tvScreenName;
        public TextView tvRelativeDate;
        public ImageButton ibReply;
        public ImageButton ibFav;
        public ImageButton ibRetweet;

        public ViewHolder(View itemView){
            super(itemView);

            // prform findViewByID lookups

            ivProfileImage = (ImageView) itemView.findViewById(R.id.ivProfileImage);
            tvUsername = (TextView) itemView.findViewById(R.id.tvUserName);
            tvBody = (TextView) itemView.findViewById(R.id.tvBody);
            tvScreenName = (TextView) itemView.findViewById(R.id.tvScreenName);
            tvRelativeDate = (TextView)itemView.findViewById(R.id.tvRelativeDate);
            ibReply = (ImageButton)itemView.findViewById(R.id.ibReply);
            ibFav = (ImageButton)itemView.findViewById(R.id.ibFav);
            ibRetweet = (ImageButton) itemView.findViewById(R.id.ibRetweet);

            ibReply.setOnClickListener(this);
            ibFav.setOnClickListener(this);
            ibRetweet.setOnClickListener(this);

            // handle row click event
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mListener != null){
                    // get the position of row element
                        int position = getAdapterPosition();
                    // fire the listener callback
                        mListener.onItemSelected(v,  position);
                }
            }});

        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            //Button b = (Button) v;
            Intent intent;
//            switch(v.getId()) {
//
//                case R.id.ivProfileImage:
//                    if (position != RecyclerView.NO_POSITION) {
//                        Tweet tweet = mTweets.get(position);
//                        intent = new Intent(v.getContext(), ProfileActivity.class);
//                        intent.putExtra("is_me", false);
//                        intent.putExtra("screen_name", tweet.user.screenName);
//                        //intent.putExtra("")
//                    }
//                    break;
//                case R.id.tvBody:
//                    if (position != RecyclerView.NO_POSITION) {
//                        Tweet tweet = mTweets.get(position);
//                        intent = new Intent(v.getContext(), DetailActivity.class);
//                        intent.putExtra("body", tweet.body);
//                        intent.putExtra("screen_name", tweet.user.screenName);
//                        //intent.putExtra("")
//                    }
//                case R.id.ibReply:
//                    //compose activity
//                    Log.d("Sending data", "Reply"); // perform
////                    intent = new Intent(context, ComposeActivity.class);
//                    // action
//                    break;
////                case R.id.YOUR_SECOND_BUTTON:
////                    // Do something
////                    break;
//            }
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

