package org.ihsan.android.noline;

import android.app.Activity;
import android.app.ListFragment;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

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

        LocationManager locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if (location != null) {
            double myLat = location.getLatitude();
            double myLng = location.getLongitude();

            double range = 180 / Math.PI * 1 / 6372.797;
            double lngR = range / Math.cos(myLat * Math.PI / 180.0);
            double minLat = myLat - range;
            double maxLat = myLat + range;
            double minLng = myLng - lngR;
            double maxLng = myLng + lngR;

//            Toast.makeText(getActivity(), minLat + "~" + maxLat + "," + minLng + "~" + maxLng, Toast.LENGTH_SHORT).show();
            new FetchQueueTask().execute(minLat,maxLat,minLng,maxLng);
        } else {
            Toast.makeText(getActivity(),"无法获取位置，请使用搜索",Toast.LENGTH_SHORT).show();
        }
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

    private class FetchQueueTask extends AsyncTask<Double, Void, ArrayList<Queue>> {
        @Override
        protected ArrayList<Queue> doInBackground(Double... params) {
            return new DataFetcher(getActivity()).fetchQueue(params[0],params[1],params[2],params[3]);
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

            ImageView imageView = (ImageView) convertView.findViewById(R.id.queue_list_item_imageView);
            Picasso.with(getActivity()).load(getString(R.string.root_url) + queue.getImage())
                    .error(R.drawable.placeholder)
                    .placeholder(R.drawable.placeholder)
                    .into(imageView);
            TextView titleTextView =
                    (TextView) convertView.findViewById(R.id.queue_list_item_nameTextView);
            titleTextView.setText(queue.getName());
            RatingBar ratingBar = (RatingBar) convertView.findViewById(R.id.queue_list_item_ratingBar);
            ratingBar.setRating(queue.getRating());

            return convertView;
        }
    }
}
