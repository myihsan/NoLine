package org.ihsan.android.noline;

import android.app.Activity;
import android.app.Fragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.tencent.android.tpush.XGPushConfig;

/**
 * Created by Ihsan on 15/2/5.
 */
public class QueueDetailFragment extends Fragment {
    public static final String TAG = "QueueDetailFragment";
    public static final String EXTRA_QUEUE_ID =
            "com.l3.android.ccbuptservice.queue_id";

    private TextView mNowTextView, mTotalTextView;

    private Queue mQueue;

    public static QueueDetailFragment newInstance(int id) {
        Bundle args = new Bundle();
        args.putInt(EXTRA_QUEUE_ID, id);

        QueueDetailFragment fragment = new QueueDetailFragment();
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int queueId = -1;
        if (getActivity() != null) {
            queueId = getArguments().getInt(EXTRA_QUEUE_ID);
        }
        if (queueId != -1) {
            mQueue = QueueArray.get(getActivity()).getQueue(queueId);
            ((ActionBarActivity) getActivity()).getSupportActionBar().setTitle(mQueue.getTitle());
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_queue_detail, container, false);

        mNowTextView = (TextView) view.findViewById(R.id.queue_detail_now_textView);
        mTotalTextView = (TextView) view.findViewById(R.id.queue_detail_total_textView);

        Button queueUpButton = (Button) view.findViewById(R.id.queue_detail_queue_up_button);
        queueUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new QueueUpTask().execute();
            }
        });

        new GetDetailTask().execute();

        return view;
    }

    private class GetDetailTask extends AsyncTask<Void, Void, Boolean> {
        @Override
        protected Boolean doInBackground(Void... params) {
            return new DataFetcher(getActivity()).fetchQueueDetail(mQueue);
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            if (aBoolean) {
                if (mQueue.getNextNumber() != 0) {
                    mNowTextView.setText(String.valueOf(mQueue.getNextNumber()));
                }
                mTotalTextView.setText(String.valueOf(mQueue.getTotal()));
            } else {
                Toast.makeText(getActivity(), "获取队列信息失败，请重试", Toast.LENGTH_LONG).show();
            }
        }
    }

    private class QueueUpTask extends AsyncTask<Void, Void, Integer> {
        @Override
        protected Integer doInBackground(Void... params) {
            int queueId = mQueue.getId();
            String token = XGPushConfig.getToken(getActivity());
            return new DataFetcher(getActivity()).fetchQueueUpResult(queueId, token);
        }

        @Override
        protected void onPostExecute(Integer integer) {

            if (integer != -1) {
                Toast.makeText(getActivity(), "加入成功", Toast.LENGTH_LONG).show();
                PreferenceManager.getDefaultSharedPreferences(getActivity())
                        .edit()
                        .putInt(getString(R.string.queued_number), integer)
                        .putInt(getString(R.string.queued_queue), mQueue.getId())
                        .commit();
                getActivity().setResult(Activity.RESULT_OK);
                getActivity().finish();
            } else {
                Toast.makeText(getActivity(), "加入失败，请重试", Toast.LENGTH_LONG).show();
            }
        }

    }
}
