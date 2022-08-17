package com.codepath.apps.restclienttemplate;

import com.codepath.apps.restclienttemplate.models.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;


@Parcel
public class Entities {

    public String Image;





    public Entities() {
    }


    public static Entities fromJson(JSONObject jsonObject) throws JSONException {
        Entities entities = new Entities();

        if (!jsonObject.has("media")){
            entities.Image = "";
        }
        else if(jsonObject.has("media")){
            final JSONArray Image_Array = jsonObject.getJSONArray("media");

            entities.Image = Image_Array.getJSONObject(0).getString("media_url_https");
        }


        return entities;
    }
}
