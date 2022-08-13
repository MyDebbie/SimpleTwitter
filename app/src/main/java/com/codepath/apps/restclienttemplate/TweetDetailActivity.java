package com.codepath.apps.restclienttemplate;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.codepath.apps.restclienttemplate.models.Tweet;

import org.parceler.Parcels;

public class TweetDetailActivity extends AppCompatActivity {

    ImageView ivProfileImage;
    TextView tvBody;
    TextView tvScreenName;
    TextView tvTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tweet_detail);

        // Fetch views
        ivProfileImage = findViewById(R.id.ivProfileImage);
        tvBody = findViewById(R.id.tvBody);
        tvScreenName = findViewById(R.id.tvSreenName);
        tvTime = findViewById(R.id.tvTime);

        Toolbar toolbar =  findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Extract book object from intent extras

        Tweet tweet = Parcels.unwrap(getIntent().getParcelableExtra("tweet"));

        tvBody.setText(tweet.body);
        tvScreenName.setText(tweet.user.sreenName);
        tvTime.setText(tweet.createdAt);
        Glide.with(this).load(tweet.user.profileImageUrl).into(ivProfileImage);

    }
}