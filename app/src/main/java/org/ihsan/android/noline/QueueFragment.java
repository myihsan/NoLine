package org.ihsan.android.noline;

import android.app.Fragment;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.melnykov.fab.FloatingActionButton;
import com.tencent.android.tpush.XGPushConfig;

import in.srain.cube.views.ptr.PtrDefaultHandler;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrHandler;
import in.srain.cube.views.ptr.header.MaterialHeader;

/**
 * Created by Ihsan on 15/2/5.
 */
public class QueueFragment extends Fragment {
    private static final String TAG = "QueueFragment";
    private static final int CHOOSE_QUEUE = 1;

    private TextView mMyNumberTextView, mMyIdentificationTextView, mQueueNowTextView;
    private FloatingActionButton mFAButton;
    private Button mQuitButton;
    private PtrFrameLayout mFrame;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_queue, container, false);

        mMyNumberTextView = (TextView) view.findViewById(R.id.queue_my_number_textView);
        mMyIdentificationTextView = (TextView) view.findViewById(R.id.queue_my_identification);
        mQueueNowTextView = (TextView) view.findViewById(R.id.queue_now_textView);
        mFAButton = (FloatingActionButton) view.findViewById(R.id.notice_list_fab);
        mQuitButton = (Button) view.findViewById(R.id.queue_quit_queue_button);
        mQuitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new QuitQueueTask().execute();
            }
        });

        mFAButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), QueueListActivity.class);
                startActivityForResult(intent, CHOOSE_QUEUE);
            }
        });


        mFrame = (PtrFrameLayout) view.findViewById(R.id.queue_material_style_ptr_frame);

        mFrame.setVisibility(View.INVISIBLE);

        // header
        final MaterialHeader header = new MaterialHeader(getActivity());
        int[] colors = getResources().getIntArray(R.array.header_colors);
        header.setColorSchemeColors(colors);
        header.setLayoutParams(new PtrFrameLayout.LayoutParams(-1, -2));
        header.setPadding(0, DisplayUtil.dp2px(getActivity(), 10), 0, DisplayUtil.dp2px(getActivity(), 10));
        header.setPtrFrameLayout(mFrame);

        mFrame.setLoadingMinTime(1000);
        mFrame.setDurationToCloseHeader(1000);
        mFrame.setHeaderView(header);
        mFrame.addPtrUIHandler(header);

        mFrame.setPtrHandler(new PtrHandler() {
            @Override
            public boolean checkCanDoRefresh(PtrFrameLayout frame, View content, View header) {
                return PtrDefaultHandler.checkContentCanBePulledDown(frame, content, header);
            }

            @Override
            public void onRefreshBegin(final PtrFrameLayout frame) {
                new GetNowQueuerTask().execute();
            }
        });

        checkQueuedQueue();

        return view;
    }

    private void checkQueuedQueue() {
        SharedPreferences pre = PreferenceManager.getDefaultSharedPreferences(getActivity());
        // If already queued a queue, show the detail
        if (pre.contains(getString(R.string.queued_number))
                && pre.contains(getString(R.string.queued_queue))) {
            mFAButton.setVisibility(View.INVISIBLE);
            mFrame.setVisibility(View.VISIBLE);
            mMyNumberTextView.setText(String.valueOf(
                    pre.getInt(getString(R.string.queued_number), -1)));
            mMyIdentificationTextView.setText(XGPushConfig.getToken(getActivity()));
//            mFrame.autoRefresh(true);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mFrame.getVisibility() == View.VISIBLE) {
            mFrame.autoRefresh(true);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        checkQueuedQueue();
    }

    private class GetNowQueuerTask extends AsyncTask<Void, Void, Integer> {
        @Override
        protected Integer doInBackground(Void... params) {
            int queueId = PreferenceManager.getDefaultSharedPreferences(getActivity())
                    .getInt(getString(R.string.queued_queue), -1);
            return new DataFetcher(getActivity()).fetchNowQueuer(queueId);
        }

        @Override
        protected void onPostExecute(Integer integer) {
            mFrame.refreshComplete();
            if (integer != -1) {
                mQueueNowTextView.setText(String.valueOf(integer));
            } else {
                Toast.makeText(getActivity(), "排队更新失败，请重试", Toast.LENGTH_LONG).show();
            }
        }
    }

    private class QuitQueueTask extends AsyncTask<Void, Void, Boolean> {
        @Override
        protected Boolean doInBackground(Void... params) {
            int queueId = PreferenceManager
                    .getDefaultSharedPreferences(getActivity())
                    .getInt(getString(R.string.queued_queue), -1);
            int number = Integer.valueOf(mMyNumberTextView.getText().toString());
            String token = XGPushConfig.getToken(getActivity());
            return new DataFetcher(getActivity()).fetchQuitQueueResult(queueId,number,token);
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            if (aBoolean) {
                PreferenceManager.getDefaultSharedPreferences(getActivity())
                        .edit()
                        .remove(getString(R.string.queued_queue))
                        .remove(getString(R.string.queued_number))
                        .commit();
                mFrame.setVisibility(View.INVISIBLE);
                mFAButton.setVisibility(View.VISIBLE);
            } else {
                Toast.makeText(getActivity(), "处理失败，请重试", Toast.LENGTH_LONG).show();
            }
        }
    }
}
