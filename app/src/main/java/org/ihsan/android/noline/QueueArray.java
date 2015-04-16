package org.ihsan.android.noline;

import android.content.Context;

import java.util.ArrayList;

/**
 * Created by Ihsan on 15/2/5.
 */
public class QueueArray {
    private ArrayList<Queue> mQueues;

    private static QueueArray sQueueArray;
    private Context mAppContext;

    private QueueArray(Context appContext) {
        mAppContext = appContext;
        mQueues = new ArrayList<Queue>();
    }

    public static QueueArray get(Context context) {
        if (sQueueArray == null) {
            sQueueArray = new QueueArray(context);
        }
        return sQueueArray;
    }

    public ArrayList<Queue> getQueues() {
        return mQueues;
    }

    public Queue getQueue(int id) {
        for (Queue queue : mQueues) {
            if (queue.getId() == id) {
                return queue;
            }
        }
        return null;
    }

    public void refreshQueues(ArrayList<Queue> queues) {
        mQueues.clear();
        mQueues.addAll(queues);
    }
}
