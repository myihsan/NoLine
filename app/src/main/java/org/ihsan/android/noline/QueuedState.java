package org.ihsan.android.noline;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Ihsan on 15/5/4.
 */
public class QueuedState {
    private int mNumber;
    private String mQueueName;
    private String mSubqueueName;
    private String mInTime;
    private String mOutTime;
    private int mEstimatedTime;
    private String state;

    public QueuedState(JSONObject jsonQueue) throws JSONException {
        mNumber = Integer.valueOf(jsonQueue.getString("number"));
        mQueueName = jsonQueue.getString("name");
        mSubqueueName = jsonQueue.getString("subqueueName");
        mInTime = jsonQueue.getString("inTime");
        mOutTime = jsonQueue.getString("outTime");
        String estimatedTime = jsonQueue.getString("estimatedTime");
        if (estimatedTime != "null") {
            mEstimatedTime = Integer.valueOf(estimatedTime);
        } else {
            mEstimatedTime = 0;
        }
        state = jsonQueue.getString("state");
    }

    public int getNumber() {
        return mNumber;
    }

    public void setNumber(int number) {
        mNumber = number;
    }

    public String getQueueName() {
        return mQueueName;
    }

    public void setQueueName(String queueName) {
        mQueueName = queueName;
    }

    public String getSubqueueName() {
        return mSubqueueName;
    }

    public void setSubqueueName(String subqueueName) {
        mSubqueueName = subqueueName;
    }

    public String getInTime() {
        return mInTime;
    }

    public void setInTime(String inTime) {
        mInTime = inTime;
    }

    public String getOutTime() {
        return mOutTime;
    }

    public void setOutTime(String outTime) {
        mOutTime = outTime;
    }

    public int getEstimatedTime() {
        return mEstimatedTime;
    }

    public void setEstimatedTime(int estimatedTime) {
        mEstimatedTime = estimatedTime;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getEstimatedTimeString() {
        String estimatedTimeString = "预计时间：";
        if (mEstimatedTime == -1) {
            estimatedTimeString="排队结束";
        } else if (mEstimatedTime == 0) {
            estimatedTimeString="预计时间：无法计算";
        } else {
            int hour = mEstimatedTime / 3600;
            int minute = (mEstimatedTime - hour * 3600) / 60;
            if (hour != 0) {
                if (minute != 0) {
                    estimatedTimeString += hour + "小时" + minute + "分钟";
                } else {
                    estimatedTimeString += hour + "小时";
                }
            } else {
                estimatedTimeString += minute + "分钟";
            }
        }
        return estimatedTimeString;
    }
}
