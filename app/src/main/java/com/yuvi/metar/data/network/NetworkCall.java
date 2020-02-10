package com.yuvi.metar.data.network;

import android.os.AsyncTask;
import android.text.TextUtils;

import com.yuvi.metar.utils.Constants;
import com.yuvi.metar.utils.Utils;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;

public class NetworkCall extends AsyncTask<Void, Void, String> {

    public interface OnDataReceiveListener {
        void networkCallComplete(String data, String url, String airport);
    }

    private String url, airport;
    OnDataReceiveListener listener;

    public NetworkCall(String url, String airport, OnDataReceiveListener listener) {
        this.url = url;
        this.listener = listener;
        this.airport = airport;
    }

    @Override
    protected String doInBackground(Void... voids) {
        try {
           return get(url);
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        Utils.debug(NetworkCall.class, String.format("fromServer = %s", result));
        if(listener != null) {
            listener.networkCallComplete(result, url, airport);
        }
    }

    private String get(String url) throws Exception {
        URL mUrl = new URL(url);
        HttpURLConnection conn = (HttpURLConnection) mUrl.openConnection();
        String responseFromServer = "";
        try {
            conn.setRequestMethod("GET");
            conn.setDoInput(true);
            conn.connect();
            InputStream is = new BufferedInputStream(mUrl.openStream());
            responseFromServer = Utils.convertStreamToString(is);
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        } finally {
            conn.disconnect();
        }
        return responseFromServer;
    }
}
