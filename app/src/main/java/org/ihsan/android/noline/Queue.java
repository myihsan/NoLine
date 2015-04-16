package org.ihsan.android.noline;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Ihsan on 15/2/5.
 */
public class Queue {
    private int mId;
    private String mName;
    private int mState;

    public Queue(JSONObject jsonQueue) throws JSONException {
        mId = Integer.valueOf(jsonQueue.getString("id"));
        mName = jsonQueue.getString("name");
        mState = Integer.valueOf(jsonQueue.getString("state"));
    }

    public int getId() {
        return mId;
    }

    public void setId(int id) {
        mId = id;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public int getState() {
        return mState;
    }

    public void setState(int state) {
        mState = state;
    }
}
