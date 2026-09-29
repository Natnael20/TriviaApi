package com.example.Cortex.components.core;

import android.app.Activity;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import androidx.core.content.ContextCompat;

import androidx.appcompat.app.AlertDialog;

import com.example.Cortex.R;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

/**
 * Reusable dropdown dialog.
 * Shows a list of options with white text on a dark background.
 */
public class Dialog {

    private static final float MAX_HEIGHT = 0.4f;
    private final Activity activity;

    /**
     * Creates a new DropdownDialog.
     *
     * @param activity The hosting activity
     */
    public Dialog(Activity activity) {
        this.activity = activity;
    }

    /**
     * Shows a dropdown dialog with the given options.
     *
     * @param target     The TextView to update on selection
     * @param arrayResId The string-array resource with the options
     * @param scrollable true to cap height + scroll, false to fit content
     */
    public void show(TextView target, int arrayResId, boolean scrollable) {
        String[] items = activity.getResources().getStringArray(arrayResId);

        View dialogView = LayoutInflater.from(activity)
            .inflate(R.layout.dialog_dropdown, null, false);

        ListView listView = dialogView.findViewById(R.id.dropdownListView);
        listView.setVerticalScrollBarEnabled(scrollable);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
            activity, R.layout.item_dropdown, R.id.dropdownItemTextView, items);
        listView.setAdapter(adapter);

        AlertDialog dialog = new MaterialAlertDialogBuilder(activity)
            .setView(dialogView)
            .create();

        listView.setOnItemClickListener((parent, view, position, id) -> {
            target.setText(items[position]);
            dialog.dismiss();
        });

        dialog.show();

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(
                new android.graphics.drawable.ColorDrawable(
                    ContextCompat.getColor(activity, R.color.surface_dark))
            );

            if (scrollable) {
                int maxHeight = (int) (activity.getResources()
                    .getDisplayMetrics().heightPixels * MAX_HEIGHT);
                WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
                params.height = maxHeight;
                dialog.getWindow().setAttributes(params);
            }
        }
    }
}