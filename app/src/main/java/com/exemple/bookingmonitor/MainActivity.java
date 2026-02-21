package com.exemple.bookingmonitor;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationManagerCompat;

public class MainActivity extends AppCompatActivity {

    private EditText urlInput, emailInput;
    private Switch monitoringSwitch;
    private TextView statusText;
    private Button saveButton, testButton;
    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize views
        urlInput = findViewById(R.id.urlInput);
        emailInput = findViewById(R.id.emailInput);
        monitoringSwitch = findViewById(R.id.monitoringSwitch);
        statusText = findViewById(R.id.statusText);
        saveButton = findViewById(R.id.saveButton);
        testButton = findViewById(R.id.testButton);

        prefs = getSharedPreferences("BookingMonitor", MODE_PRIVATE);

        // Load saved preferences
        loadPreferences();

        // Create notification channel
        NotificationHelper.createNotificationChannel(this);

        // Save button listener
        saveButton.setOnClickListener(v -> saveSettings());

        // Test button listener
        testButton.setOnClickListener(v -> testMonitoring());

        // Monitoring switch listener
        monitoringSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                saveSettings();
                startMonitoring();
            } else {
                stopMonitoring();
            }
        });

        // Check permissions
        checkNotificationPermission();
    }

    private void loadPreferences() {
        String savedUrl = prefs.getString("monitoring_url", "https://annamalaiyar.hrce.tn.gov.in/ticketing/service_collection.php?tid=20343&scode=21&sscode=1&target_type=&fees_slno=63716&group_id=4&action=P");
        String savedEmail = prefs.getString("alert_email", "kesav.jrp@gmail.com");
        boolean isMonitoring = prefs.getBoolean("is_monitoring", false);

        urlInput.setText(savedUrl);
        emailInput.setText(savedEmail);
        monitoringSwitch.setChecked(isMonitoring);

        updateStatus();
    }

    private void saveSettings() {
        String url = urlInput.getText().toString().trim();
        String email = emailInput.getText().toString().trim();

        if (url.isEmpty() || email.isEmpty()) {
            Toast.makeText(this, "Please enter both URL and email", Toast.LENGTH_SHORT).show();
            monitoringSwitch.setChecked(false);
            return;
        }

        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("monitoring_url", url);
        editor.putString("alert_email", email);
        editor.putBoolean("is_monitoring", monitoringSwitch.isChecked());
        editor.apply();

        Toast.makeText(this, "Settings saved", Toast.LENGTH_SHORT).show();
        updateStatus();
    }

    private void startMonitoring() {
        Intent intent = new Intent(this, MonitoringService.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(intent);
        } else {
            startService(intent);
        }
        Toast.makeText(this, "Monitoring started", Toast.LENGTH_SHORT).show();
        updateStatus();
    }

    private void stopMonitoring() {
        Intent intent = new Intent(this, MonitoringService.class);
        stopService(intent);
        Toast.makeText(this, "Monitoring stopped", Toast.LENGTH_SHORT).show();
        updateStatus();
    }

    private void testMonitoring() {
        String url = urlInput.getText().toString().trim();
        String email = emailInput.getText().toString().trim();

        if (url.isEmpty() || email.isEmpty()) {
            Toast.makeText(this, "Please enter URL and email first", Toast.LENGTH_SHORT).show();
            return;
        }

        testButton.setEnabled(false);
        new Thread(() -> {
            try {
                PageMonitor monitor = new PageMonitor();
                String content = monitor.fetchPageContent(url);

                if (content != null) {
                    // Send test email
                    EmailSender emailSender = new EmailSender();
                    emailSender.sendAlertEmail(
                        email,
                        "Booking Monitor Test Alert",
                        "This is a test notification. The monitoring system is working correctly.\n\n" +
                        "URL: " + url + "\n" +
                        "Content length: " + content.length() + " characters"
                    );

                    runOnUiThread(() -> {
                        Toast.makeText(MainActivity.this, "Test email sent successfully", Toast.LENGTH_SHORT).show();
                        NotificationHelper.sendNotification(
                            MainActivity.this,
                            "Test Notification",
                            "Booking monitor test successful"
                        );
                    });
                } else {
                    runOnUiThread(() -> Toast.makeText(MainActivity.this, "Failed to fetch page content", Toast.LENGTH_SHORT).show());
                }
            } catch (Exception e) {
                runOnUiThread(() -> Toast.makeText(MainActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show());
            } finally {
                runOnUiThread(() -> testButton.setEnabled(true));
            }
        }).start();
    }

    private void updateStatus() {
        boolean isMonitoring = monitoringSwitch.isChecked();
        String status = isMonitoring ? "Monitoring: ON" : "Monitoring: OFF";
        statusText.setText(status);
    }

    private void checkNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (!NotificationManagerCompat.from(this).areNotificationsEnabled()) {
                Toast.makeText(this, "Please enable notifications in system settings", Toast.LENGTH_LONG).show();
            }
        }
    }
}
