
package com.jubayer.defender;

import android.app.Activity;
import android.os.Bundle;
import android.content.pm.*;
import android.graphics.Color;
import android.view.Gravity;
import android.widget.*;
import android.view.View;

import java.util.*;

public class MainActivity extends Activity {

    LinearLayout layout;
    TextView report;

    String[] riskyPermissions = {
        "android.permission.READ_SMS",
        "android.permission.SEND_SMS",
        "android.permission.RECEIVE_SMS",
        "android.permission.READ_CONTACTS",
        "android.permission.READ_CALL_LOG",
        "android.permission.RECORD_AUDIO",
        "android.permission.CAMERA",
        "android.permission.ACCESS_FINE_LOCATION",
        "android.permission.REQUEST_INSTALL_PACKAGES",
        "android.permission.SYSTEM_ALERT_WINDOW"
    };

    void addText(String text, int size, int color) {
        TextView t = new TextView(this);
        t.setText(text);
        t.setTextSize(size);
        t.setTextColor(color);
        t.setPadding(12, 12, 12, 12);
        layout.addView(t);
    }

    void addButton(String title, Runnable action) {
        Button b = new Button(this);
        b.setText(title);
        layout.addView(b);
        b.setOnClickListener(v -> action.run());
    }

    void scanApps() {

        report.setText("Scanning installed apps...");

        new Thread(() -> {

            List<PackageInfo> apps =
                getPackageManager().getInstalledPackages(
                    PackageManager.GET_PERMISSIONS
                );

            StringBuilder result = new StringBuilder();

            int scanned = 0;
            int flagged = 0;

            for (PackageInfo app : apps) {

                scanned++;

                int score = 0;
                List<String> found = new ArrayList<>();

                if (app.requestedPermissions != null) {

                    for (String permission : app.requestedPermissions) {

                        for (String risky : riskyPermissions) {

                            if (permission.equals(risky)) {
                                score++;
                                found.add(
                                    permission.replace(
                                        "android.permission.", ""
                                    )
                                );
                            }
                        }
                    }
                }

                if (score >= 3) {

                    flagged++;

                    result.append("\n⚠️ REVIEW REQUIRED\n");
                    result.append("App: ")
                        .append(app.applicationInfo.loadLabel(
                            getPackageManager()
                        ))
                        .append("\n");

                    result.append("Package: ")
                        .append(app.packageName)
                        .append("\n");

                    result.append("Sensitive permissions: ")
                        .append(found)
                        .append("\n");

                    result.append("----------------------\n");
                }
            }

            final String summary =
                "JUBAYER SECURITY REPORT\n\n" +
                "Apps scanned: " + scanned +
                "\nApps requiring review: " + flagged +
                "\n\n" +
                (flagged == 0
                    ? "No apps met the permission-risk threshold."
                    : result.toString()) +
                "\n\nNOTE: Permission risk is not proof of malware.";

            runOnUiThread(() -> report.setText(summary));

        }).start();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ScrollView scroll = new ScrollView(this);

        layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(16, 20, 16, 20);
        layout.setBackgroundColor(Color.rgb(10, 15, 20));

        addText("JUBAYER AI", 30, Color.GREEN);
        addText("MOBILE DEFENDER V2", 22, Color.WHITE);

        addText("INSTALLED APP SECURITY", 18, Color.GREEN);

        addButton("START APP SCAN", this::scanApps);

        report = new TextView(this);
        report.setTextColor(Color.WHITE);
        report.setTextSize(14);
        report.setPadding(12, 20, 12, 20);

        report.setText(
            "Security scanner ready.\nPress START APP SCAN."
        );

        layout.addView(report);

        scroll.addView(layout);
        setContentView(scroll);
    }
}
