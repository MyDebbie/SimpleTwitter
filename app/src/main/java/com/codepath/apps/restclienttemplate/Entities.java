package com.codepath.apps.restclienttemplate;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.codepath.apps.restclienttemplate.models.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;


@Parcel
@Entity

public class Entities {

    @ColumnInfo
    @PrimaryKey
    public long id_photo;

    @ColumnInfo
    public String Image;


    public Entities() {
    }

    public String getImage() {
        return Image;
    }

    public static Entities fromJson(JSONObject jsonObject) throws JSONException {
        Entities entities = new Entities();

        if (!jsonObject.has("media")){
            entities.Image = "";
        }
        else if(jsonObject.has("media")){
            final JSONArray Image_Array = jsonObject.getJSONArray("media");

            entities.Image = Image_Array.getJSONObject(0).getString("media_url_https");
            entities.id_photo = Image_Array.getJSONObject(0).getLong("id");
        }


        return entities;
    }
}
