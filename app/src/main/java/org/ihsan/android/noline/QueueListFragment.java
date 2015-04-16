package org.ihsan.android.noline;

import android.app.Activity;
import android.app.ListFragment;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Ihsan on 15/2/5.
 */
public class QueueListFragment extends ListFragment {
    private static final String TAG = "QueueListFragment";
    private static final int QUEUE_DETAIL = 1;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        QueueAdapter adapter = new QueueAdapter(QueueArray.get(getActivity()).getQueues());
        setListAdapter(adapter);
        new FetchQueueTask().execute();
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
            getActivity().finish();
        }
    }

    public void updateAdapter() {
        ((QueueAdapter) getListAdapter()).notifyDataSetChanged();
    }

    private class FetchQueueTask extends AsyncTask<Void, Void, ArrayList<Queue>> {
        @Override
        protected ArrayList<Queue> doInBackground(Void... params) {
            return new DataFetcher(getActivity()).fetchQueue();
        }

        @Override
        protected void onPostExecute(ArrayList<Queue> queues) {
            QueueArray.get(getActivity()).refreshQueues(queues);
            updateAdapter();
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

            TextView titleTextView =
                    (TextView) convertView.findViewById(R.id.queue_list_item_titleTextView);
            titleTextView.setText(queue.getTitle());

            return convertView;
        }
    }
}
