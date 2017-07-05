package com.codepath.apps.restclienttemplate;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.codepath.apps.restclienttemplate.models.Tweet;

public class DetailActivity extends AppCompatActivity {
    public Tweet tweet;
    public ImageView ivProfileImage;
    public TextView tvUserName;
    public TextView tvScreenName;
    public TextView tvBody;
    public TextView tvTimeStamp;
    public ImageButton ibReply;
    public ImageButton ibFav;
    public ImageButton ibRetweet;

    public TwitterClient client;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        tweet = getIntent().getParcelableExtra("tweet");
        ivProfileImage = (ImageView)findViewById(R.id.ivProfileImage);
        tvUserName = (TextView) findViewById(R.id.tvUserName);
        tvScreenName = (TextView) findViewById(R.id.tvScreenName);
        tvBody = (TextView) findViewById(R.id.tvBody);
        tvTimeStamp = (TextView) findViewById(R.id.tvRelativeDate);
        ibReply = (ImageButton) findViewById(R.id.ibReply);
        ibFav = (ImageButton) findViewById(R.id.ibFav);
        ibRetweet = (ImageButton) findViewById(R.id.ibRetweet);

        client = TwitterApp.getRestClient();

        tvUserName.setText(tweet.user.name);
        tvScreenName.setText("@"+tweet.user.screenName);
        tvBody.setText(tweet.body);
        tvTimeStamp.setText(TweetAdapter.getRelativeTimeAgo(tweet.createdAt));
        Glide.with(this).load(tweet.user.profileImageUrl).into(this.ivProfileImage);

    }
}
