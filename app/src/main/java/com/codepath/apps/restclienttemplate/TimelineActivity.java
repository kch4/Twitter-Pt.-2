package com.codepath.apps.restclienttemplate;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.codepath.apps.restclienttemplate.models.Tweet;

import fragments.TweetsListFragment;
import fragments.TweetsPagerAdapter;


public class TimelineActivity extends AppCompatActivity implements TweetsListFragment.TweetSelectedListener{
//    private SwipeRefreshLayout swipeContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);

        // get the view pager
        ViewPager vpPager = (ViewPager) findViewById(R.id.viewpager);
        // set the adapter for the pager
        vpPager.setAdapter(new TweetsPagerAdapter(getSupportFragmentManager(),this));
        // setup the TabLayout to use the view pager
        TabLayout tabLayout = (TabLayout) findViewById(R.id.sliding_tabs);
        tabLayout.setupWithViewPager(vpPager);
    }


//        // Lookup the swipe container view
//        swipeContainer = (SwipeRefreshLayout) findViewById(R.id.swipeContainer);
//        // Setup refresh listener which triggers new data loading
//        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
//            @Override
//            public void onRefresh() {
//                // Your code to refresh the list here.
//                // Make sure you call swipeContainer.setRefreshing(false)
//                // once the network request has completed successfully.
//                fetchTimelineAsync(0);
//            }
//        });
//        // Configure the refreshing colors
//        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
//                android.R.color.holo_green_light,
//                android.R.color.holo_orange_light,
//                android.R.color.holo_red_light);



//    }
//    public void fetchTimelineAsync(int page) {
//        // Send the network request to fetch the updated data
//        // `client` here is an instance of Android Async HTTP
//        // getHomeTimeline is an example endpoint.
//        client.getHomeTimeline(new JsonHttpResponseHandler() {
//            @Override
//            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
//                // Remember to CLEAR OUT old items before appending in the new ones
//                tweetAdapter.clear();
//                for (int i = 0; i < response.length(); i++){
//                    Tweet tweet = null;
//                    try{
//                        tweet = Tweet.fromJson(response.getJSONObject(i));
//                        tweets.add(tweet);
//                        tweetAdapter.notifyItemInserted(tweets.size()-1);
//
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
//                }
//                // ...the data has come back, add new items to your adapter...
//                swipeContainer.setRefreshing(false);
//            }
//
//            public void onFailure(Throwable e) {
//                Log.d("DEBUG", "Fetch timeline error: " + e.toString());
//            }
//        });
//    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_timeline, menu);
        return true;
    }

    public void onProfileView(MenuItem item) {
        // launch the profile view
        Intent i = new Intent(this, ProfileActivity.class);
        startActivity(i);
    }
    private final int REQUEST_CODE = 20;
    private final int RESULT_OK = -1;
//
    public void onComposeActivity(MenuItem miCompose) {
        // first parameter is the context, second is the class of the activity to launch
        Intent i = new Intent(TimelineActivity.this, ComposeActivity.class);
        startActivityForResult(i, REQUEST_CODE); // brings up the second activity
    }

//    public void onActivityResult(int requestCode, int resultCode, Intent data){
//        if (resultCode==RESULT_OK && (requestCode == REQUEST_CODE)){
//            Tweet tweet = (Tweet)data.getParcelableExtra("new");
//            tweets.add(0, tweet);
//            tweetAdapter.notifyItemInserted(0);
//            rvTweets.scrollToPosition(0);
//            //Toast.makeText(this, "Hey", Toast.LENGTH_SHORT).show();
//        }
//
//    }


    @Override
    public void onTweetSelected(Tweet tweet) {
        Toast.makeText(this, tweet.body, Toast.LENGTH_SHORT).show();

    }
}
