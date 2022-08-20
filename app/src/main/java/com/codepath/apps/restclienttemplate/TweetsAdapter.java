package com.codepath.apps.restclienttemplate;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.codepath.apps.restclienttemplate.models.Tweet;

import org.parceler.Parcels;

import java.util.List;

public class TweetsAdapter extends RecyclerView.Adapter<TweetsAdapter.ViewHolder> {

    Context context;
    List<Tweet> tweets;
    // Pass in the context and list of tweets
    public TweetsAdapter (Context context, List<Tweet> tweets) {
        this.context = context;
        this.tweets = tweets;
    }

    // For each row, inflate the layout
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_tweet, parent, false);
        return new ViewHolder(view);
    }

    // Bind values based on the position of the element
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // Get the data at position
        Tweet tweet = tweets.get(position);
        // Bind the tweet with view holder
        holder.bind(tweet);
        holder.tvTime.setText(tweet.getFormattedTimestamp());

    }

    @Override
    public int getItemCount() {
        return tweets.size();
    }

    // clean all elements of the recycler

    public void clear() {

        tweets.clear();
        notifyDataSetChanged();
    }

    // Add a list of items

    public void addAll(List<Tweet> tweetList) {
        tweets.addAll(tweetList);
        notifyDataSetChanged();
    }


    // Define a viewholder
    public class ViewHolder extends RecyclerView.ViewHolder{

        ImageView ivProfileImage;
        TextView tvBody;
        TextView tvScreenName;
        TextView tvName;
        TextView tvTime;
        RelativeLayout container;
        TextView tvHeart;
        TextView tvRetweet;
        ImageView ivImage;



        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivProfileImage = itemView.findViewById(R.id.ivProfileImage);
            tvBody = itemView.findViewById(R.id.tvBody);
            tvScreenName = itemView.findViewById(R.id.tvSreenName);
            tvName = itemView.findViewById(R.id.tvName);
            tvTime = itemView.findViewById(R.id.tvTime);
            container = itemView.findViewById(R.id.container);
            tvHeart = itemView.findViewById(R.id.tvHeart);
            tvRetweet = itemView.findViewById(R.id.tvRetweet);
            ivImage = itemView.findViewById(R.id.ivImage);


        }

        public void bind(Tweet tweet) {
            tvBody.setText(tweet.body);
            tvScreenName.setText("@" + tweet.user.sreenName);
            tvName.setText( tweet.user.name);
            tvRetweet.setText(tweet.getRetweets());
            tvHeart.setText(tweet.getFavorites());

            container.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, TweetDetailActivity.class);
                    intent.putExtra("tweet", Parcels.wrap(tweet));
                    context.startActivity(intent);

                }
            });

            tvHeart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(!tweet.favorited){
                        Drawable drawable = ContextCompat.getDrawable(context, R.drawable.fill_heart);
                        drawable.setBounds(0,0, drawable.getMinimumHeight(), drawable.getMinimumWidth());
                        tvHeart.setCompoundDrawables(drawable, null, null, null);


                        tvHeart.setText(String.valueOf(++tweet.Favorites));
                        tweet.favorited = true;
                    }
                    else
                    {
                        ++tweet.Favorites;

                        Drawable drawable = ContextCompat.getDrawable(context, R.drawable.heart);
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
                        Drawable drawable = ContextCompat.getDrawable(context, R.drawable.green_retweet);
                        drawable.setBounds(0,0, drawable.getMinimumHeight(), drawable.getMinimumWidth());
                        tvRetweet.setCompoundDrawables(drawable, null, null, null);


                        tvRetweet.setText(String.valueOf(++tweet.Retweets));
                        tweet.retweeted = true;
                    }
                    else
                    {
                        ++tweet.Retweets;

                        Drawable drawable = ContextCompat.getDrawable(context, R.drawable.retweet);
                        drawable.setBounds(0,0, drawable.getMinimumHeight(), drawable.getMinimumWidth());
                        tvRetweet.setCompoundDrawables(drawable, null, null, null);

                        --tweet.Retweets;
                        tvRetweet.setText(String.valueOf(tweet.Retweets));
                        tweet.retweeted = false;
                    }
                }
            });


            Glide.with(context).load(tweet.user.profileImageUrl).transform(new CircleCrop()).into(ivProfileImage);


            if (!tweet.entities.Image.isEmpty()) {
                ivImage.setVisibility(View.VISIBLE);
            Glide.with(context).load(tweet.entities.Image).transform(new RoundedCorners(60)).into(ivImage);
            }

        }
    }

}
