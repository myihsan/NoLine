package org.ihsan.android.noline;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.ListFragment;
import android.app.SearchManager;
import android.app.SearchableInfo;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
    private SearchView mSearchView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivity().setTitle("附近商户");
        setHasOptionsMenu(true);

        QueueAdapter adapter = new QueueAdapter(QueueArray.get(getActivity()).getQueues());
        setListAdapter(adapter);

        getNearQueue();
    }

    private void getNearQueue() {
        LocationManager locationManager = (LocationManager) getActivity().getSystemService
                (Context.LOCATION_SERVICE);
        Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if (location != null) {
            double myLat = location.getLatitude();
            double myLng = location.getLongitude();

            double rangeLat = 180 / Math.PI / 6372.797;
            double rangeLng = rangeLat / Math.cos(myLat * Math.PI / 180.0);
            double minLat = myLat - rangeLat;
            double maxLat = myLat + rangeLat;
            double minLng = myLng - rangeLng;
            double maxLng = myLng + rangeLng;

//            Toast.makeText(getActivity(), minLat + "~" + maxLat + "," + minLng + "~" + maxLng,
// Toast.LENGTH_SHORT).show();
            new GetNearQueueTask().execute(minLat, maxLat, minLng, maxLng);
        } else {
//            Toast.makeText(getActivity(),"无法获取位置，请使用搜索",Toast.LENGTH_SHORT).show();
            new GetNearQueueTask().execute(40.074, 40.076, 116.28, 116.3);
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
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_queue_list, menu);

        // Pull out the SearchView
        final MenuItem searchItem = menu.findItem(R.id.action_search);
        mSearchView = (SearchView) searchItem.getActionView();

        // Get the data from our searchable.xml as a SearchableInfo
        SearchManager searchManager = (SearchManager) getActivity()
                .getSystemService(Context.SEARCH_SERVICE);
        ComponentName name = getActivity().getComponentName();
        SearchableInfo searchInfo = searchManager.getSearchableInfo(name);

        mSearchView.setSearchableInfo(searchInfo);
        LinearLayout LinearLayout = (LinearLayout) ((LinearLayout) ((LinearLayout) mSearchView
                .getChildAt(0)).getChildAt(2)).getChildAt(1);
        SearchView.SearchAutoComplete autoComplete = ((SearchView.SearchAutoComplete) LinearLayout
                .getChildAt(0));
        autoComplete.setHintTextColor(getResources().getColor(R.color.md_indigo_100));
        mSearchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                getNearQueue();
                return false;
            }
        });
    }

    public boolean isSearchViewIconified(){
        return mSearchView.isIconified();
    }

    public void setSearchViewIconified(boolean flag){
        mSearchView.setIconified(flag);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_search:
                getActivity().onSearchRequested();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            FragmentManager fragmentManager = getFragmentManager();
            Fragment fragment = new QueuedStateFragment();
            fragmentManager.beginTransaction()
                    .replace(R.id.fragmentContainer, fragment)
                    .commit();
            ((QueueListActivity) getActivity()).setDrawerSelection(1);
        }
    }

    public void updateAdapter() {
        ((QueueAdapter) getListAdapter()).notifyDataSetChanged();
    }

    public void searchQueue(String keyword) {
        new GetQueueByKeywordTask().execute(keyword);
    }

    private class GetNearQueueTask extends AsyncTask<Double, Void, ArrayList<Queue>> {
        @Override
        protected ArrayList<Queue> doInBackground(Double... params) {
            return new DataFetcher(getActivity()).fetchNearQueue(params[0], params[1], params[2],
                    params[3]);
        }

        @Override
        protected void onPostExecute(ArrayList<Queue> queues) {
            if (queues != null) {
                QueueArray.get(getActivity()).refreshQueues(queues);
                updateAdapter();
            } else {
                Toast.makeText(getActivity(), "获取失败，请重试", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private class GetQueueByKeywordTask extends AsyncTask<String, Void, ArrayList<Queue>> {
        @Override
        protected ArrayList<Queue> doInBackground(String... params) {
            return new DataFetcher(getActivity()).fetchQueueByKeyword(params[0]);
        }

        @Override
        protected void onPostExecute(ArrayList<Queue> queues) {
            if (queues != null) {
                QueueArray.get(getActivity()).refreshQueues(queues);
                updateAdapter();
                mSearchView.clearFocus();
            } else {
                Toast.makeText(getActivity(), "搜索失败，请重试", Toast.LENGTH_SHORT).show();
            }
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
}
