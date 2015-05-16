package org.ihsan.android.noline;

import android.app.Fragment;
import android.os.Bundle;

/**
 * Created by Ihsan on 15/2/5.
 */
public class QueueDetailActivity extends SingleFragmentActivity {
    @Override
    protected Fragment createFragment() {
        Queue queue = (Queue) getIntent().getSerializableExtra(QueueDetailFragment.EXTRA_QUEUE);
        return QueueDetailFragment.newInstance(queue);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activateToolbarWithHomeEnabled();
    }
}
