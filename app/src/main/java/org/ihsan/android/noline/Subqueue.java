package org.ihsan.android.noline;

/**
 * Created by Ihsan on 15/4/17.
 */
public class Subqueue {
    private String mName;
    private int mSize;
    private int mTotal;
    private int mFirstNumber;
    private int mEstimatedTime;

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public int getSize() {
        return mSize;
    }

    public void setSize(int size) {
        mSize = size;
    }

    public int getTotal() {
        return mTotal;
    }

    public void setTotal(int total) {
        mTotal = total;
    }

    public int getFirstNumber() {
        return mFirstNumber;
    }

    public void setFirstNumber(int firstNumber) {
        mFirstNumber = firstNumber;
    }

    public int getEstimatedTime() {
        return mEstimatedTime;
    }

    public void setEstimatedTime(int estimatedTime) {
        mEstimatedTime = estimatedTime;
    }

    public String getEstimatedTimeString(){
        String estimatedTimeString = "";
        if (mEstimatedTime != 0) {
            estimatedTimeString = "\n预计等待时间：";
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
