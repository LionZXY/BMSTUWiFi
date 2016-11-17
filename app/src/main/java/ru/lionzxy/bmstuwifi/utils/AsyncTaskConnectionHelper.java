package ru.lionzxy.bmstuwifi.utils;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.os.Handler;
import android.os.Message;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;

/**
 * Created by lionzxy on 06.11.16.
 */

public class AsyncTaskConnectionHelper extends AsyncTaskLoader<String> {
    public static abstract class ResponseInUI implements ConnectionHelper.IResponseInterface {
        public static final int MSGCODE_STATE = 0;
        public static final int MSGCODE_READY = 1;

        protected final Handler handler = new Handler() {

            public void handleMessage(Message msg) {
                if (msg != null && msg.obj != null)
                    switch (msg.what) {
                        case MSGCODE_STATE: {
                            onState((ConnectionHelper.ConnectionState) msg.obj);
                            break;
                        }
                        case MSGCODE_READY: {
                            onSucsesful((StringBuilder) msg.obj);
                        }
                    }

            }
        };
    }

    private ConnectionHelper.IResponseInterface responseInterface = null;
    private URL url;
    private HashMap<String, String> params;

    public AsyncTaskConnectionHelper(Context context, URL url, HashMap<String, String> params) {
        super(context);
        this.url = url;
        this.params = params;
    }

    public AsyncTaskConnectionHelper setResponseInterface(ConnectionHelper.IResponseInterface responseInterface) {
        this.responseInterface = responseInterface;
        return this;
    }

    @Override
    public String loadInBackground() {
        try {
            return ConnectionHelper.sendPostRequest(url, params, new ConnectionHelper.IResponseInterface() {
                @Override
                public void onSucsesful(StringBuilder answer) {
                    if (responseInterface != null)
                        if (responseInterface instanceof ResponseInUI)
                            ((ResponseInUI) responseInterface).handler.obtainMessage(ResponseInUI.MSGCODE_READY, answer);
                        else responseInterface.onSucsesful(answer);
                }

                @Override
                public void onState(ConnectionHelper.ConnectionState connectionState) {
                    if (responseInterface != null)
                        if (responseInterface instanceof ResponseInUI)
                            ((ResponseInUI) responseInterface).handler.obtainMessage(ResponseInUI.MSGCODE_STATE, connectionState);
                        else responseInterface.onState(connectionState);
                }
            }).toString();
        } catch (IOException e) {
        }
        return null;
    }


}
