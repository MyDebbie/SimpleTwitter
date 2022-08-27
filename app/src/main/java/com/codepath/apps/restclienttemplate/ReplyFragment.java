package com.codepath.apps.restclienttemplate;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.apps.restclienttemplate.models.User;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;

import org.json.JSONException;
import org.parceler.Parcels;

import okhttp3.Headers;

public class ReplyFragment extends DialogFragment {

    public static final String TAG = "ComposeActivity";
    public static final int MAX_TWEET_LENGTH = 140;
    TwitterClient client;
    Context context;

    EditText edReply;
    Button btnTweet;
    ImageButton cnbutton;
    ImageView ivprofile;
    TextView tvScreenName1;
    TextView tvName1;
    TextView txReply;


    public ReplyFragment(){}


    public interface ReplyFragmentListener {
        void onFinishReplyDialog(Tweet tweet);
    }


    public static com.codepath.apps.restclienttemplate.ReplyFragment newInstance(String title) {
        com.codepath.apps.restclienttemplate.ReplyFragment frag = new com.codepath.apps.restclienttemplate.ReplyFragment();
        Bundle args = new Bundle();
        args.putString("title", title);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.reply_fragment, container);

    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        client = TwitterApp.getRestClient(context);



        tvScreenName1 = view.findViewById(R.id.tvSreenName1);
        tvName1 = view.findViewById(R.id.tvName1);
        ivprofile = view.findViewById(R.id.ivprofile);
        edReply = view.findViewById(R.id.edReply);
        btnTweet = view.findViewById(R.id.btnTweet);
        cnbutton = view.findViewById(R.id.cnbutton);
        txReply = view.findViewById(R.id.txReply);


        Bundle bundle = getArguments();
        User currentuser = Parcels.unwrap(bundle.getParcelable("CurrentUser"));

        Tweet tweet = Parcels.unwrap(bundle.getParcelable("tweet"));
        txReply.setHint("In reply to " + tweet.user.sreenName);
        edReply.setText("@" + tweet.user.sreenName + "  ");



        tvName1.setText(currentuser.name);
        tvScreenName1.setText(currentuser.sreenName);
        Glide.with(getContext()).load(currentuser.profileImageUrl).transform(new CircleCrop()).into(ivprofile);








        String title = getArguments().getString("title", "Compose tweet");
        getDialog().getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getContext());
        String draft = pref.getString("TEXT", "");
        if(!draft.isEmpty()) {
            edReply.setText(draft);
        }



        // Set click listener on button
        btnTweet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String tweetContent = edReply.getText().toString();
                if (tweetContent.isEmpty()) {
                    Toast.makeText(context, "Sorry, your tweet cannot be empty", Toast.LENGTH_LONG).show();
                    return;
                }
                if (tweetContent.length() > MAX_TWEET_LENGTH) {
                    Toast.makeText(context, "Sorry, your tweet is too long", Toast.LENGTH_LONG).show();
                    return;
                }
                // Make an API call to Twitter to publish the tweet
                client.publishTweet(tweetContent, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Headers headers, JSON json) {
                        Log.i(TAG, "onSuccess to publish tweet");
                        try {
                            Tweet tweet = Tweet.fromJson(json.jsonObject);
                            Log.i(TAG, "Published tweet says: " + tweet.body);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                        Log.e(TAG, "onFailure to publish tweet", throwable);
                    }
                });
                dismiss();
            }
        });

        cnbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String tweetContent = edReply.getText().toString();
                SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getContext());
                SharedPreferences.Editor edit = pref.edit();
                edit.putString("TEXT", tweetContent);
                edit.commit();
                dismiss();

            }
        });

        getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        getDialog().getWindow().setLayout(700,1400);

    }


}


