package org.ihsan.android.noline;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Ihsan on 15/2/5.
 */
public class Queue {
    private int mId;
    private String mTitle;
    private int mNextNumber;
    private int mTotal;

    public Queue(JSONObject jsonQueue) throws JSONException {
        mId = Integer.valueOf(jsonQueue.getString("id"));
        mTitle = jsonQueue.getString("title");
    }

    public int getId() {
        return mId;
    }

    public void setId(int id) {
        mId = id;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public int getNextNumber() {
        return mNextNumber;
    }

    public void setNextNumber(int nextNumber) {
        mNextNumber = nextNumber;
    }

    public int getTotal() {
        return mTotal;
    }

    public void setTotal(int total) {
        mTotal = total;
    }
}
