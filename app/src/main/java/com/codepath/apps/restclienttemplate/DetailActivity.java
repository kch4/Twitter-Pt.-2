package com.codepath.apps.restclienttemplate;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.codepath.apps.restclienttemplate.models.Tweet;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class DetailActivity extends AppCompatActivity{
    public ImageView ivProfileImage;
    public TextView tvUserName;
    public TextView tvScreenName;
    public TextView tvBody;
    public TextView tvTimeStamp;
    public TextView tvFavCt;
    public TextView tvRetweetCt;
    public ImageButton ibReply;
    public ImageButton ibFav;
    public ImageButton ibRetweet;
    TwitterClient client;
//    public Integer favoriteCt;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        final Tweet tweet;

        tweet = getIntent().getParcelableExtra("tweet");
        ivProfileImage = (ImageView)findViewById(R.id.ivProfileImage);
        tvUserName = (TextView) findViewById(R.id.tvUserName);
        tvScreenName = (TextView) findViewById(R.id.tvScreenName);
        tvBody = (TextView) findViewById(R.id.tvBody);
        tvTimeStamp = (TextView) findViewById(R.id.tvRelativeDate);
        ibReply = (ImageButton) findViewById(R.id.ibReply);
        ibFav = (ImageButton) findViewById(R.id.ibFav);
        ibRetweet = (ImageButton) findViewById(R.id.ibRetweet);
        tvFavCt = (TextView) findViewById(R.id.tvFavCt);
        tvRetweetCt = (TextView) findViewById(R.id.tvRetweetCt);

        client = TwitterApp.getRestClient();

        tvUserName.setText(tweet.user.name);
        tvScreenName.setText("@"+tweet.user.screenName);
        tvBody.setText(tweet.body);
        tvTimeStamp.setText("Posted "+TweetAdapter.getRelativeTimeAgo(tweet.createdAt));
        tvFavCt.setText(tweet.favoritedCt+" Likes");
        tvRetweetCt.setText(tweet.retweetCt+" Retweets");

        // Changes color if favorited/unfavorited
        if (tweet.favorited){
            ibFav.setPressed(true);
            ibFav.setColorFilter(ContextCompat.getColor(DetailActivity.this,R.color.medium_red));
        }
        else{
            ibFav.setPressed(false);
            ibFav.setColorFilter(ContextCompat.getColor(DetailActivity.this,R.color.light_gray));
        }
        //Changes color if retweeted/unretweeted
        if (tweet.retweeted){
            ibRetweet.setColorFilter(ContextCompat.getColor(DetailActivity.this,R.color.medium_green));
        }
        else{
            ibRetweet.setColorFilter(ContextCompat.getColor(DetailActivity.this,R.color.light_gray));
        }

        Glide.with(this).load(tweet.user.profileImageUrl).into(this.ivProfileImage);

        ivProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(DetailActivity.this, ProfileActivity.class);
                i.putExtra("screen_name", tweet.user.screenName);
                i.putExtra("user_ID", String.valueOf(tweet.user.uid));
                i.putExtra("is_me", false);
                i.putExtra("tweet", tweet);
                ((Activity) DetailActivity.this).startActivity(i);
            }
        });
        ibReply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(DetailActivity.this, ComposeActivity.class);
                i.putExtra("replying", true);
                i.putExtra("currentTweet", tweet);
                ((Activity) DetailActivity.this).startActivity(i);
            }
        });
        ibFav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!tweet.favorited){
                    tweet.favorited = true;
                    client.favoriteTweet(tweet.uid, new JsonHttpResponseHandler(){
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                            ibFav.setColorFilter(ContextCompat.getColor(DetailActivity.this,R.color.medium_red));
                            ibFav.setPressed(true);
                            tvFavCt.setText(tweet.favoritedCt+1+" Likes");
                            tweet.favoritedCt += 1;
                            Toast.makeText(DetailActivity.this, "favorited", Toast.LENGTH_SHORT).show();
                        }
                    });

                }
                if (tweet.favorited){
                    tweet.favorited = false;
                    client.unfavoriteTweet(tweet.uid, new JsonHttpResponseHandler(){
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                            ibFav.setColorFilter(ContextCompat.getColor(DetailActivity.this,R.color.light_gray));
                            ibFav.setPressed(false);
                            tvFavCt.setText(tweet.favoritedCt-1+" Likes");
                            tweet.favoritedCt -= 1;
                            Toast.makeText(DetailActivity.this, "unfavorited", Toast.LENGTH_SHORT).show();
                        }
                        @Override
                        public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                            Log.d("TwitterClient", errorResponse.toString());
                        }
                    });
                }
            }
        });
        ibRetweet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!tweet.retweeted){
                    client.retweet(tweet.uid, new JsonHttpResponseHandler(){
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                            tweet.retweeted = true;
                            ibRetweet.setColorFilter(ContextCompat.getColor(DetailActivity.this,R.color.medium_green));
                            tvRetweetCt.setText(tweet.retweetCt+1+" Retweets");
                            tweet.retweetCt += 1;
                            Toast.makeText(DetailActivity.this, "retweeted", Toast.LENGTH_SHORT).show();
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
                            ibRetweet.setColorFilter(ContextCompat.getColor(DetailActivity.this,R.color.light_gray));
                            tvRetweetCt.setText(tweet.retweetCt-1+" Retweets");
                            tweet.retweetCt -= 1;
                            Toast.makeText(DetailActivity.this, "unretweeted", Toast.LENGTH_SHORT).show();
                        }
                        @Override
                        public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                            Log.d("TwitterClient", errorResponse.toString());
                        }
                    });
                }
            }
        });

        ibReply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(DetailActivity.this, ComposeActivity.class);
                i.putExtra("replying", true);
                i.putExtra("currentTweet", tweet);
                ((Activity) DetailActivity.this).startActivity(i);
            }
        });
    }

}
