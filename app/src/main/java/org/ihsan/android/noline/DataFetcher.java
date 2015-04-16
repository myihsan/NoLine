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

    public DataFetcher(Context context){
        mContext=context;
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

    public ArrayList<Queue> fetchQueue() {
        ArrayList<Queue> queues = new ArrayList<Queue>();

        String fetchUrl = mContext.getString(R.string.root_url)+"queue.php";
        String url = Uri.parse(fetchUrl).buildUpon().build().toString();
        Log.d(TAG, url);
        try {
            String jsonString = getUrl(url);
            Log.i(TAG, jsonString);
            parseQueues(queues, jsonString);
        } catch (IOException ioe) {
            Log.e(TAG, "Failed to fetch URL: ", ioe);
        } catch (JSONException jsone) {
            Log.e(TAG, "Failed to parse notices", jsone);
        }
        return queues;
    }

    private void parseQueues(ArrayList<Queue> queues, String jsonString) throws JSONException, IOException {
        JSONArray itemsArray = new JSONArray(jsonString);
        for (int i = 0; i < itemsArray.length(); i++) {
            Queue queue = new Queue(itemsArray.getJSONObject(i));
            queues.add(queue);
        }
    }

    public boolean fetchQueueDetail(Queue queue){
        String fetchUrl = mContext.getString(R.string.root_url)+"getqueuedetail.php";
        String url = Uri.parse(fetchUrl).buildUpon()
                .appendQueryParameter("queueId", String.valueOf(queue.getId()))
                .build().toString();
        try {
            String result = getUrl(url);
            JSONObject jsonObject = new JSONObject(result);
            queue.setNextNumber(jsonObject.getInt("nextNumber"));
            queue.setTotal(jsonObject.getInt("total"));
        } catch (IOException ioe) {
            Log.e(TAG, "Failed to fetch URL: ", ioe);
            return false;
        } catch (JSONException jsone) {
            Log.e(TAG, "Failed to parse detail", jsone);
            return false;
        }
        return true;
    }

    public int fetchNowQueuer(int queueId) {
        if (queueId != -1) {
            String result = null;
            String fetchUrl = mContext.getString(R.string.root_url) + "getnowqueuer.php";
            String url = Uri.parse(fetchUrl).buildUpon()
                    .appendQueryParameter("queueId", String.valueOf(queueId))
                    .build().toString();
            Log.d(TAG, url);
            try {
                result = getUrl(url);
                return Integer.valueOf(result);
            } catch (IOException ioe) {
                Log.e(TAG, "Failed to fetch URL: ", ioe);
                return -1;
            } catch (NumberFormatException nfe) {
                return -1;
            }
        }
        return -1;
    }

    public boolean fetchQuitQueueResult(int queueId,int number,String token) {
        boolean flag = false;
        String fetchUrl = mContext.getString(R.string.root_url) + "quitqueue.php";
        String url = Uri.parse(fetchUrl).buildUpon()
                .appendQueryParameter("queueId", String.valueOf(queueId))
                .appendQueryParameter("number", String.valueOf(number))
                .appendQueryParameter("token", token)
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

    public int fetchQueueUpResult(int queueId, String token) {
        String fetchUrl = mContext.getString(R.string.root_url) + "queueup.php";
        String url = Uri.parse(fetchUrl).buildUpon()
                .appendQueryParameter("queueId", String.valueOf(queueId))
                .appendQueryParameter("token", token)
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
}
