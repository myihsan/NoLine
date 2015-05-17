package org.ihsan.android.noline;

import android.app.Activity;
import android.app.ListFragment;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by Ihsan on 15/5/6.
 */
public class QueuedHistoryFragment extends ListFragment {

    private static final int LOGIN = 1;

    private ListView mListView;
    private Button mButton;
    private ArrayList<Queued> mQueueds = new ArrayList<Queued>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle
            savedInstanceState) {
        getActivity().setTitle("排队记录");
        setHasOptionsMenu(true);
        View view = inflater.inflate(R.layout.fragment_queued_history, container, false);

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
        QueuedAdapter adapter = new QueuedAdapter(mQueueds);
        setListAdapter(adapter);

        int userId = PreferenceManager.getDefaultSharedPreferences(getActivity())
                .getInt(getString(R.string.logined_user_id), -1);
        if (userId == -1) {
            mListView.setVisibility(View.INVISIBLE);
        } else {
            mButton.setVisibility(View.INVISIBLE);
            loadQueued();
        }
    }

    private void loadQueued(){
        new FetchQueuedTask().execute();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            mListView.setVisibility(View.VISIBLE);
            mButton.setVisibility(View.INVISIBLE);
            loadQueued();
        }
    }


    public void updateAdapter() {
        ((QueuedAdapter) getListAdapter()).notifyDataSetChanged();
    }

    private class QueuedAdapter extends ArrayAdapter<Queued> {

        public QueuedAdapter(ArrayList<Queued> queueds) {
            super(getActivity(), 0, queueds);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = getActivity().getLayoutInflater()
                        .inflate(R.layout.list_item_queued, null);
            }

            Queued queued = getItem(position);

            ImageView imageView =
                    (ImageView) convertView.findViewById(R.id.queued_list_item_imageView);
            Picasso.with(getActivity()).load(getString(R.string.root_url) + queued.getQueueImage())
                    .error(R.drawable.placeholder)
                    .placeholder(R.drawable.placeholder)
                    .into(imageView);
            TextView queueNameTextView =
                    (TextView) convertView.findViewById(R.id.queued_list_item_name_textView);
            queueNameTextView.setText(queued.getQueueName());
            TextView dateTextView =
                    (TextView) convertView.findViewById(R.id.queued_list_item_date_textView);
            dateTextView.setText(queued.getDate());

            return convertView;
        }
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        Queued queued = mQueueds.get(position);
        Intent intent=new Intent(getActivity(), QueuedStateActivity.class);
        intent.putExtra(QueuedStateFragment.EXTRA_QUEUED_QUEUE,queued.getQueueId());
        intent.putExtra(QueuedStateFragment.EXTRA_QUEUED_ID,queued.getQueuedId());
        startActivity(intent);
    }

    private class FetchQueuedTask extends AsyncTask<Void, Void, ArrayList<Queued>> {

        @Override
        protected ArrayList<Queued> doInBackground(Void... params) {
            int userId = PreferenceManager
                    .getDefaultSharedPreferences(getActivity())
                    .getInt(getString(R.string.logined_user_id), -1);
            return new DataFetcher(getActivity()).fetchQueued(userId);
        }

        @Override
        protected void onPostExecute(ArrayList<Queued> queueds) {
            if (queueds != null) {
                mQueueds.clear();
                mQueueds.addAll(queueds);
                updateAdapter();
            } else {
                Toast.makeText(getActivity(), "获取失败，请重试", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
