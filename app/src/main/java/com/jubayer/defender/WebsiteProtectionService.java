
package com.jubayer.defender;

import android.content.Intent;
import android.net.VpnService;
import android.os.ParcelFileDescriptor;
import android.util.Log;

public class WebsiteProtectionService extends VpnService {

    private static final String TAG = "JubayerVPN";
    private ParcelFileDescriptor vpnInterface;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent == null || !VpnService.SERVICE_INTERFACE.equals(
                intent.getAction())) {
            Log.i(TAG, "VPN service started; waiting for protection engine.");
        }

        // No VPN tunnel is established in this initial stage.
        // DNS filtering and packet forwarding must be implemented first.
        stopSelf(startId);
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        closeVpnInterface();
        super.onDestroy();
    }

    private void closeVpnInterface() {
        if (vpnInterface != null) {
            try {
                vpnInterface.close();
            } catch (Exception e) {
                Log.e(TAG, "Could not close VPN interface", e);
            }
            vpnInterface = null;
        }
    }
}
