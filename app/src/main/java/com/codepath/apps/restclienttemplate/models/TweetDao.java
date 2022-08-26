package com.codepath.apps.restclienttemplate.models;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.codepath.apps.restclienttemplate.Entities;

import java.util.List;

@Dao
public interface TweetDao {

    @Query("SELECT Tweet.body AS tweet_body, Tweet.createdAt AS tweet_createdAt, Tweet.id AS tweet_id, Tweet.Favorites AS tweet_Favorites, Tweet.Retweets AS tweet_Retweets, Tweet.favorited AS tweet_favorited, Tweet.retweeted AS tweet_retweeted, User.*, Entities.* FROM Tweet, Entities INNER JOIN User ON Tweet.userId = User.id ORDER BY Tweet.createdAt DESC LIMIT 20")
    List<TweetWithUser> recentItems();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertModel(Tweet... tweets);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertModel(User... users);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertModel(Entities... entities);

}

