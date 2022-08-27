package com.codepath.apps.restclienttemplate.models;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.codepath.apps.restclienttemplate.ComposeFragment;
import com.codepath.apps.restclienttemplate.EndlessRecyclerViewScrollListener;
import com.codepath.apps.restclienttemplate.R;
import com.codepath.apps.restclienttemplate.TweetsAdapter;
import com.codepath.apps.restclienttemplate.TwitterApp;
import com.codepath.apps.restclienttemplate.TwitterClient;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Headers;

public class TimelineActivity extends AppCompatActivity implements ComposeFragment.ComposeFragmentListener {

    private static final String TAG = "TimelineActivity";
    public static User current_user;

    TweetDao tweetDao;

    TwitterClient client;
    RecyclerView rvTweets;
    TweetsAdapter adapter;
    List<Tweet> tweets;
    EndlessRecyclerViewScrollListener scrollListener;
    SwipeRefreshLayout swipeContainer;
    FloatingActionButton fltbutton;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);

         client = TwitterApp.getRestClient(this);


        tweetDao = ((TwitterApp) getApplicationContext()).getMyDatabase().tweetDao();


        fltbutton = findViewById(R.id.fltbutton);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setLogo(R.drawable.twitter);
        getSupportActionBar().setTitle("");

        fltbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showEditDialog();
            }
        });





        swipeContainer = findViewById(R.id.swipeContainer);
        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
         swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
             @Override
             public void onRefresh() {
                Log.i(TAG, "fetching new data!");
                 populateHomeTimeline();
             }
         });

         // Find the recycler view
        rvTweets = findViewById(R.id.rvTweets);
        // Init the list of tweets and adapter
        tweets = new ArrayList<>();
        adapter = new TweetsAdapter(this, tweets);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        // Recycler view setup: layout manager and the adapter
        rvTweets.setLayoutManager(layoutManager);
        rvTweets.setAdapter(adapter);


        scrollListener = new EndlessRecyclerViewScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                Log.i(TAG, "onLoadMore: " + page);
                loadMoreData();
            }
        };
        // Adds the scroll listener to RecyclerView
        rvTweets.addOnScrollListener(scrollListener);

        // Query for existing tweets in the DB

        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                Log.i(TAG, "Showing data from database");
                List<TweetWithUser> tweetWithUsers = tweetDao.recentItems();
                List<Tweet> tweetsFromDB = TweetWithUser.getTweetList(tweetWithUsers);

                adapter.clear();
                adapter.addAll(tweetsFromDB);
            }
        });
        populateHomeTimeline();
    }


    private void showEditDialog() {
        FragmentManager fm = getSupportFragmentManager();
        ComposeFragment composeFragment = ComposeFragment.newInstance("Some Title");


        Bundle bundle = new Bundle();
        bundle.putParcelable("CurrentUser", Parcels.wrap(current_user));
        composeFragment.setArguments(bundle);
        composeFragment.show(fm, "Compose_Fragament");


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.add_tweet, menu);
        MenuItem Add_Tweet_Item= menu.findItem(R.id.action_add_tweet);

        return true;
    }

    private void loadMoreData() {
        // 1. Send an API request to retrieve appropriate paginated data
        client.getNextPageOfTweets(new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                Log.i(TAG, "onSuccess for loadMoreData!" + json.toString());
                // 2. Deserialize and construct new model objects from the API response
                JSONArray jsonArray = json.jsonArray;
                List<Tweet> tweets = null;
                try {
                     tweets = Tweet.fromJsonArray(jsonArray);
                    // 3. Append the new data objects to the existing set of items inside the array of items
                    // 4. Notify the adapter of the new items made with `notifyItemRangeInserted()`

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                adapter.addAll(tweets);
            }

            @Override
            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                Log.i(TAG, "onFailure for loadMoreData!", throwable);

            }
        }, tweets.get(tweets.size() - 1).id);
    }

    private void populateHomeTimeline() {
        client.getHomeTimeline(new JsonHttpResponseHandler() {
           @Override
           public void onSuccess(int statusCode, Headers headers, JSON json) {
               Log.i(TAG, "onSuccess!" + json.toString());
               JSONArray jsonArray = json.jsonArray;
               try {
                   List<Tweet> tweetsFromNetwork = Tweet.fromJsonArray(jsonArray);
                   adapter.clear();
                   adapter.addAll(tweetsFromNetwork);
                   // Now we call setRefreshing(false) to signal refresh has finished
                   swipeContainer.setRefreshing(false);
                   adapter.notifyDataSetChanged();

                   AsyncTask.execute(new Runnable() {
                       @Override
                       public void run() {
                           Log.i(TAG, "Saving data into database");
                           // insert users first
                           List<User> usersFromNetwork = User.fromJsonTweetArray(tweetsFromNetwork);
                           tweetDao.insertModel(usersFromNetwork.toArray(new User[0]));

                           // insert tweets next
                           tweetDao.insertModel(tweetsFromNetwork.toArray(new Tweet[0]));
                       }
                   });
               } catch (JSONException e) {
                   Log.e(TAG, "Json exception", e);
               }

           }

           @Override
           public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
               Log.e(TAG, "onFailure!" + response , throwable);

           }
       });

        client.getCurrent_User(new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                Log.i(TAG, "onSuccess getCurrent User" + json.toString());
                JSONObject jsonObject = json.jsonObject;

                try {
                    current_user = User.fromJson(jsonObject);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                Log.i(TAG, "onFailure getCurrent User", throwable);

            }
        });
    }

    @Override
    public void onFinishComposeDialog(Tweet tweet) {
        // Update the RV with the tweet
        // Modify data source of tweets
        tweets.add(0, tweet);
        // update the adapter
        adapter.notifyItemInserted(0);
        rvTweets.smoothScrollToPosition(0);
    }
}