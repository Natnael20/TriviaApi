package com.example.Cortex.components.core;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.util.Log;
import android.view.View;

import com.example.Cortex.R;
import com.example.Cortex.util.ConnectionChecker;

/**
 * Real-time network banner.
 * Uses a NetworkCallback for instant updates + polling as a fallback.
 */
public class NetworkBanner {

    private static final String TAG = "NetBanner";
    private static final long POLL_INTERVAL_MS = 1000L;

    private final Activity activity;
    private final View banner;
    private final ConnectivityManager connectivityManager;
    private boolean registered = false;

    private final ConnectivityManager.NetworkCallback networkCallback =
        new ConnectivityManager.NetworkCallback() {
            @Override
            public void onAvailable(Network network) {
                Log.d(TAG, "onAvailable");
                activity.runOnUiThread(NetworkBanner.this::refresh);
            }

            @Override
            public void onLost(Network network) {
                Log.d(TAG, "onLost");
                activity.runOnUiThread(NetworkBanner.this::refresh);
            }
        };

    private final Runnable poller = new Runnable() {
        @Override
        public void run() {
            refresh();
            banner.postDelayed(this, POLL_INTERVAL_MS);
        }
    };

    public NetworkBanner(Activity activity) {
        this.activity = activity;
        this.banner = activity.findViewById(R.id.networkBannerInclude);
        this.connectivityManager =
            (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
    }

    /**
     * Starts listening. Call from onResume().
     */
    public void start() {
        refresh();
        if (connectivityManager != null && !registered) {
            try {
                connectivityManager.registerDefaultNetworkCallback(networkCallback);
                registered = true;
                Log.d(TAG, "Callback registered");
            } catch (Exception e) {
                Log.e(TAG, "Register failed", e);
            }
        }
        if (banner != null) {
            banner.post(poller);
            Log.d(TAG, "Poller started");
        }
    }

    /**
     * Stops listening. Call from onPause().
     */
    public void stop() {
        if (connectivityManager != null && registered) {
            try {
                connectivityManager.unregisterNetworkCallback(networkCallback);
            } catch (Exception ignored) { }
            registered = false;
        }
        if (banner != null) {
            banner.removeCallbacks(poller);
            Log.d(TAG, "Poller stopped");
        }
    }

    /**
     * Refreshes the banner visibility.
     */
    public void refresh() {
        if (banner == null) return;

        boolean connected = ConnectionChecker.isConnected(activity);
        Log.d(TAG, "refresh: connected = " + connected);

        banner.setVisibility(connected ? View.GONE : View.VISIBLE);
    }
}