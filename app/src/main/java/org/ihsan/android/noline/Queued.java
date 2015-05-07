package org.ihsan.android.noline;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Ihsan on 15/5/7.
 */
public class Queued {
    private int mQueueId;
    private String mQueueName;
    private String mQueueImage;
    private int mQueuedId;
    private String mDate;

    public Queued(JSONObject jsonObject) throws JSONException {
        mQueueId = Integer.valueOf(jsonObject.getString("queueId"));
        mQueueName = jsonObject.getString("queueName");
        mQueueImage = jsonObject.getString("queueImage");
        mQueuedId = Integer.valueOf(jsonObject.getString("queuedId"));
        mDate = jsonObject.getString("date");
    }

    public int getQueueId() {
        return mQueueId;
    }

    public void setQueueId(int queueId) {
        mQueueId = queueId;
    }

    public String getQueueName() {
        return mQueueName;
    }

    public void setQueueName(String queueName) {
        mQueueName = queueName;
    }

    public String getQueueImage() {
        return mQueueImage;
    }

    public void setQueueImage(String queueImage) {
        mQueueImage = queueImage;
    }

    public int getQueuedId() {
        return mQueuedId;
    }

    public void setQueuedId(int queuedId) {
        mQueuedId = queuedId;
    }

    public String getDate() {
        return mDate;
    }

    public void setDate(String date) {
        mDate = date;
    }
}
