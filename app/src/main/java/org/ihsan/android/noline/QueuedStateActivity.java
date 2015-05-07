package org.ihsan.android.noline;

import android.app.Fragment;
import android.os.Bundle;

/**
 * Created by Ihsan on 15/5/7.
 */
public class QueuedStateActivity extends SingleFragmentActivity {
    @Override
    protected Fragment createFragment() {
        int queuedQueue = getIntent().getIntExtra(QueuedStateFragment.EXTRA_QUEUED_QUEUE, -1);
        int queuedId = getIntent().getIntExtra(QueuedStateFragment.EXTRA_QUEUED_ID, -1);
        return QueuedStateFragment.newInstance(queuedQueue,queuedId);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activateToolbarWithHomeEnabled();
    }
}
