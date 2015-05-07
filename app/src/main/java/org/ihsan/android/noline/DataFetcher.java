package org.ihsan.android.noline;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by Ihsan on 15/1/23.
 */
public class DataFetcher {
    private static final String TAG = "DataFetcher";
    private Context mContext;

    public DataFetcher(Context context) {
        mContext = context;
    }

    private byte[] getUrlBytes(String urlSpec) throws IOException {
        URL url = new URL(urlSpec);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            InputStream in = connection.getInputStream();

            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                return null;
            }

            int bytesRead = 0;
            byte[] buffer = new byte[1024];
            while ((bytesRead = in.read(buffer)) > 0) {
                out.write(buffer, 0, bytesRead);
            }
            out.close();
            return out.toByteArray();
        } finally {
            connection.disconnect();
        }
    }

    public String getUrl(String urlSpec) throws IOException {
        return new String(getUrlBytes(urlSpec));
    }

    public ArrayList<Queue> fetchNearQueue(double minLat, double maxLat, double minLng, double
            maxLng) {
        ArrayList<Queue> queues = new ArrayList<Queue>();

        String fetchUrl = mContext.getString(R.string.root_url) + "getqueue.php";
        String url = Uri.parse(fetchUrl).buildUpon()
                .appendQueryParameter("minLat", String.valueOf(minLat))
                .appendQueryParameter("maxLat", String.valueOf(maxLat))
                .appendQueryParameter("minLng", String.valueOf(minLng))
                .appendQueryParameter("maxLng", String.valueOf(maxLng))
                .build().toString();
        Log.d(TAG, url);
        try {
            String jsonString = getUrl(url);
            Log.i(TAG, jsonString);
            parseQueues(queues, jsonString);
        } catch (IOException ioe) {
            Log.e(TAG, "Failed to fetch URL: ", ioe);
        } catch (JSONException jsone) {
            Log.e(TAG, "Failed to parse result", jsone);
        }
        return queues;
    }

    public ArrayList<Queue> fetchQueueByKeyword(String keyword) {
        ArrayList<Queue> queues = new ArrayList<Queue>();

        String fetchUrl = mContext.getString(R.string.root_url) + "getqueue.php";
        String url = Uri.parse(fetchUrl).buildUpon()
                .appendQueryParameter("keyword", keyword)
                .build().toString();
        Log.d(TAG, url);
        try {
            String jsonString = getUrl(url);
            Log.i(TAG, jsonString);
            parseQueues(queues, jsonString);
        } catch (IOException ioe) {
            Log.e(TAG, "Failed to fetch URL: ", ioe);
        } catch (JSONException jsone) {
            Log.e(TAG, "Failed to parse result", jsone);
        }
        return queues;
    }

    private void parseQueues(ArrayList<Queue> queues, String jsonString) throws JSONException,
            IOException {
        JSONArray itemsArray = new JSONArray(jsonString);
        for (int i = 0; i < itemsArray.length(); i++) {
            Queue queue = new Queue(itemsArray.getJSONObject(i));
            queues.add(queue);
        }
    }

    public ArrayList<Subqueue> fetchQueueDetail(Queue queue) {
        String fetchUrl = mContext.getString(R.string.root_url) + "getqueuedetail.php";
        String url = Uri.parse(fetchUrl).buildUpon()
                .appendQueryParameter("queueId", String.valueOf(queue.getId()))
                .build().toString();
        ArrayList<Subqueue> subqueues = new ArrayList<Subqueue>();
        try {
            String result = getUrl(url);
            JSONObject jsonObject = new JSONObject(result);
            queue.setAddress(jsonObject.getString("address"));
            JSONArray subqueueNames = jsonObject.getJSONArray("subqueueNames");
            JSONArray subqueueSizes = jsonObject.getJSONArray("subqueueSizes");
            JSONArray subqueueTotals = jsonObject.getJSONArray("subqueueTotals");
            JSONArray subqueueFirstNumbers = jsonObject.getJSONArray("subqueueFirstNumbers");
            JSONArray subqueueEstimatedTime = jsonObject.getJSONArray("subqueueEstimatedTime");
            for (int i = 0; i < subqueueNames.length(); i++) {
                Subqueue subqueue = new Subqueue();
                subqueue.setName(subqueueNames.getString(i));
                subqueue.setSize(subqueueSizes.getInt(i));
                subqueue.setTotal(subqueueTotals.getInt(i));
                subqueue.setFirstNumber(subqueueFirstNumbers.getInt(i));
                subqueue.setEstimatedTime(subqueueEstimatedTime.getInt(i));
                subqueues.add(subqueue);
            }
        } catch (IOException ioe) {
            Log.e(TAG, "Failed to fetch URL: ", ioe);
            return null;
        } catch (JSONException jsone) {
            Log.e(TAG, "Failed to parse result", jsone);
            return null;
        }
        return subqueues;
    }

    public QueuedState fetchQueuedState(int queueId, int queuedId) {
        QueuedState queuedState = null;
        if (queueId != -1 && queuedId != -1) {
            String result = null;
            String fetchUrl = mContext.getString(R.string.root_url) + "getqueuedstate.php";
            String url = Uri.parse(fetchUrl).buildUpon()
                    .appendQueryParameter("queueId", String.valueOf(queueId))
                    .appendQueryParameter("queuedId", String.valueOf(queuedId))
                    .build().toString();
            try {
                result = getUrl(url);
                queuedState = new QueuedState(new JSONObject(result));
            } catch (IOException ioe) {
                Log.e(TAG, "Failed to fetch URL: ", ioe);
            } catch (JSONException jsone) {
                Log.e(TAG, "Failed to parse result", jsone);
            }
            return queuedState;
        }
        return queuedState;
    }

    public boolean fetchQuitQueueResult(int queuedQueue, int queuedId) {
        boolean flag = false;
        String fetchUrl = mContext.getString(R.string.root_url) + "quitqueue.php";
        String url = Uri.parse(fetchUrl).buildUpon()
                .appendQueryParameter("queuedQueue", String.valueOf(queuedQueue))
                .appendQueryParameter("queuedId", String.valueOf(queuedId))
                .build().toString();
        Log.d(TAG, url);
        try {
            String result = getUrl(url);
            Log.d(TAG, result);
            if (result.equals("succeed")) {
                flag = true;
            }
        } catch (IOException ioe) {
            Log.e(TAG, "Failed to fetch URL: ", ioe);
        }
        return flag;
    }

    public int fetchQueueUpResult(int queueId, int subqueueNumber, String token,int userId) {
        String fetchUrl = mContext.getString(R.string.root_url) + "queueup.php";
        String url = Uri.parse(fetchUrl).buildUpon()
                .appendQueryParameter("queueId", String.valueOf(queueId))
                .appendQueryParameter("subqueueNumber", String.valueOf(subqueueNumber))
                .appendQueryParameter("token", token)
                .appendQueryParameter("userId", String.valueOf(userId))
                .build().toString();
        try {
            String result = getUrl(url);
            Log.d(TAG, result);
            return Integer.valueOf(result);
        } catch (IOException ioe) {
            Log.e(TAG, "Failed to fetch URL: ", ioe);
        } catch (NumberFormatException nfe) {
            return -1;
        }
        return -1;
    }

    public int fetchLoginResult(String username, String password) {
        int queueId=-1;
        String loginUrl = mContext.getString(R.string.root_url) + "login.php";
        String url = Uri.parse(loginUrl).buildUpon()
                .appendQueryParameter("username", username)
                .appendQueryParameter("password", password)
                .appendQueryParameter("identity", "user")
                .build().toString();
        Log.d(TAG, url);

        String jsonString;
        try {
            jsonString = getUrl(url);
            JSONObject jsonObject = new JSONObject(jsonString);
            queueId = jsonObject.getInt("queueId");
        } catch (IOException ioe) {
            Log.e(TAG, "Failed to fetch URL: ", ioe);
            return -2;
        } catch (JSONException jsone) {
            Log.e(TAG, "Failed to parse result", jsone);
        }
        Log.d(TAG, queueId+"");
        return queueId;
    }

    public ArrayList<Queued> fetchQueued(int userId) {
        String fetchUrl = mContext.getString(R.string.root_url) + "getqueuedhistory.php";
        String url = Uri.parse(fetchUrl).buildUpon()
                .appendQueryParameter("userId", String.valueOf(userId))
                .build().toString();
        ArrayList<Queued> queueds = new ArrayList<Queued>();
        try {
            String result = getUrl(url);
            Log.d(TAG, result);
            JSONArray jsonArray = new JSONArray(result);
            for (int i = 0; i < jsonArray.length(); i++) {
                Queued queued = new Queued(jsonArray.getJSONObject(i));
                queueds.add(queued);
            }
        } catch (IOException ioe) {
            Log.e(TAG, "Failed to fetch URL: ", ioe);
            return null;
        } catch (JSONException jsone) {
            Log.e(TAG, "Failed to parse result", jsone);
            return null;
        }
        return queueds;
    }
}
