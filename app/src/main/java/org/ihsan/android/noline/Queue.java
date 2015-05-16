package org.ihsan.android.noline;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

/**
 * Created by Ihsan on 15/2/5.
 */
public class Queue implements Serializable {
    private int mId;
    private String mImage;
    private String mName;
    private float mRating;
    private int mState;
    private String mAddress;

    public Queue(JSONObject jsonQueue) throws JSONException {
        mId = Integer.valueOf(jsonQueue.getString("id"));
        mImage = jsonQueue.getString("image");
        mName = jsonQueue.getString("name");
        mRating = Float.parseFloat(jsonQueue.getString("rating"));
        mState = Integer.valueOf(jsonQueue.getString("state"));
    }

    public int getId() {
        return mId;
    }

    public void setId(int id) {
        mId = id;
    }

    public String getImage() {
        return mImage;
    }

    public void setImage(String image) {
        mImage = image;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public float getRating() {
        return mRating;
    }

    public void setRating(float rating) {
        mRating = rating;
    }

    public int getState() {
        return mState;
    }

    public void setState(int state) {
        mState = state;
    }

    public String getAddress() {
        return mAddress;
    }

    public void setAddress(String address) {
        mAddress = address;
    }
}
