package org.ihsan.android.noline;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by Ihsan on 15/5/4.
 */
public class QueuedStateFragment extends Fragment {

    public static final String EXTRA_QUEUED_QUEUE = "org.ihsan.android.noline.queueQueue";
    public static final String EXTRA_QUEUED_ID = "org.ihsan.android.noline.queued_id";

    private boolean mHasArguments = false;

    private SwipeRefreshLayout mSwipeRefreshLayout;
    private LinearLayout mLinearLayout;
    private TextView mWarnTextView;
    private TextView mNumberTextView;
    private TextView mQueueNameTextView;
    private TextView mSubqueueNameTextView;
    private TextView mInTimeTextView;
    private TextView mOutTimeTextView;
    private TextView mTokenTextView;
    private TextView mEstimatedTimeTextView;
    private TextView stateTextView;
    private Activity mActivity;
    private MenuItem mQuitMenuItem;

    public static QueuedStateFragment newInstance(int queuedQueue, int queuedId) {
        Bundle args = new Bundle();
        args.putInt(EXTRA_QUEUED_QUEUE, queuedQueue);
        args.putInt(EXTRA_QUEUED_ID, queuedId);

        QueuedStateFragment fragment = new QueuedStateFragment();
        fragment.setArguments(args);

        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle
            savedInstanceState) {
        getActivity().setTitle("排队记录");
        setHasOptionsMenu(true);
        View view = inflater.inflate(R.layout.fragment_queue_state, container, false);
        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id
                .queued_state_swipeRefreshLayout);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.primary);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadQueuedState();
            }
        });
        mLinearLayout = (LinearLayout) view.findViewById(R.id.queued_state_linearLayout);
        mWarnTextView = (TextView) view.findViewById(R.id.queued_state_warn);
        mNumberTextView = (TextView) view.findViewById(R.id.queued_state_number_textView);
        mQueueNameTextView = (TextView) view.findViewById(R.id.queued_state_queue_name_textView);
        mSubqueueNameTextView = (TextView) view.findViewById(R.id
                .queued_state_subqueue_name_textView);
        mInTimeTextView = (TextView) view.findViewById(R.id.queued_state_in_time_textView);
        mOutTimeTextView = (TextView) view.findViewById(R.id.queued_state_out_time_textView);
        mTokenTextView = (TextView) view.findViewById(R.id.queued_state_token_textView);
        mEstimatedTimeTextView = (TextView) view.findViewById(R.id
                .queued_state_estimated_time_textView);
        stateTextView = (TextView) view.findViewById(R.id.queued_state_state_textView);

        loadQueuedState();

        return view;
    }

    private void loadQueuedState() {
        Bundle arguments = getArguments();
        int queuedQueue;
        int queuedId;
        if (arguments != null) {
            mHasArguments = true;
            queuedQueue = getArguments().getInt(EXTRA_QUEUED_QUEUE);
            queuedId = getArguments().getInt(EXTRA_QUEUED_ID);
            mWarnTextView.setVisibility(View.INVISIBLE);
            new GetQueuedStateTask().execute(queuedQueue, queuedId, 1);
            mSwipeRefreshLayout.setEnabled(true);
        } else {
            SharedPreferences defaultSharedPreferences = PreferenceManager
                    .getDefaultSharedPreferences(getActivity());
            queuedQueue = defaultSharedPreferences
                    .getInt(getString(R.string.queued_queue), -1);
            queuedId = defaultSharedPreferences
                    .getInt(getString(R.string.queued_id), -1);
            if (queuedQueue != -1 && queuedId != -1) {
                mWarnTextView.setVisibility(View.INVISIBLE);
                new GetQueuedStateTask().execute(queuedQueue, queuedId, 0);
                mSwipeRefreshLayout.setEnabled(true);
            } else {
                mLinearLayout.setVisibility(View.INVISIBLE);
                mSwipeRefreshLayout.setEnabled(false);
            }
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        if (!mHasArguments) {
            inflater.inflate(R.menu.menu_queued_state, menu);
            mQuitMenuItem = menu.findItem(R.id.action_quit_queue);
            if (!PreferenceManager.getDefaultSharedPreferences(getActivity()).contains(getString(R
                    .string.queued_queue))) {
                mQuitMenuItem.setEnabled(false);
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_quit_queue:
                SharedPreferences defaultSharedPreferences = PreferenceManager
                        .getDefaultSharedPreferences(getActivity());
                int queuedQueue = defaultSharedPreferences
                        .getInt(getString(R.string.queued_queue), -1);
                int queuedId = defaultSharedPreferences
                        .getInt(getString(R.string.queued_id), -1);
                new QuitQueueTask().execute(queuedQueue, queuedId);
                return true;
            case R.id.action_queued_history:
                Intent intent = new Intent(getActivity(), QueuedHistoryActivity.class);
                startActivity(intent);
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        mActivity = activity;
        super.onAttach(activity);
    }

    private class GetQueuedStateTask extends AsyncTask<Integer, Void, QueuedState> {
        @Override
        protected QueuedState doInBackground(Integer... params) {
            return new DataFetcher(mActivity).fetchQueuedState(params[0], params[1], params[2] ==
                    1);
        }


        @Override
        protected void onPostExecute(QueuedState queuedState) {
            if (queuedState != null) {
                if (!queuedState.isFresh()) {
                    Toast.makeText(mActivity, "服务器连接故障，更新信息失败", Toast.LENGTH_LONG).show();
                }
                mNumberTextView.setText(String.valueOf(queuedState.getNumber()));
                mQueueNameTextView.setText(queuedState.getQueueName());
                mSubqueueNameTextView.setText(queuedState.getSubqueueName());
                mInTimeTextView.setText(queuedState.getInTime());
                mOutTimeTextView.setText(queuedState.getOutTime());
                mTokenTextView.setText(queuedState.getToken());
                mEstimatedTimeTextView.setText(queuedState.getEstimatedTimeString());
                stateTextView.setText(queuedState.getState());
            } else {
                Toast.makeText(mActivity, "获取信息失败，请重试", Toast.LENGTH_LONG).show();
            }
            mSwipeRefreshLayout.setRefreshing(false);
        }
    }

    private class QuitQueueTask extends AsyncTask<Integer, Void, Boolean> {
        @Override
        protected Boolean doInBackground(Integer... params) {
            return new DataFetcher(mActivity).fetchQuitQueueResult(params[0], params[1]);
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            if (aBoolean) {
                PreferenceManager.getDefaultSharedPreferences(mActivity)
                        .edit()
                        .remove(getString(R.string.queued_queue))
                        .remove(getString(R.string.queued_id))
                        .commit();
                mWarnTextView.setVisibility(View.VISIBLE);
                mLinearLayout.setVisibility(View.INVISIBLE);
                mSwipeRefreshLayout.setEnabled(false);
                mQuitMenuItem.setEnabled(false);
            } else {
                Toast.makeText(mActivity, "处理失败，请重试", Toast.LENGTH_LONG).show();
            }
        }
    }
}
