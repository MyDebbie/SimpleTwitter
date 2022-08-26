package com.codepath.apps.restclienttemplate.models;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.codepath.apps.restclienttemplate.Entities;
import com.codepath.apps.restclienttemplate.TimeFormatter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

import java.util.ArrayList;
import java.util.List;

@Parcel
@Entity(foreignKeys = @ForeignKey(entity = User.class, parentColumns = "id", childColumns = "userId"))


public class Tweet {

    @ColumnInfo
    @PrimaryKey
    public Long id;

    @ColumnInfo
    public String body;

    @ColumnInfo
    public String createdAt;

    @Ignore
    public User user;
    @ColumnInfo
    public long userId;

    @Ignore
    public Extended_entities extended_entities;

    @Ignore
    public Entities entities;

    @ColumnInfo
    public int Favorites;

    @ColumnInfo
    public int Retweets;

    @ColumnInfo
    public boolean favorited;

    @ColumnInfo
    public boolean retweeted;


    public Tweet() {}

    public static Tweet fromJson(JSONObject jsonObject) throws JSONException {
        Tweet tweet = new Tweet();
        tweet.body = jsonObject.getString("text");
        tweet.createdAt = jsonObject. getString("created_at");
        tweet.id = jsonObject.getLong("id");
        tweet.Favorites = jsonObject.getInt("favorite_count");
        tweet.Retweets = jsonObject.getInt("retweet_count");
        User user = User.fromJson(jsonObject.getJSONObject("user"));
        tweet.userId = user.id;
        tweet.user = user;
        tweet.favorited = jsonObject.getBoolean("favorited");
        tweet.retweeted = jsonObject.getBoolean("retweeted");
        tweet.entities = Entities.fromJson(jsonObject.getJSONObject("entities"));


        if (jsonObject.has("extended_entities")){
            tweet.extended_entities = Extended_entities.fromJson(jsonObject.getJSONObject("extended_entities"));
        }
        else if(jsonObject.has("extended_entities")){
            tweet.extended_entities.Video = "";
        }

        return tweet;

    }

    public String getFormattedTimestamp(){
        return TimeFormatter.getTimeDifference(createdAt);
    }

    public String getFormatedTimeStamp1(){
        return TimeFormatter.getTimeStamp(createdAt);
    }

    public String getFavorites() {
        return String.valueOf(Favorites);
    }

    public String getRetweets() {
        return String.valueOf(Retweets);
    }

    public static List<Tweet> fromJsonArray(JSONArray jsonArray) throws JSONException {
        List<Tweet> tweets = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++){
            tweets.add(fromJson(jsonArray.getJSONObject(i)));
        }
        return tweets;
    }
}
