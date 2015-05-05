package org.ihsan.android.noline;

import android.app.Activity;
import android.app.Fragment;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
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

    private LinearLayout mLinearLayout;
    private TextView mWarnTextView;
    private TextView mNumberTextView;
    private TextView mQueueNameTextView;
    private TextView mSubqueueNameTextView;
    private TextView mInTimeTextView;
    private TextView mOutTimeTextView;
    private TextView mEstimatedTimeTextView;
    private TextView stateTextView;
    private Activity mActivity;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle
            savedInstanceState) {
        setHasOptionsMenu(true);
        View view = inflater.inflate(R.layout.fragment_queue_state, container, false);
        mLinearLayout = (LinearLayout) view.findViewById(R.id.queued_state_linearLayout);
        mWarnTextView = (TextView) view.findViewById(R.id.queued_state_warn);
        mNumberTextView = (TextView) view.findViewById(R.id.queue_state_number_textView);
        mQueueNameTextView = (TextView) view.findViewById(R.id.queue_state_queue_name_textView);
        mSubqueueNameTextView = (TextView) view.findViewById(R.id
                .queue_state_subqueue_name_textView);
        mInTimeTextView = (TextView) view.findViewById(R.id.queue_state_in_time_textView);
        mOutTimeTextView = (TextView) view.findViewById(R.id.queue_state_out_time_textView);
        mEstimatedTimeTextView = (TextView) view.findViewById(R.id
                .queue_state_estimated_time_textView);
        stateTextView = (TextView) view.findViewById(R.id.queue_state_state_textView);

        SharedPreferences defaultSharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(getActivity());
        int queuedQueue = defaultSharedPreferences
                .getInt(getString(R.string.queued_queue), -1);
        int queuedId = defaultSharedPreferences
                .getInt(getString(R.string.queued_id), -1);
        if (queuedQueue != -1 && queuedId != -1) {
            mWarnTextView.setVisibility(View.INVISIBLE);
            new GetQueuedStateTask().execute(queuedQueue,queuedId);
        } else {
            mLinearLayout.setVisibility(View.INVISIBLE);
        }

        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_queued_state, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.quit_queue) {
            SharedPreferences defaultSharedPreferences = PreferenceManager
                    .getDefaultSharedPreferences(getActivity());
            int queuedQueue = defaultSharedPreferences
                    .getInt(getString(R.string.queued_queue), -1);
            int queuedId = defaultSharedPreferences
                    .getInt(getString(R.string.queued_id), -1);
            if (queuedQueue!=-1&&queuedId!=-1) {
                new QuitQueueTask().execute(queuedQueue,queuedId);
            } else {
                Toast.makeText(getActivity(),"尚未加入队列，无法退出",Toast.LENGTH_SHORT).show();
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onAttach(Activity activity) {
        mActivity = activity;
        super.onAttach(activity);
    }

    private class GetQueuedStateTask extends AsyncTask<Integer, Void, QueuedState> {
        @Override
        protected QueuedState doInBackground(Integer... params) {
            return new DataFetcher(mActivity).fetchQueuedState(params[0], params[1]);
        }


        @Override
        protected void onPostExecute(QueuedState queuedState) {
            if (queuedState != null) {
                mNumberTextView.setText(String.valueOf(queuedState.getNumber()));
                mQueueNameTextView.setText(queuedState.getQueueName());
                mSubqueueNameTextView.setText(queuedState.getSubqueueName());
                mInTimeTextView.setText(queuedState.getInTime());
                mOutTimeTextView.setText(queuedState.getOutTime());
                mEstimatedTimeTextView.setText(queuedState.getEstimatedTimeString());
                stateTextView.setText(queuedState.getState());
            } else {
                Toast.makeText(mActivity, "获取信息失败，请重试", Toast.LENGTH_LONG).show();
            }
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
            } else {
                Toast.makeText(mActivity, "处理失败，请重试", Toast.LENGTH_LONG).show();
            }
        }
    }
}
