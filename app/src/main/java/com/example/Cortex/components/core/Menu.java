package com.example.Cortex.components.core;

import android.app.Activity;
import android.view.Gravity;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.view.menu.MenuBuilder;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.Cortex.R;
import com.example.Cortex.listener.EdgeLightningListener;
import com.example.Cortex.manager.EdgeLightningManager;

/**
 * Reusable drawer menu component.
 * Delegates Edge Lightning state to {@link EdgeLightningManager}.
 */
public class Menu implements EdgeLightningListener {

    private final Activity activity;
    private final DrawerLayout drawerLayout;
    private final LinearLayout drawerItemsContainer;
    private final EdgeLightningManager edgeManager;

    private Switch edgeLightningSwitch;

    public Menu(Activity activity) {
        this.activity = activity;
        this.drawerLayout = activity.findViewById(R.id.drawerLayout);
        this.drawerItemsContainer = activity.findViewById(R.id.drawerItemsContainer);
        this.edgeManager = EdgeLightningManager.getInstance(activity);
        this.edgeManager.addListener(this);
    }

    public void initialize(ImageView menuButton) {
        menuButton.setOnClickListener(v ->
            drawerLayout.openDrawer(activity.findViewById(R.id.drawerPanel)));
        buildDrawerMenu();
    }

    @Override
    public void onEdgeLightningChanged(boolean on) {
        if (edgeLightningSwitch != null) edgeLightningSwitch.setChecked(on);
    }

    public void shutdown() {
        edgeManager.removeListener(this);
    }

    private void buildDrawerMenu() {
        if (drawerItemsContainer == null) return;
        drawerItemsContainer.removeAllViews();

        MenuBuilder menuBuilder = new MenuBuilder(activity);
        activity.getMenuInflater().inflate(R.menu.menu_main, menuBuilder);

        for (int i = 0; i < menuBuilder.size(); i++) {
            MenuItem item = menuBuilder.getItem(i);
            int itemId = item.getItemId();

            if (itemId == R.id.action_edge_lightning) {
                drawerItemsContainer.addView(createSwitchRow(item.getTitle().toString()));
            } else {
                TextView itemView = createMenuItemView(item.getTitle().toString());
                itemView.setOnClickListener(v -> handleMenuItemClick(itemId));
                drawerItemsContainer.addView(itemView);
            }
        }
    }

    private LinearLayout createSwitchRow(String title) {
        LinearLayout row = new LinearLayout(activity);
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setGravity(Gravity.CENTER_VERTICAL);
        row.setLayoutParams(new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT, 140));
        row.setPadding(12, 0, 12, 0);

        TextView label = new TextView(activity);
        label.setText(title);
        label.setTextColor(ContextCompat.getColor(activity, R.color.text_primary));
        label.setTextSize(16f);
        label.setLayoutParams(new LinearLayout.LayoutParams(
            0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));

        edgeLightningSwitch = new Switch(activity);
        edgeLightningSwitch.setChecked(edgeManager.isEnabled());
        edgeLightningSwitch.setOnCheckedChangeListener((btn, checked) ->
            edgeManager.setEnabled(checked));

        row.addView(label);
        row.addView(edgeLightningSwitch);
        row.setOnClickListener(v -> edgeManager.toggle());

        return row;
    }

    private TextView createMenuItemView(String title) {
        TextView tv = new TextView(activity);
        tv.setText(title);
        tv.setTextColor(ContextCompat.getColor(activity, R.color.text_primary));
        tv.setTextSize(16f);
        tv.setPadding(12, 0, 12, 0);
        tv.setLayoutParams(new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT, 140));
        tv.setGravity(Gravity.CENTER_VERTICAL);
        tv.setClickable(true);
        tv.setFocusable(true);
        return tv;
    }

    private void handleMenuItemClick(int itemId) {
        if (itemId == R.id.action_high_scores) {
            activity.moveTaskToBack(true);
        }
        drawerLayout.closeDrawer(activity.findViewById(R.id.drawerPanel));
    }
}