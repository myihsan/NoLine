package org.ihsan.android.noline;

import android.app.Activity;
import android.app.Fragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.tencent.android.tpush.XGPushConfig;

import java.util.ArrayList;

/**
 * Created by Ihsan on 15/2/5.
 */
public class QueueDetailFragment extends Fragment {
    public static final String TAG = "QueueDetailFragment";
    public static final String EXTRA_QUEUE_ID =
            "com.l3.android.ccbuptservice.queue_id";

    private TextView mNameTextView;
    private RatingBar mRatingBar;
    private TextView mAddressTextView;
    private LinearLayout mSubqueueLinearLayout;

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
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_queue_detail, container, false);

        mNameTextView = (TextView) view.findViewById(R.id.queue_detail_nameTextView);
        mRatingBar = (RatingBar) view.findViewById(R.id.queue_detail_ratingBar);
        mAddressTextView = (TextView) view.findViewById(R.id.queue_detail_address);
        mSubqueueLinearLayout = (LinearLayout) view.findViewById(R.id.subqueue_linearLayout);

        mNameTextView.setText(mQueue.getName());
        mRatingBar.setRating(mQueue.getRating());

//        Button queueUpButton = (Button) view.findViewById(R.id.queue_detail_queue_up_button);
//        queueUpButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                new QueueUpTask().execute();
//            }
//        });

        new GetDetailTask().execute();

        return view;
    }

    private class GetDetailTask extends AsyncTask<Void, Void, ArrayList<Subqueue>> {
        @Override
        protected ArrayList<Subqueue> doInBackground(Void... params) {
            return new DataFetcher(getActivity()).fetchQueueDetail(mQueue);
        }

        @Override
        protected void onPostExecute(ArrayList<Subqueue> subqueues) {
            if (subqueues != null) {
                mAddressTextView.setText(String.valueOf(mQueue.getAddress()));
                for (Subqueue subqueue : subqueues) {
                    View subqueueView = getActivity().getLayoutInflater().inflate(R.layout.table_item_subqueue, mSubqueueLinearLayout, false);
                    TextView subqueueNameTextView = (TextView) subqueueView.findViewById(R.id.subqueue_name_textView);
                    TextView subqueueSizeTextView = (TextView) subqueueView.findViewById(R.id.subqueue_size_textView);
                    TextView subqueueTotalTextView = (TextView) subqueueView.findViewById(R.id.subqueue_total_textView);
                    TextView subqueueFirstNumberTextView = (TextView) subqueueView.findViewById(R.id.subqueue_first_number_textView);
                    subqueueNameTextView.setText(subqueue.getName());
                    subqueueSizeTextView.setText(String.valueOf(subqueue.getSize()));
                    subqueueTotalTextView.setText(String.valueOf(subqueue.getTotal()));
                    subqueueFirstNumberTextView.setText(String.valueOf(subqueue.getFirstNumber()));
                    mSubqueueLinearLayout.addView(subqueueView);
                }
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
