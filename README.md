# Booking Monitor - Android Application

A background monitoring Android application that checks a booking/ticketing page for available dates and sends alerts via push notification and email.

## Features

✅ **Background Monitoring** - Continuously monitors the specified URL every 15 minutes  
✅ **Push Notifications** - Instant alerts when dates become available  
✅ **Email Alerts** - Sends email notifications to your address  
✅ **Sound & Vibration** - Audible and tactile alerts  
✅ **Auto-Start** - Automatically resumes monitoring after device reboot  
✅ **Persistent Monitoring** - Runs as foreground service (won't be killed by system)  
✅ **Easy Configuration** - Simple UI to set URL and email  

## Project Structure

```
BookingMonitorApp/
├── app/
│   ├── src/main/
│   │   ├── java/com/exemple/bookingmonitor/
│   │   │   ├── MainActivity.java          # Main UI activity
│   │   │   ├── MonitoringService.java     # Background service
│   │   │   ├── PageMonitor.java           # HTTP page fetcher
│   │   │   ├── EmailSender.java           # Email notification
│   │   │   ├── NotificationHelper.java    # Push notifications
│   │   │   └── BootCompleteReceiver.java  # Auto-start on boot
│   │   ├── res/
│   │   │   ├── layout/activity_main.xml
│   │   │   ├── values/
│   │   │   │   ├── strings.xml
│   │   │   │   ├── colors.xml
│   │   │   │   └── themes.xml
│   │   └── AndroidManifest.xml
│   ├── build.gradle
│   └── proguard-rules.pro
├── build.gradle
├── settings.gradle
└── README.md
```

## Prerequisites

- Android Studio 2022.1 or later
- JDK 11 or higher
- Android SDK 26+ (API level 26+)
- Gmail account with [App Password](https://support.google.com/accounts/answer/185833)

## Installation & Setup

### 1. Clone/Download the Project
```bash
cd /root/Downloads/BookingMonitorApp
```

### 2. Open in Android Studio
- Launch Android Studio
- Select "Open an Existing Project"
- Navigate to `/root/Downloads/BookingMonitorApp`
- Wait for Gradle sync to complete

### 3. Configure Email (IMPORTANT)

Edit `app/src/main/java/com/exemple/bookingmonitor/EmailSender.java`:

```java
private static final String SENDER_EMAIL = "your-email@gmail.com";
private static final String SENDER_PASSWORD = "your-app-password";
```

**To get your Gmail App Password:**
1. Go to [Google Account Settings](https://myaccount.google.com/)
2. Enable 2-Step Verification if not already done
3. Generate App Password for "Mail" and "Windows Computer"
4. Copy the 16-character password
5. Replace `your-app-password` with the copied password

### 4. Build the APK

```bash
./gradlew assembleDebug
```

The APK will be generated at: `app/build/outputs/apk/debug/app-debug.apk`

### 5. Install on Device

```bash
adb install app/build/outputs/apk/debug/app-debug.apk
```

## Usage

1. **Launch the App** - Open "Booking Monitor" from your app drawer

2. **Configure Settings:**
   - Enter the booking page URL (pre-filled with the example)
   - Enter your email address where you want alerts
   - Click "Save Settings"

3. **Enable Monitoring:**
   - Toggle the "Enable Monitoring" switch to ON
   - App will start monitoring in background

4. **Test the App:**
   - Click "Test Monitoring" button
   - You should receive a test email and notification
   - Verify email arrives at the configured address

5. **Monitor in Background:**
   - The app will check the page every 15 minutes
   - You'll receive notifications when dates become available
   - Email alert is also sent automatically

## How It Works

### Monitoring Process

1. **Service Starts**: When enabled, `MonitoringService` starts as foreground service
2. **Regular Checks**: Timer checks page content every 15 minutes
3. **Content Analysis**: Looks for keywords indicating available booking dates
4. **Alert Triggered**: When dates found, sends notifications and email
5. **Email Notification**: Background thread sends email via Gmail SMTP
6. **User Alert**: Push notification + sound + vibration

### Detection Logic

The app looks for keywords indicating available dates:
- "available", "slot", "date", "select date", "pick date", "calendar"
- "booking open", "apply", "submit"

It also ignores pages showing "no dates available" or "unavailable"

## Permissions Required

The app requests the following permissions:

```xml
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
<uses-permission android:name="android.permission.POST_NOTIFICATIONS" />
<uses-permission android:name="android.permission.VIBRATE" />
<uses-permission android:name="android.permission.FOREGROUND_SERVICE" />
```

**Grant permissions in Settings > Apps > Booking Monitor > Permissions**

## Important Notes

⚠️ **Email Configuration**: The app MUST be configured with valid Gmail credentials to send emails

⚠️ **Battery Optimization**: Add the app to battery optimization whitelist to prevent system from killing it:
- Settings > Battery > App Battery Usage > Booking Monitor > Don't Optimize

⚠️ **Notification Permissions**: Grant notification permissions on Android 13+

⚠️ **Background Restriction**: Ensure the app is not restricted from running in background

## Troubleshooting

### Not Receiving Notifications
1. Check if notifications are enabled in system settings
2. Verify app is added to battery optimization whitelist
3. Check device's Do Not Disturb mode

### Email Not Sending
1. Verify Gmail email and app password are correct
2. Check internet connection
3. Ensure 2-Step Verification is enabled on Gmail account
4. Try sending test email from the app

### App Stopping After Reboot
1. Verify foreground service notification is showing
2. Add app to battery optimization whitelist
3. Ensure "is_monitoring" preference is saved

### Page Not Detected as Available
1. Check if the booking page has keywords the app looks for
2. Modify detection logic in `MonitoringService.isBookingAvailable()`
3. Try the "Test Monitoring" button to verify page access

## Building Release APK

```bash
./gradlew assembleRelease
```

The release APK will be at: `app/build/outputs/apk/release/app-release.apk`

## Customization

### Change Check Frequency
In `MonitoringService.java`, modify:
```java
private static final long CHECK_INTERVAL = 15 * 60 * 1000; // Change to your interval
```

### Change Email Provider
If using a different email provider instead of Gmail:
1. Update SMTP settings in `EmailSender.java`
2. Change host, port, and authentication method accordingly

### Custom Detection Logic
Modify `isBookingAvailable()` in `MonitoringService.java` to detect your specific page format

## APK Installation Methods

### Method 1: Using ADB
```bash
adb install app/build/outputs/apk/debug/app-debug.apk
```

### Method 2: Direct Transfer
1. Connect Android device to computer
2. Copy APK to device storage
3. Open file manager on phone
4. Tap the APK file to install

### Method 3: Using Android Studio
1. Build the project
2. Click "Run" or "Debug" in Android Studio
3. Select your device

## Development Notes

- **Target SDK**: Android 13 (API 33)
- **Min SDK**: Android 12 (API 26)
- **Language**: Java 11
- **HTTP Client**: OkHttp 4.11
- **Email**: JavaMail API
- **Notifications**: Android X Core Library

## License

This application is provided as-is for monitoring booking pages.

## Support

For issues or feature requests related to the app functionality, check the code comments and documentation sections.

---

**Last Updated**: February 20, 2026
**Version**: 1.0
