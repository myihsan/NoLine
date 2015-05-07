package org.ihsan.android.noline;

import android.app.Activity;
import android.app.ListFragment;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by Ihsan on 15/5/7.
 */
public class FavoriteQueueFragment extends ListFragment {
    private static final int QUEUE_DETAIL = 1;
    private static final int LOGIN = 1;

    private ListView mListView;
    private Button mButton;
    private ArrayList<Queue> mQueues = new ArrayList<Queue>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle
            savedInstanceState) {
        getActivity().setTitle("我的收藏");
        View view=inflater.inflate(R.layout.fragment_favorite_queue,container,false);

        mButton = (Button) view.findViewById(R.id.button);
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                startActivityForResult(intent, LOGIN);
            }
        });

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mListView = getListView();
        QueueAdapter adapter = new QueueAdapter(mQueues);
        setListAdapter(adapter);

        int userId = PreferenceManager.getDefaultSharedPreferences(getActivity())
                .getInt(getString(R.string.logined_user_id), -1);
        if (userId == -1) {
            mListView.setVisibility(View.INVISIBLE);
        } else {
            mButton.setVisibility(View.INVISIBLE);
            loadQueue();
        }
    }

    private void loadQueue(){
        new FetchFavoriteQueueTask().execute();
    }

    public void updateAdapter() {
        ((QueueAdapter) getListAdapter()).notifyDataSetChanged();
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        Intent intent = new Intent(getActivity(), QueueDetailActivity.class);
        int queueId = ((QueueAdapter) getListAdapter()).getItem(position).getId();
        intent.putExtra(QueueDetailFragment.EXTRA_QUEUE_ID, queueId);
        startActivityForResult(intent, QUEUE_DETAIL);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            mListView.setVisibility(View.VISIBLE);
            mButton.setVisibility(View.INVISIBLE);
            loadQueue();
        } else if (requestCode==QUEUE_DETAIL){
            loadQueue();
        }
    }

    private class QueueAdapter extends ArrayAdapter<Queue> {
        public QueueAdapter(ArrayList<Queue> queues) {
            super(getActivity(), 0, queues);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = getActivity().getLayoutInflater()
                        .inflate(R.layout.list_item_queue, null);
            }

            Queue queue = getItem(position);

            ImageView imageView = (ImageView) convertView.findViewById(R.id
                    .queue_list_item_imageView);
            Picasso.with(getActivity()).load(getString(R.string.root_url) + queue.getImage())
                    .error(R.drawable.placeholder)
                    .placeholder(R.drawable.placeholder)
                    .into(imageView);
            TextView titleTextView =
                    (TextView) convertView.findViewById(R.id.queue_list_item_name_textView);
            titleTextView.setText(queue.getName());
            RatingBar ratingBar = (RatingBar) convertView.findViewById(R.id
                    .queue_list_item_ratingBar);
            ratingBar.setRating(queue.getRating());

            return convertView;
        }
    }

    private class FetchFavoriteQueueTask extends AsyncTask<Void, Void, ArrayList<Queue>> {
        @Override
        protected ArrayList<Queue> doInBackground(Void... params) {
            int userId = PreferenceManager
                    .getDefaultSharedPreferences(getActivity())
                    .getInt(getString(R.string.logined_user_id), -1);
            return new DataFetcher(getActivity()).fetchFavoriteQueue(userId);
        }

        @Override
        protected void onPostExecute(ArrayList<Queue> queues) {
            if (queues != null) {
                mQueues.clear();
                mQueues.addAll(queues);
                updateAdapter();
            } else {
                Toast.makeText(getActivity(), "获取失败，请重试", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
