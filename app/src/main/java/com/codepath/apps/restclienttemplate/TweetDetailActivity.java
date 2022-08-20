package com.codepath.apps.restclienttemplate;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.codepath.apps.restclienttemplate.models.TimelineActivity;
import com.codepath.apps.restclienttemplate.models.Tweet;

import org.parceler.Parcels;

public class TweetDetailActivity extends AppCompatActivity {

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


        Toolbar toolbar =  findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.arrow_left);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setIcon(R.drawable.twitter);
        getSupportActionBar().setTitle("     Tweet");



        // Extract book object from intent extras

        Tweet tweet = Parcels.unwrap(getIntent().getParcelableExtra("tweet"));

        tvBody.setText(tweet.body);
        tvName.setText(tweet.user.name);
        tvScreenName.setText("@" + tweet.user.sreenName);
        tvTime.setText(tweet.getFormatedTimeStamp1());
        tvFavorites.setText(tweet.getFavorites() + " FAVORITES");
        tvRetweets.setText(tweet.getRetweets() + " RETWEETS");
        edReply.setHint("Reply to " + tweet.user.name);

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


