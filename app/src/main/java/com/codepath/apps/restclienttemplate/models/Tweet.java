package com.codepath.apps.restclienttemplate.models;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by kathu228 on 6/26/17.
 */
public class Tweet implements Parcelable {
    // attributes
    public String body;
    public long uid; // database id
    public User user;
    public String createdAt;
    public String screen;
    public boolean retweeted;
    public int retweetCt;
    public boolean favorited;
    public int favoritedCt;
//    public boolean replied;
//    public int repliedCt;


    // deserialize JSON
    public static Tweet fromJson(JSONObject jsonObject) throws JSONException{
        Tweet tweet = new Tweet();

        //extract the values from JSON
        tweet.body = jsonObject.getString("text");
        tweet.uid = jsonObject.getLong("id");
//        tweet.screen = jsonObject.getString("screen_name");
        tweet.createdAt = jsonObject.getString("created_at");
        tweet.user = User.fromJSON(jsonObject.getJSONObject("user"));
        tweet.retweeted = jsonObject.getBoolean("retweeted");
        tweet.retweetCt = jsonObject.getInt("retweet_count");
        tweet.favorited = jsonObject.getBoolean("favorited");
        tweet.favoritedCt = jsonObject.getInt("favorite_count");


        return tweet;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.body);
        dest.writeLong(this.uid);
        dest.writeParcelable(this.user, flags);
        dest.writeString(this.createdAt);
        dest.writeString(this.screen);
        dest.writeByte((byte) (this.favorited ? 1 : 0));
        dest.writeInt(this.favoritedCt);
        dest.writeByte((byte) (this.favorited ? 1 : 0));
        dest.writeInt(this.retweetCt);
//        dest.writeByte((byte) (this.replied ? 1 : 0));
//        dest.writeInt(this.repliedCt);
    }

    public Tweet() {
    }

    protected Tweet(Parcel in) {
        this.body = in.readString();
        this.uid = in.readLong();
        this.user = in.readParcelable(User.class.getClassLoader());
        this.createdAt = in.readString();
        this.screen = in.readString();
        this.favorited = in.readByte() != 0;
        this.favoritedCt = in.readInt();
        this.retweeted = in.readByte() != 0;
        this.retweetCt = in.readInt();
    }

    public static final Creator<Tweet> CREATOR = new Parcelable.Creator<Tweet>() {
        @Override
        public Tweet createFromParcel(Parcel source) {
            return new Tweet(source);
        }

        @Override
        public Tweet[] newArray(int size) {
            return new Tweet[size];
        }
    };
}

