package com.exemple.bookingmonitor;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;

public class BootCompleteReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            // Check if monitoring was enabled before reboot
            SharedPreferences prefs = context.getSharedPreferences("BookingMonitor", Context.MODE_PRIVATE);
            boolean isMonitoring = prefs.getBoolean("is_monitoring", false);

            if (isMonitoring) {
                Intent monitoringIntent = new Intent(context, MonitoringService.class);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    context.startForegroundService(monitoringIntent);
                } else {
                    context.startService(monitoringIntent);
                }
            }
        }
    }
}
