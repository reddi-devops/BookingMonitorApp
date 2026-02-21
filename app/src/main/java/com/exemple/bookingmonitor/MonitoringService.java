package com.exemple.bookingmonitor;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.IBinder;
import androidx.core.app.NotificationCompat;
import java.util.Timer;
import java.util.TimerTask;

public class MonitoringService extends Service {

    private Timer timer;
    private PageMonitor pageMonitor;
    private String lastContent = "";
    private static final long CHECK_INTERVAL = 15 * 60 * 1000; // 15 minutes
    private static final int NOTIFICATION_ID = 1;

    @Override
    public void onCreate() {
        super.onCreate();
        pageMonitor = new PageMonitor();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        startForegroundNotification();
        startMonitoring();
        return START_STICKY;
    }

    private void startForegroundNotification() {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "booking_monitor")
            .setContentTitle("Booking Monitor")
            .setContentText("Monitoring for available dates...")
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setOngoing(true);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            startForeground(NOTIFICATION_ID, builder.build(), 
                android.content.pm.ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC);
        } else {
            startForeground(NOTIFICATION_ID, builder.build());
        }
    }

    private void startMonitoring() {
        if (timer != null) {
            timer.cancel();
        }

        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                checkForAvailableDates();
            }
        }, 0, CHECK_INTERVAL);
    }

    private void checkForAvailableDates() {
        try {
            SharedPreferences prefs = getSharedPreferences("BookingMonitor", MODE_PRIVATE);
            String url = prefs.getString("monitoring_url", "");
            String alertEmail = prefs.getString("alert_email", "");

            if (url.isEmpty() || alertEmail.isEmpty()) {
                return;
            }

            String content = pageMonitor.fetchPageContent(url);

            if (content != null) {
                // Check for keywords indicating available dates
                if (isBookingAvailable(content) && !lastContent.equals(content)) {
                    lastContent = content;
                    
                    // Send notification
                    NotificationHelper.sendNotification(
                        this,
                        "Booking Available!",
                        "Dates are now available for booking. Tap to view."
                    );

                    // Send email
                    sendAlertEmail(alertEmail, url);

                    // Play alert sound and vibration
                    NotificationHelper.playAlertSound(this);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean isBookingAvailable(String content) {
        // Check for common indicators of available booking dates
        String[] indicators = {
            "available",
            "slot",
            "date",
            "select date",
            "pick date",
            "calendar",
            "yyyy-mm-dd",
            "booking open",
            "apply",
            "submit"
        };

        String lowerContent = content.toLowerCase();
        for (String indicator : indicators) {
            if (lowerContent.contains(indicator)) {
                // Check that it's not just saying "no dates available" or "not available"
                if (!lowerContent.contains("no " + indicator) && 
                    !lowerContent.contains("not " + indicator) &&
                    !lowerContent.contains("unavailable")) {
                    return true;
                }
            }
        }
        return false;
    }

    private void sendAlertEmail(String recipientEmail, String url) {
        new Thread(() -> {
            try {
                EmailSender emailSender = new EmailSender();
                emailSender.sendAlertEmail(
                    recipientEmail,
                    "Booking Dates Available - Booking Monitor Alert",
                    "Good news! Booking dates are now available.\n\n" +
                    "URL: " + url + "\n\n" +
                    "Please visit the link above to complete your booking.\n\n" +
                    "Sent by Booking Monitor App"
                );
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (timer != null) {
            timer.cancel();
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
