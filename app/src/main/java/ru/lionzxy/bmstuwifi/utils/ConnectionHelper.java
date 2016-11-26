package ru.lionzxy.bmstuwifi.utils;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

import ru.lionzxy.bmstuwifi.R;

/**
 * Created by lionzxy on 06.11.16.
 */

public class ConnectionHelper {
    private static final String TAG = "ConnectHelper";
    public static enum ConnectionState {
        START_CONNECTION(R.string.auth_conn),
        OPEN_CONNECTION(R.string.auth_open),
        SEND_POST_DATA(R.string.auth_send_data),
        GET_DATA(R.string.auth_get_data);
        private final int resId;

        ConnectionState(int resId) {
            this.resId = resId;
        }

        public int getResId() {
            return resId;
        }
    }

    public static interface IResponseInterface {
        public void onSucsesful(StringBuilder answer);

        public void onState(ConnectionState connectionState);
    }

    public static StringBuilder sendPostRequest(URL url, HashMap<String, String> params, IResponseInterface responseInterface) throws IOException {
        Logger.getLogger().log(TAG, Logger.Level.DEBUG, "Обрашение к URL " + url);

        responseInterface.onState(ConnectionState.START_CONNECTION);
        StringBuilder postData = new StringBuilder();
        if (params != null)
            for (Map.Entry<String, String> param : params.entrySet()) {
                if (postData.length() != 0) postData.append('&');
                postData.append(URLEncoder.encode(param.getKey(), "UTF-8"));
                postData.append('=');
                postData.append(URLEncoder.encode(String.valueOf(param.getValue()), "UTF-8"));
            }
        byte[] postDataBytes = postData.toString().getBytes("UTF-8");

        responseInterface.onState(ConnectionState.OPEN_CONNECTION);
        HttpsURLConnection httpsURLConnection = (HttpsURLConnection) url.openConnection();

        httpsURLConnection.setDoOutput(true);
        httpsURLConnection.setRequestMethod("POST");
        httpsURLConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        httpsURLConnection.setRequestProperty("charset", "UTF-8");
        httpsURLConnection.setRequestProperty("Content-Length", Integer.toString(postDataBytes.length));

        responseInterface.onState(ConnectionState.SEND_POST_DATA);
        DataOutputStream wr = new DataOutputStream(httpsURLConnection.getOutputStream());
        try {
            wr.write(postDataBytes);
            wr.flush();
        } finally {
            wr.close();
        }

        responseInterface.onState(ConnectionState.GET_DATA);
        DataInputStream is = new DataInputStream(httpsURLConnection.getInputStream());
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        StringBuilder stringBuilder = new StringBuilder();
        try {
            String line = null;
            while ((line = br.readLine()) != null) {
                stringBuilder.append(line);
            }
            responseInterface.onSucsesful(stringBuilder);
        } finally {
            br.close();
        }

        return stringBuilder;
    }
}
