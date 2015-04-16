package org.ihsan.android.noline;

import android.app.Fragment;
import android.os.Bundle;

/**
 * Created by Ihsan on 15/2/5.
 */
public class QueueListActivity extends SingleFragmentActivity {
    @Override
    protected Fragment createFragment() {
        return new QueueListFragment();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activateToolbarWithHomeEnabled();
    }
}
