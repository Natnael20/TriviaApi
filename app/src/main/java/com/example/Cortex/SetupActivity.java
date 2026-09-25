package com.example.Cortex;

import android.os.Bundle;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.Cortex.components.Setup;
import com.example.Cortex.components.core.Menu;

public class SetupActivity extends AppCompatActivity {

    private Setup setup;
    private Menu menu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);

        menu = new Menu(this);
        menu.initialize((ImageView) findViewById(R.id.menuButton));

        setup = new Setup(this);
        setup.initialize();
    }
}