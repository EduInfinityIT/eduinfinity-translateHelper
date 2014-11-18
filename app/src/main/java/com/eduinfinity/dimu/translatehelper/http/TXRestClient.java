package com.eduinfinity.dimu.translatehelper.http;

/**
 * Created by Dimu on 10/23/14.
 */

import android.util.Log;

import com.eduinfinity.dimu.translatehelper.adapter.Center;
import com.loopj.android.http.*;

public class TXRestClient {
    public static final String TAG = "TXRestClient";
    private static final String BASE_URL = "https://www.transifex.com/api/2/project/";

    private static AsyncHttpClient client = new AsyncHttpClient();

    public static void get(final String url, final RequestParams params, final AsyncHttpResponseHandler responseHandler) {
        getUser(new UserHandler() {

            @Override
            public void getUserSuccess(String user, String password) {
                client.setBasicAuth(user, password);
                Log.i(TAG,user+password);
                client.get(getAbsoluteUrl(url), params, responseHandler);
            }
            @Override
            public void getUserFailed(String user, String password) {
                //TODO
            }
        });

    }

    public static void post(final String url, final RequestParams params, final AsyncHttpResponseHandler responseHandler) {
        getUser(new UserHandler() {
            @Override
            public void getUserSuccess(String user, String password) {
                client.setBasicAuth(user, password);
                client.post(getAbsoluteUrl(url), params, responseHandler);
            }
            @Override
            public void getUserFailed(String user, String password) {
                //TODO
            }
        });

    }

    private static String getAbsoluteUrl(final String relativeUrl) {
        return BASE_URL + relativeUrl;
    }

    public static void getUser(UserHandler userHandler) {
        String id = Center.getInstance().getID();
        String pw = Center.getInstance().getPassWord();
        if(id==null||pw==null||id.equals("")||pw.equals("")) Center.getInstance().requestUser(userHandler);
        else userHandler.getUserSuccess(id,pw);
    }

    public interface UserHandler {
        public void getUserSuccess(String user, String password);

        public void getUserFailed(String user, String password);
    }
}
