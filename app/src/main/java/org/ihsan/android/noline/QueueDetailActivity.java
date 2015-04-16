package org.ihsan.android.noline;

import android.app.Fragment;
import android.os.Bundle;

/**
 * Created by Ihsan on 15/2/5.
 */
public class QueueDetailActivity extends SingleFragmentActivity {
    @Override
    protected Fragment createFragment() {
        int queueId = getIntent().getIntExtra(QueueDetailFragment.EXTRA_QUEUE_ID, -1);
        return QueueDetailFragment.newInstance(queueId);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activateToolbarWithHomeEnabled();
    }
}
