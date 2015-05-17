package org.ihsan.android.noline;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;

import com.mikepenz.aboutlibraries.Libs;
import com.mikepenz.aboutlibraries.ui.LibsFragment;

/**
 * Created by Ihsan on 15/5/17.
 */
public class AboutActivity extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragment);
        activateToolbarWithHomeEnabled();

        LibsFragment fragment = new Libs.Builder()
                .withAboutAppName("NoLine")
                .withAboutIconShown(true)
                .withAboutDescription("毕业设计作品")
                .withFields(R.string.class.getFields())
                .withExcludedLibraries()
                .withVersionShown(true)
                .withLicenseShown(true)
                .fragment();

        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.fragmentContainer, fragment).commit();
    }
}
