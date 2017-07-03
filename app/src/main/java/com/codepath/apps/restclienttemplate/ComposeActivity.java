package com.codepath.apps.restclienttemplate;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.codepath.apps.restclienttemplate.models.Tweet;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

public class ComposeActivity extends AppCompatActivity {
    private TwitterClient client;
    TweetAdapter tweetAdapter;
    ArrayList<Tweet> tweets;
    RecyclerView rvTweets;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compose);
        TextInputLayout text1 = (TextInputLayout) findViewById(R.id.text1);
        text1.getEditText().addTextChangedListener(new CharacterCountErrorWatcher(text1, 1, 140));

        final Button button = (Button) findViewById(R.id.bTweet);
        client = TwitterApp.getRestClient();
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Code here executes on main thread after user presses button
                onSubmit(v);
            }
        });
    }

    public void onSubmit(View v) {
        EditText etTweet = (EditText) findViewById(R.id.etTweet);
        String body = etTweet.getText().toString();

        client.sendTweet(body, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
//                super.onSuccess(statusCode, headers, response);
                Tweet tweet = null;
                try {
                    tweet = Tweet.fromJson(response);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Intent data = new Intent();
                // Pass relevant data back as a result
                data.putExtra("new", tweet);
                //data.putExtra("UID", uid);
//                data.putExtra("code", 200); // ints work too
                // Activity finished ok, return the data
                setResult(RESULT_OK, data); // set result code and bundle data for response
                finish(); // closes the activity, pass data to parent

            }
            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.d("TwitterClient", errorResponse.toString());
            }

        });


    }

}
