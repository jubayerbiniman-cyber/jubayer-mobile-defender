
package com.jubayer.defender;

import android.app.Activity;
import android.os.Bundle;
import android.content.Intent;
import android.content.pm.*;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.view.Gravity;
import android.view.View;
import android.widget.*;

import java.io.*;
import java.security.MessageDigest;
import java.util.*;

public class MainActivity extends Activity {

    LinearLayout main;
    TextView report;
    TextView status;

    final int BG = Color.rgb(8, 12, 20);
    final int CARD = Color.rgb(18, 26, 39);
    final int CYAN = Color.rgb(0, 220, 220);
    final int WHITE = Color.WHITE;
    final int MUTED = Color.rgb(160, 175, 195);

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ScrollView scroll = new ScrollView(this);
        scroll.setFillViewport(true);

        main = new LinearLayout(this);
        main.setOrientation(LinearLayout.VERTICAL);
        main.setPadding(18, 24, 18, 30);
        main.setBackgroundColor(BG);

        scroll.addView(main);
        setContentView(scroll);

        createDashboard();
    }

    GradientDrawable shape(int color, int radius) {
        GradientDrawable d = new GradientDrawable();
        d.setColor(color);
        d.setCornerRadius(radius);
        d.setStroke(1, Color.rgb(35, 65, 80));
        return d;
    }

    TextView text(String value, int size, int color) {
        TextView t = new TextView(this);
        t.setText(value);
        t.setTextSize(size);
        t.setTextColor(color);
        t.setPadding(8, 8, 8, 8);
        return t;
    }

    void addHeader(String value, int size, int color) {
        TextView t = text(value, size, color);
        t.setTypeface(null, Typeface.BOLD);
        main.addView(t);
    }

    void addCard(String title, String subtitle, Runnable action) {

        LinearLayout card = new LinearLayout(this);
        card.setOrientation(LinearLayout.VERTICAL);
        card.setPadding(16, 14, 16, 14);
        card.setBackground(shape(CARD, 22));

        TextView heading = text(title, 18, CYAN);
        heading.setTypeface(null, Typeface.BOLD);

        TextView description = text(subtitle, 13, MUTED);

        card.addView(heading);
        card.addView(description);

        LinearLayout.LayoutParams params =
            new LinearLayout.LayoutParams(
                -1, -2
            );

        params.setMargins(0, 8, 0, 8);

        main.addView(card, params);

        card.setOnClickListener(v -> action.run());
    }

    void createDashboard() {

        addHeader("JUBAYER", 17, CYAN);
        addHeader("MOBILE DEFENDER", 29, WHITE);

        TextView version = text(
            "ADVANCED SECURITY CENTER  |  V3",
            12, MUTED
        );
        main.addView(version);

        LinearLayout statusCard = new LinearLayout(this);
        statusCard.setOrientation(LinearLayout.VERTICAL);
        statusCard.setPadding(16, 15, 16, 15);
        statusCard.setBackground(shape(
            Color.rgb(12, 35, 42), 20
        ));

        status = text("● SYSTEM READY", 17, CYAN);
        status.setTypeface(null, Typeface.BOLD);

        statusCard.addView(status);
        statusCard.addView(text(
            "Device security dashboard is active.",
            13, WHITE
        ));

        main.addView(statusCard);

        addHeader("SECURITY TOOLS", 18, WHITE);

        addCard(
            "◈ INSTALLED APP SCANNER",
            "Check installed apps and sensitive permissions.",
            this::scanApps
        );

        addCard(
            "◈ APK FILE SCANNER",
            "Select an APK to inspect its metadata and permissions.",
            this::selectAPK
        );

        addCard(
            "◈ SECURITY INFORMATION",
            "Understand scan results and protection limits.",
            this::showInformation
        );

        addHeader("SCAN REPORT", 18, CYAN);

        report = text(
            "No scan performed yet.\n\nChoose a security tool above.",
            14, WHITE
        );

        report.setPadding(16, 18, 16, 18);
        report.setBackground(shape(CARD, 18));

        main.addView(report);

        TextView footer = text(
            "\nJUBAYER MOBILE DEFENDER\nDefensive Security Edition",
            11, MUTED
        );
        footer.setGravity(Gravity.CENTER);
        main.addView(footer);
    }

    void setStatus(String value) {
        status.setText("● " + value);
    }

    void scanApps() {

        setStatus("SCANNING INSTALLED APPS");
        report.setText("Scanning installed applications...\nPlease wait.");

        new Thread(() -> {

            try {

                List<PackageInfo> apps =
                    getPackageManager().getInstalledPackages(
                        PackageManager.GET_PERMISSIONS
                    );

                StringBuilder result = new StringBuilder();

                int scanned = 0;
                int flagged = 0;

                for (PackageInfo app : apps) {

                    scanned++;

                    List<String> found = new ArrayList<>();

                    if (app.requestedPermissions != null) {

                        for (String permission :
                            app.requestedPermissions) {

                            for (String risky : riskyPermissions) {

                                if (permission.equals(risky)) {

                                    String shortName =
                                        permission.replace(
                                            "android.permission.", ""
                                        );

                                    if (!found.contains(shortName)) {
                                        found.add(shortName);
                                    }
                                }
                            }
                        }
                    }

                    if (found.size() >= 3) {

                        flagged++;

                        result.append("\n⚠ REVIEW REQUIRED\n");
                        result.append("App: ")
                            .append(app.applicationInfo.loadLabel(
                                getPackageManager()
                            ))
                            .append("\n");

                        result.append("Package: ")
                            .append(app.packageName)
                            .append("\n");

                        result.append("Permissions: ")
                            .append(found)
                            .append("\n");

                        result.append("----------------------\n");
                    }
                }

                String summary =
                    "INSTALLED APP SECURITY REPORT\n\n" +
                    "Apps scanned: " + scanned +
                    "\nApps requiring review: " + flagged +
                    "\n\n" +
                    (flagged == 0
                        ? "No apps met the review threshold."
                        : result.toString()) +
                    "\n\nPermission risk is NOT proof of malware.";

                runOnUiThread(() -> {
                    report.setText(summary);
                    setStatus("APP SCAN COMPLETED");
                });

            } catch (Exception e) {

                runOnUiThread(() -> {
                    report.setText(
                        "Scan error:\n" + e.getMessage()
                    );
                    setStatus("SCAN ERROR");
                });
            }

        }).start();
    }

    void selectAPK() {

        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.setType("application/vnd.android.package-archive");
        intent.addCategory(Intent.CATEGORY_OPENABLE);

        startActivityForResult(intent, 101);
    }

    @Override
    protected void onActivityResult(
        int requestCode,
        int resultCode,
        Intent data
    ) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 101 &&
            resultCode == RESULT_OK &&
            data != null &&
            data.getData() != null) {

            Uri uri = data.getData();

            setStatus("ANALYZING APK");
            report.setText("APK analysis running...\nPlease wait.");

            new Thread(() -> scanAPK(uri)).start();
        }
    }

    void scanAPK(Uri uri) {

        File apkFile = null;

        try {

            apkFile = File.createTempFile(
                "jubayer_scan_", ".apk", getCacheDir()
            );

            InputStream input =
                getContentResolver().openInputStream(uri);

            if (input == null) {
                throw new Exception("Cannot read selected file.");
            }

            FileOutputStream output =
                new FileOutputStream(apkFile);

            byte[] buffer = new byte[8192];
            int length;

            while ((length = input.read(buffer)) != -1) {
                output.write(buffer, 0, length);
            }

            input.close();
            output.close();

            String hash = calculateSHA256(apkFile);

            PackageInfo info =
                getPackageManager().getPackageArchiveInfo(
                    apkFile.getAbsolutePath(),
                    PackageManager.GET_PERMISSIONS
                );

            if (info == null) {
                throw new Exception(
                    "Invalid or unreadable APK file."
                );
            }

            String appName = "Unknown";

            if (info.applicationInfo != null) {

                info.applicationInfo.sourceDir =
                    apkFile.getAbsolutePath();

                info.applicationInfo.publicSourceDir =
                    apkFile.getAbsolutePath();

                appName = info.applicationInfo.loadLabel(
                    getPackageManager()
                ).toString();
            }

            List<String> permissions = new ArrayList<>();

            if (info.requestedPermissions != null) {

                for (String permission :
                    info.requestedPermissions) {

                    permissions.add(
                        permission.replace(
                            "android.permission.", ""
                        )
                    );
                }
            }

            List<String> risky = new ArrayList<>();

            for (String permission :
                info.requestedPermissions == null
                    ? new String[0]
                    : info.requestedPermissions) {

                for (String riskyPermission : riskyPermissions) {

                    if (permission.equals(riskyPermission)) {

                        String name = permission.replace(
                            "android.permission.", ""
                        );

                        if (!risky.contains(name)) {
                            risky.add(name);
                        }
                    }
                }
            }

            String riskLabel =
                risky.size() >= 3
                    ? "REVIEW REQUIRED"
                    : "NO HIGH PERMISSION COUNT FLAG";

            String result =
                "APK SECURITY REPORT\n\n" +
                "App name: " + appName +
                "\nPackage: " + info.packageName +
                "\nVersion: " + info.versionName +
                "\nVersion code: " + info.versionCode +
                "\n\nSHA-256:\n" + hash +
                "\n\nRisk review: " + riskLabel +
                "\nSensitive permissions: " + risky.size() +
                "\n\nPermissions:\n" + permissions +
                "\n\nNOTE:\n" +
                "This is a metadata and permission inspection. " +
                "It does not prove whether the APK contains malware.";

            runOnUiThread(() -> {
                report.setText(result);
                setStatus("APK ANALYSIS COMPLETED");
            });

        } catch (Exception e) {

            runOnUiThread(() -> {
                report.setText(
                    "APK scan failed:\n" + e.getMessage()
                );
                setStatus("APK SCAN ERROR");
            });

        } finally {

            if (apkFile != null) {
                apkFile.delete();
            }
        }
    }

    String calculateSHA256(File file) throws Exception {

        MessageDigest digest =
            MessageDigest.getInstance("SHA-256");

        InputStream input = new FileInputStream(file);

        byte[] buffer = new byte[8192];
        int count;

        while ((count = input.read(buffer)) > 0) {
            digest.update(buffer, 0, count);
        }

        input.close();

        byte[] hash = digest.digest();

        StringBuilder hex = new StringBuilder();

        for (byte b : hash) {
            hex.append(String.format("%02x", b));
        }

        return hex.toString();
    }

    void showInformation() {

        report.setText(
            "JUBAYER MOBILE DEFENDER V3\n\n" +
            "CURRENT FEATURES\n" +
            "• Installed app permission inspection\n" +
            "• APK metadata inspection\n" +
            "• APK SHA-256 calculation\n" +
            "• Sensitive permission review\n\n" +
            "IMPORTANT LIMITATIONS\n" +
            "• Permission count cannot prove malware.\n" +
            "• SHA-256 alone cannot identify a threat.\n" +
            "• No live website filtering is active yet.\n" +
            "• Background monitoring is not active yet.\n\n" +
            "More protection modules will be added separately."
        );
    }
}
