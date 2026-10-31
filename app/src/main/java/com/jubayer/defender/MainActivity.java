
package com.jubayer.defender;

import android.app.Activity;
import android.os.Bundle;
import android.graphics.Color;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends Activity {

    LinearLayout layout;

    void addText(String text, int size, int color) {
        TextView t = new TextView(this);
        t.setText(text);
        t.setTextSize(size);
        t.setTextColor(color);
        t.setGravity(Gravity.CENTER);
        t.setPadding(12, 16, 12, 16);
        layout.addView(t);
    }

    void addButton(String name) {
        Button b = new Button(this);
        b.setText(name);
        layout.addView(b);

        b.setOnClickListener(v ->
            Toast.makeText(this,
                name + " - Coming Soon",
                Toast.LENGTH_SHORT).show()
        );
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setGravity(Gravity.CENTER);
        layout.setPadding(20, 20, 20, 20);
        layout.setBackgroundColor(Color.rgb(10, 15, 20));

        addText("JUBAYER AI", 30, Color.GREEN);
        addText("MOBILE DEFENDER", 22, Color.WHITE);
        addText("ANDROID SECURITY CENTER", 14, Color.LTGRAY);

        addText("PROTECTION STATUS", 18, Color.GREEN);
        addText("SETUP IN PROGRESS", 16, Color.YELLOW);

        addButton("APK SECURITY SCANNER");
        addButton("INSTALLED APP MONITOR");
        addButton("WEB PROTECTION");
        addButton("SECURITY LOGS");
        addButton("QUARANTINE");

        setContentView(layout);
    }
}
