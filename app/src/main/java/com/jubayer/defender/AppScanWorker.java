
package com.jubayer.defender;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.work.Worker;
import androidx.work.WorkerParameters;

import java.util.ArrayList;
import java.util.List;

public class AppScanWorker extends Worker {

    private static final String CHANNEL_ID = "jubayer_security";

    private final String[] riskyPermissions = {
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

    public AppScanWorker(
        Context context,
        WorkerParameters params
    ) {
        super(context, params);
    }

    @Override
    public Result doWork() {

        try {

            Context context = getApplicationContext();

            List<PackageInfo> apps =
                context.getPackageManager().getInstalledPackages(
                    PackageManager.GET_PERMISSIONS
                );

            int scanned = 0;
            int flagged = 0;

            for (PackageInfo app : apps) {

                scanned++;

                List<String> found = new ArrayList<>();

                if (app.requestedPermissions != null) {

                    for (String permission :
                        app.requestedPermissions) {

                        for (String risky : riskyPermissions) {

                            if (permission.equals(risky) &&
                                !found.contains(permission)) {

                                found.add(permission);
                            }
                        }
                    }
                }

                if (found.size() >= 3) {
                    flagged++;
                }
            }

            if (flagged > 0) {
                showNotification(
                    context,
                    "Security review needed",
                    flagged + " apps need permission review. " +
                    "Scanned: " + scanned
                );
            }

            return Result.success();

        } catch (Exception e) {

            return Result.retry();
        }
    }

    private void showNotification(
        Context context,
        String title,
        String message
    ) {

        NotificationManager manager =
            (NotificationManager) context.getSystemService(
                Context.NOTIFICATION_SERVICE
            );

        if (manager == null) return;

        if (Build.VERSION.SDK_INT >= 33 &&
            context.checkSelfPermission(
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        if (Build.VERSION.SDK_INT >= 26) {

            NotificationChannel channel =
                new NotificationChannel(
                    CHANNEL_ID,
                    "Jubayer Security Alerts",
                    NotificationManager.IMPORTANCE_DEFAULT
                );

            manager.createNotificationChannel(channel);
        }

        Notification.Builder builder;

        if (Build.VERSION.SDK_INT >= 26) {
            builder = new Notification.Builder(context, CHANNEL_ID);
        } else {
            builder = new Notification.Builder(context);
        }

        Notification notification = builder
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(title)
            .setContentText(message)
            .setStyle(new Notification.BigTextStyle().bigText(message))
            .setAutoCancel(true)
            .build();

        manager.notify(3001, notification);
    }
}
