package com.codepath.apps.restclienttemplate.models;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

@Parcel
public class Extended_entities {

    public String Video;


    public Extended_entities() {
    }


    public String getVideo() {
        return Video;
    }

    public void setVideo(String video) {
        Video = video;
    }

    public static Extended_entities fromJson(JSONObject jsonObject) throws JSONException {
        Extended_entities extended_entities = new Extended_entities();

        if (!jsonObject.has("media")){
            extended_entities.Video = "";
        }
        else if(jsonObject.has("media")){
            final JSONArray Image_Array = jsonObject.getJSONArray("media");

            extended_entities.Video = Image_Array.getJSONObject(0).getString("media_url_https");
        }


        return extended_entities;
    }

}

