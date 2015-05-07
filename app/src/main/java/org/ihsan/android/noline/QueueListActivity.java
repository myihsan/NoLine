package org.ihsan.android.noline;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.Toast;

import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.accountswitcher.AccountHeader;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IProfile;
import com.tencent.android.tpush.XGIOperateCallback;
import com.tencent.android.tpush.XGPushConfig;
import com.tencent.android.tpush.XGPushManager;

/**
 * Created by Ihsan on 15/2/5.
 */
public class QueueListActivity extends SingleFragmentActivity {

    private static final int LOGIN = 1;

    private Drawer.Result mDrawerResult;

    @Override
    protected Fragment createFragment() {
        return new QueueListFragment();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activateToolbar();

        // 开启logcat输出，方便debug，发布时请关闭
        XGPushConfig.enableDebug(this, true);

        Context context = getApplicationContext();
        XGPushManager.registerPush(context, new XGIOperateCallback() {
            @Override
            public void onSuccess(Object data, int flag) {
                Log.d("TPush", "注册成功，设备token为：" + data);
            }

            @Override
            public void onFail(Object data, int errCode, String msg) {
                Log.d("TPush", "注册失败，错误码：" + errCode + ",错误信息：" + msg);
            }
        });

        AccountHeader.Result headerResult = new AccountHeader()
                .withActivity(this)
                .withHeaderBackground(R.drawable.drawer_header)
                .withHeaderBackgroundScaleType(ImageView.ScaleType.CENTER_CROP)
                .build();

        mDrawerResult = new Drawer()
                .withActivity(this)
                .withToolbar(mToolbar)
                .withAccountHeader(headerResult)
                .addDrawerItems(
                        new PrimaryDrawerItem().withName("我要排队").withIcon(R.drawable
                                .ic_format_list_numbered_grey600_24dp),
                        new PrimaryDrawerItem().withName("排队记录").withIcon(R.drawable
                                .ic_history_grey600_24dp),
                        new DividerDrawerItem(),
                        new SecondaryDrawerItem().withCheckable(false)
                )
                .build();

        checkLogin();
    }

    private void checkLogin() {
        int userId = PreferenceManager
                .getDefaultSharedPreferences(this)
                .getInt(getString(R.string.logined_user_id), -1);
        if (userId != -1) {
            ((SecondaryDrawerItem)mDrawerResult.getDrawerItems().get(3)).setName("注销");
            mDrawerResult.setOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int position, long
                        id,
                                        IDrawerItem iDrawerItem) {
                    FragmentManager fragmentManager = getFragmentManager();
                    Fragment fragment;
                    switch (position) {
                        case 0:
                            fragment = new QueueListFragment();
                            fragmentManager.beginTransaction()
                                    .replace(R.id.fragmentContainer, fragment)
                                    .commit();
                            break;
                        case 1:
                            fragment = new QueuedStateFragment();
                            fragmentManager.beginTransaction()
                                    .replace(R.id.fragmentContainer, fragment)
                                    .commit();
                            break;
                        case 3:
                            PreferenceManager.getDefaultSharedPreferences(QueueListActivity.this)
                                    .edit()
                                    .remove(getString(R.string.logined_user_id))
                                    .commit();
                            checkLogin();
                            break;
                    }

                }
            });
        } else {
            ((SecondaryDrawerItem)mDrawerResult.getDrawerItems().get(3)).setName("登录");
            mDrawerResult.setOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int position, long
                        id,
                                        IDrawerItem iDrawerItem) {
                    FragmentManager fragmentManager = getFragmentManager();
                    Fragment fragment;
                    switch (position) {
                        case 0:
                            fragment = new QueueListFragment();
                            fragmentManager.beginTransaction()
                                    .replace(R.id.fragmentContainer, fragment)
                                    .commit();
                            break;
                        case 1:
                            fragment = new QueuedStateFragment();
                            fragmentManager.beginTransaction()
                                    .replace(R.id.fragmentContainer, fragment)
                                    .commit();
                            break;
                        case 3:
                            Intent intent = new Intent(QueueListActivity.this, LoginActivity.class);
                            startActivityForResult(intent, LOGIN);
                            break;
                    }

                }
            });
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            checkLogin();
        }
    }

    public void setDrawerSelection(int position) {
        mDrawerResult.setSelection(position);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        if (mDrawerResult.getCurrentSelection() == 0) {
            QueueListFragment fragment = (QueueListFragment) getFragmentManager()
                    .findFragmentById(R.id.fragmentContainer);
            if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
                String keyword = intent.getStringExtra(SearchManager.QUERY);
                fragment.searchQueue(keyword);
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (mDrawerResult.getCurrentSelection() == 0) {
            QueueListFragment fragment = (QueueListFragment) getFragmentManager()
                    .findFragmentById(R.id.fragmentContainer);
            if (!fragment.isSearchViewIconified()) {
                fragment.setSearchViewIconified(true);
                fragment.setSearchViewIconified(true);
            }
        } else {
            super.onBackPressed();
        }
    }
}
