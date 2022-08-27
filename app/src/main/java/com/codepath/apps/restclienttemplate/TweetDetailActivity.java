package com.codepath.apps.restclienttemplate;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.codepath.apps.restclienttemplate.models.TimelineActivity;
import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;

import org.json.JSONException;
import org.parceler.Parcels;

import java.util.Objects;

import okhttp3.Headers;

public class TweetDetailActivity extends AppCompatActivity {

    public static final String TAG = "DetailActivity";
    public static final int MAX_TWEET_LENGTH = 140;
    TwitterClient client;

    ImageView ivProfileImage;
    TextView tvBody;
    TextView tvScreenName;
    TextView tvName;
    TextView tvTime;
    TextView tvFavorites;
    TextView tvRetweets;
    TextView tvHeart;
    TextView tvRetweet;
    ImageView ivImage;
    EditText edReply;
    Button btnTweet;


    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tweet_detail);


        // Fetch views
        ivProfileImage = findViewById(R.id.ivProfileImage);
        tvBody = findViewById(R.id.tvBody);
        tvScreenName = findViewById(R.id.tvSreenName);
        tvName = findViewById(R.id.tvName);
        tvTime = findViewById(R.id.tvTime);
        tvFavorites = findViewById(R.id.tvFavorites);
        tvRetweets = findViewById(R.id.tvRetweets);
        tvHeart = findViewById(R.id.tvHeart);
        tvRetweet = findViewById(R.id.tvRetweet);
        ivImage = findViewById(R.id.ivImage);
        edReply = findViewById(R.id.edReply);
        btnTweet = findViewById(R.id.btnTweet);


        Toolbar toolbar =  findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setHomeAsUpIndicator(R.drawable.arrow_left);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setIcon(R.drawable.twitter);
        getSupportActionBar().setTitle("     Tweet");

        client = TwitterApp.getRestClient(this);




        // Extract book object from intent extras

        Tweet tweet = Parcels.unwrap(getIntent().getParcelableExtra("tweet"));

        tvBody.setText(tweet.body);
        tvName.setText(tweet.user.name);
        tvScreenName.setText("@" + tweet.user.sreenName);
        tvTime.setText(tweet.getFormatedTimeStamp1());
        tvFavorites.setText(tweet.getFavorites() + " FAVORITES");
        tvRetweets.setText(tweet.getRetweets() + " RETWEETS");
        edReply.setHint("Reply to " + tweet.user.name);
        edReply.setText("@" + tweet.user.sreenName);

        if(tweet.favorited) {
            Drawable drawable = ContextCompat.getDrawable(TweetDetailActivity.this, R.drawable.fill_heart);
            drawable.setBounds(0, 0, drawable.getMinimumHeight(), drawable.getMinimumWidth());
            tvHeart.setCompoundDrawables(drawable, null, null, null);
        }

        if(tweet.retweeted){
            Drawable drawable = ContextCompat.getDrawable(TweetDetailActivity.this, R.drawable.green_retweet);
            drawable.setBounds(0,0, drawable.getMinimumHeight(), drawable.getMinimumWidth());
            tvRetweet.setCompoundDrawables(drawable, null, null, null);
        }


            tvHeart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!tweet.favorited){
                    Drawable drawable = ContextCompat.getDrawable(TweetDetailActivity.this, R.drawable.fill_heart);
                    drawable.setBounds(0,0, drawable.getMinimumHeight(), drawable.getMinimumWidth());
                    tvHeart.setCompoundDrawables(drawable, null, null, null);


                    tvHeart.setText(String.valueOf(++tweet.Favorites));
                    tweet.favorited = true;
                }
                else
                {
                    ++tweet.Favorites;

                    Drawable drawable = ContextCompat.getDrawable(TweetDetailActivity.this, R.drawable.heart);
                    drawable.setBounds(0,0, drawable.getMinimumHeight(), drawable.getMinimumWidth());
                    tvHeart.setCompoundDrawables(drawable, null, null, null);

                    --tweet.Favorites;
                    tvHeart.setText(String.valueOf(tweet.Favorites));
                    tweet.favorited = false;
                }
            }
        });


        tvRetweet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!tweet.retweeted){
                    Drawable drawable = ContextCompat.getDrawable(TweetDetailActivity.this, R.drawable.green_retweet);
                    drawable.setBounds(0,0, drawable.getMinimumHeight(), drawable.getMinimumWidth());
                    tvRetweet.setCompoundDrawables(drawable, null, null, null);


                    tvRetweet.setText(String.valueOf(++tweet.Retweets));
                    tweet.retweeted = true;
                }
                else
                {
                    ++tweet.Retweets;

                    Drawable drawable = ContextCompat.getDrawable(TweetDetailActivity.this, R.drawable.retweet);
                    drawable.setBounds(0,0, drawable.getMinimumHeight(), drawable.getMinimumWidth());
                    tvRetweet.setCompoundDrawables(drawable, null, null, null);

                    --tweet.Retweets;
                    tvRetweet.setText(String.valueOf(tweet.Retweets));
                    tweet.retweeted = false;
                }
            }
        });

        btnTweet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String tweetContent = edReply.getText().toString();
                if (tweetContent.isEmpty()) {
                    Toast.makeText(TweetDetailActivity.this, "Sorry, your tweet cannot be empty", Toast.LENGTH_LONG).show();
                    return;
                }
                if (tweetContent.length() > MAX_TWEET_LENGTH) {
                    Toast.makeText(TweetDetailActivity.this, "Sorry, your tweet is too long", Toast.LENGTH_LONG).show();
                    return;
                }
                Toast.makeText(TweetDetailActivity.this, tweetContent, Toast.LENGTH_LONG).show();
                // Make an API call to Twitter to publish the tweet
                client.publishTweet(tweetContent, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Headers headers, JSON json) {
                        Log.i(TAG, "onSuccess to publish tweet");
                        try {
                            Tweet tweet = Tweet.fromJson(json.jsonObject);
                            Log.i(TAG, "Published tweet says: " + tweet.body);
                            Intent intent = new Intent();
                            intent.putExtra("tweet", Parcels.wrap(tweet));
                            // Set result code and bundle data for response
                            setResult(RESULT_OK, intent);
                            // Closes the activity, pass data to parent
                            finish();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                        Log.e(TAG, "onFailure to publish tweet", throwable);
                    }
                });


            }
        });


        Glide.with(this).load(tweet.user.profileImageUrl).transform(new CircleCrop()).into(ivProfileImage);

        if (!tweet.entities.Image.isEmpty()) {
            ivImage.setVisibility(View.VISIBLE);
            Glide.with(this).load(tweet.entities.Image).transform(new RoundedCorners(60)).into(ivImage);
        }
    }
    @Override
    public boolean onOptionsItemSelected( MenuItem item ) {
                Intent intent = new Intent(TweetDetailActivity.this, TimelineActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivityIfNeeded(intent, 0);
                return true;
        }

}


