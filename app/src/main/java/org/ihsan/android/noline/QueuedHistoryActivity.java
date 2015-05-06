package org.ihsan.android.noline;

import android.app.Fragment;
import android.os.Bundle;

/**
 * Created by Ihsan on 15/5/6.
 */
public class QueuedHistoryActivity extends SingleFragmentActivity {
    @Override
    protected Fragment createFragment() {
        return new QueuedHistoryFragment();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activateToolbarWithHomeEnabled();
    }
}
