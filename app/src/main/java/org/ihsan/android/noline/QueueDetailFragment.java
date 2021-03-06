package org.ihsan.android.noline;

import android.app.Activity;
import android.app.Fragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.NavUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.squareup.picasso.Picasso;
import com.tencent.android.tpush.XGPushConfig;

import java.util.ArrayList;

/**
 * Created by Ihsan on 15/2/5.
 */
public class QueueDetailFragment extends Fragment {
    public static final String TAG = "QueueDetailFragment";
    public static final String EXTRA_QUEUE = "org.ihsan.android.noline.queue";

    private ImageView mImageView;
    private TextView mNameTextView;
    private RatingBar mRatingBar;
    private TextView mAddressTextView;
    private LinearLayout mSubqueueLinearLayout;
    private MenuItem mFavoriteMenuItem;

    private Queue mQueue;
    private int mUserId;
    private boolean mIsFavorite = false;

    public static QueueDetailFragment newInstance(Queue queue) {
        Bundle args = new Bundle();
        args.putSerializable(EXTRA_QUEUE, queue);

        QueueDetailFragment fragment = new QueueDetailFragment();
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Queue queue = null;
        if (getActivity() != null) {
            queue = (Queue) getArguments().getSerializable(EXTRA_QUEUE);
        }
        if (queue != null) {
            mQueue = queue;
            getActivity().setTitle("商户详情");
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle
            savedInstanceState) {
        setHasOptionsMenu(true);
        View view = inflater.inflate(R.layout.fragment_queue_detail, container, false);

        mImageView = (ImageView) view.findViewById(R.id.queue_detail_imageView);
        mNameTextView = (TextView) view.findViewById(R.id.queue_detail_nameTextView);
        mRatingBar = (RatingBar) view.findViewById(R.id.queue_detail_ratingBar);
        mAddressTextView = (TextView) view.findViewById(R.id.queue_detail_address);
        mSubqueueLinearLayout = (LinearLayout) view.findViewById(R.id.subqueue_linearLayout);

        Picasso.with(getActivity()).load(getString(R.string.root_url) + mQueue.getImage())
                .error(R.drawable.placeholder)
                .placeholder(R.drawable.placeholder)
                .into(mImageView);
        mNameTextView.setText(mQueue.getName());
        mRatingBar.setRating(mQueue.getRating());

        new GetDetailTask().execute();

        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        mUserId = PreferenceManager.getDefaultSharedPreferences(getActivity())
                .getInt(getString(R.string.logined_user_id), -1);
        if (mUserId != -1) {
            inflater.inflate(R.menu.menu_queue_detail, menu);
            mFavoriteMenuItem = menu.findItem(R.id.action_favorite);
            new CheckFavoriteTask().execute(mUserId);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_favorite:
                new ToggleFavoriteTask().execute();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private class GetDetailTask extends AsyncTask<Void, Void, ArrayList<Subqueue>> {
        @Override
        protected ArrayList<Subqueue> doInBackground(Void... params) {
            return new DataFetcher(getActivity()).fetchQueueDetail(mQueue);
        }

        @Override
        protected void onPostExecute(final ArrayList<Subqueue> subqueues) {
            if (subqueues != null) {
                mAddressTextView.setText(String.valueOf(mQueue.getAddress()));
                mAddressTextView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                });
                for (final Subqueue subqueue : subqueues) {
                    View subqueueView = getActivity().getLayoutInflater().inflate(R.layout
                            .table_item_subqueue, mSubqueueLinearLayout, false);
                    TextView subqueueNameTextView = (TextView) subqueueView.findViewById(R.id
                            .subqueue_name_textView);
                    TextView subqueueSizeTextView = (TextView) subqueueView.findViewById(R.id
                            .subqueue_size_textView);
                    TextView subqueueTotalTextView = (TextView) subqueueView.findViewById(R.id
                            .subqueue_total_textView);
                    TextView subqueueFirstNumberTextView = (TextView) subqueueView.findViewById(R
                            .id.subqueue_first_number_textView);
                    subqueueNameTextView.setText(subqueue.getName());
                    subqueueSizeTextView.setText(String.valueOf(subqueue.getSize()));
                    subqueueTotalTextView.setText(String.valueOf(subqueue.getTotal()));
                    subqueueFirstNumberTextView.setText(String.valueOf(subqueue.getFirstNumber()));
                    int estimatedTime = subqueue.getEstimatedTime();
                    subqueueView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            new MaterialDialog.Builder(getActivity())
                                    .title("是否加入" + subqueue.getName() + "队列")
                                    .content("现在还需等待" + subqueue.getTotal() + "位" + subqueue
                                            .getEstimatedTimeString())
                                    .positiveText("确定")
                                    .negativeText("取消")
                                    .callback(new MaterialDialog.ButtonCallback() {
                                        @Override
                                        public void onPositive(MaterialDialog dialog) {
                                            if (!PreferenceManager.getDefaultSharedPreferences
                                                    (getActivity()).contains(getString(R
                                                    .string.queued_queue))) {
                                                new QueueUpTask().execute(subqueues.indexOf
                                                        (subqueue));
                                            } else {
                                                Toast.makeText(getActivity(), "正在排队，无法加入新队列", Toast.LENGTH_LONG).show();
                                            }
                                        }
                                    })
                                    .show();
                        }
                    });
                    mSubqueueLinearLayout.addView(subqueueView);
                }
            } else {
                Toast.makeText(getActivity(), "获取队列信息失败，请重试", Toast.LENGTH_LONG).show();
            }
        }
    }

    private class QueueUpTask extends AsyncTask<Integer, Void, Integer> {

        @Override
        protected Integer doInBackground(Integer... params) {
            int queueId = mQueue.getId();
            String token = XGPushConfig.getToken(getActivity());
            return new DataFetcher(getActivity()).fetchQueueUpResult(queueId, params[0], token,
                    mUserId);
        }

        @Override
        protected void onPostExecute(Integer integer) {

            if (integer != -1) {
                Toast.makeText(getActivity(), "加入成功", Toast.LENGTH_LONG).show();
                PreferenceManager.getDefaultSharedPreferences(getActivity())
                        .edit()
                        .putInt(getString(R.string.queued_id), integer)
                        .putInt(getString(R.string.queued_queue), mQueue.getId())
                        .commit();
                getActivity().setResult(Activity.RESULT_OK);
                getActivity().finish();
            } else {
                Toast.makeText(getActivity(), "加入失败，请重试", Toast.LENGTH_LONG).show();
            }
        }

    }

    private class CheckFavoriteTask extends AsyncTask<Integer, Void, Integer> {

        @Override
        protected Integer doInBackground(Integer... params) {
            int queueId = mQueue.getId();
            return new DataFetcher(getActivity()).fetchIsFavorite(params[0], queueId);
        }

        @Override
        protected void onPostExecute(Integer integer) {
            {
                if (integer == 1) {
                    mFavoriteMenuItem.setIcon(R.drawable.ic_favorite_white_24dp);
                    mIsFavorite = true;
                } else if (integer == -1) {
                    Toast.makeText(getActivity(), "获取收藏状态失败", Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    private class ToggleFavoriteTask extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected Boolean doInBackground(Void... params) {
            int queueId = mQueue.getId();
            return new DataFetcher(getActivity()).fetchToggleFavoriteResult(mUserId, queueId,
                    mIsFavorite);
        }

        @Override
        protected void onPostExecute(Boolean aboolean) {
            {
                if (aboolean) {
                    mIsFavorite = !mIsFavorite;
                    if (mIsFavorite) {
                        mFavoriteMenuItem.setIcon(R.drawable.ic_favorite_white_24dp);
                    } else {
                        mFavoriteMenuItem.setIcon(R.drawable.ic_favorite_outline_white_24dp);
                    }
                } else {
                    Toast.makeText(getActivity(), "修改收藏状态失败，请重试", Toast.LENGTH_LONG).show();
                }
            }
        }
    }
}
