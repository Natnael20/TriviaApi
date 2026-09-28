package com.example.Cortex.manager;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.View;

import com.example.Cortex.listener.EdgeLightningListener;
import com.example.Cortex.components.core.EdgeLightningView;
import com.example.Cortex.Enum.Event;

import java.util.ArrayList;
import java.util.List;

public class EdgeLightningManager {

    private static final String PREFS = "cortex_prefs";
    private static final String KEY_ENABLED = "edge_lightning_enabled";

    private static final int COLOR_NEUTRAL = 0xFF00D9FF;
    private static final int COLOR_CORRECT = 0xFF00E676;
    private static final int COLOR_WRONG   = 0xFFFF4757;
    private static final int COLOR_TIMEUP  = 0xFFFF4757;

    private static final float INTENSITY_NEUTRAL = 0.6f;
    private static final float INTENSITY_STRONG  = 1.0f;

    private static EdgeLightningManager instance;

    private final SharedPreferences prefs;
    private final List<EdgeLightningListener> listeners = new ArrayList<>();
    private boolean enabled;

    private EdgeLightningManager(Context context) {
        this.prefs = context.getApplicationContext()
            .getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        this.enabled = prefs.getBoolean(KEY_ENABLED, false);
    }

    public static synchronized EdgeLightningManager getInstance(Context context) {
        if (instance == null) {
            instance = new EdgeLightningManager(context);
        }
        return instance;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean on) {
        if (this.enabled == on) return;
        this.enabled = on;
        prefs.edit().putBoolean(KEY_ENABLED, on).apply();
        for (EdgeLightningListener l : new ArrayList<>(listeners)) {
            l.onEdgeLightningChanged(on);
        }
    }

    public void toggle() {
        setEnabled(!enabled);
    }

    public void addListener(EdgeLightningListener listener) {
        if (!listeners.contains(listener)) listeners.add(listener);
    }

    public void removeListener(EdgeLightningListener listener) {
        listeners.remove(listener);
    }

    public void applyEvent(EdgeLightningView view, Event event) {
        if (!enabled || view == null) return;

        view.setVisibility(View.VISIBLE);

        switch (event) {
            case NEUTRAL:
            case NEW_QUESTION:
                view.setGlowColor(COLOR_NEUTRAL);
                view.setIntensity(INTENSITY_NEUTRAL);
                break;

            case CORRECT:
                view.setGlowColor(COLOR_CORRECT);
                view.setIntensity(INTENSITY_STRONG);
                break;

            case WRONG:
                view.setGlowColor(COLOR_WRONG);
                view.setIntensity(INTENSITY_STRONG);
                break;

            case TIME_UP:
                view.setGlowColor(COLOR_TIMEUP);
                view.setIntensity(INTENSITY_STRONG);
                break;
        }
    }
}