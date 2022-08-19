package com.codepath.apps.restclienttemplate.models;

import com.codepath.apps.restclienttemplate.Entities;
import com.codepath.apps.restclienttemplate.TimeFormatter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

import java.util.ArrayList;
import java.util.List;

@Parcel

public class Tweet {


    public String body;
    public String createdAt;
    public long id;
    public User user;
    public Extended_entities extended_entities;
    public Entities entities;
    public String Favorites;
    public String Retweets;
    public boolean favorited;
    public boolean retweeted;


    public Tweet() {}

    public static Tweet fromJson(JSONObject jsonObject) throws JSONException {
        Tweet tweet = new Tweet();
        tweet.body = jsonObject.getString("text");
        tweet.createdAt = jsonObject. getString("created_at");
        tweet.id = jsonObject.getLong("id");
        tweet.Favorites = jsonObject.getString("favorite_count");
        tweet.Retweets = jsonObject.getString("retweet_count");
        tweet.user = User.fromJson(jsonObject.getJSONObject("user"));
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


    public static List<Tweet> fromJsonArray(JSONArray jsonArray) throws JSONException {
        List<Tweet> tweets = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++){
            tweets.add(fromJson(jsonArray.getJSONObject(i)));
        }
        return tweets;
    }
}
