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
            ibFav.setColorFilter(ContextCompat.getColor(DetailActivity.this,R.color.medium_red));
        }
        else{
            ibFav.setColorFilter(ContextCompat.getColor(DetailActivity.this,R.color.light_gray));
        }

        Glide.with(this).load(tweet.user.profileImageUrl).into(this.ivProfileImage);

        // Click to favorite
        ibFav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!tweet.favorited){
                    tweet.favorited = true;
                    client.favoriteTweet(tweet.uid, new JsonHttpResponseHandler(){
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                            ibFav.setColorFilter(ContextCompat.getColor(DetailActivity.this,R.color.medium_red));
                            tvFavCt.setText(tweet.favoritedCt+1+" Likes");
                            tweet.favoritedCt += 1;

                            Toast.makeText(DetailActivity.this, "favorited", Toast.LENGTH_LONG).show();
                        }
                    });

                }
                if (tweet.favorited){
                    tweet.favorited = false;
                    client.unfavoriteTweet(tweet.uid, new JsonHttpResponseHandler(){
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                            ibFav.setColorFilter(ContextCompat.getColor(DetailActivity.this,R.color.light_gray));
                            tvFavCt.setText(tweet.favoritedCt-1+" Likes");
                            tweet.favoritedCt -= 1;
                            Toast.makeText(DetailActivity.this, "unfavorited", Toast.LENGTH_LONG).show();
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
