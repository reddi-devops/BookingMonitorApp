# Quick Start Guide - Booking Monitor App

## 5-Minute Setup

### Step 1: Prepare Gmail Account
1. Go to https://myaccount.google.com
2. Select "Security" from left menu
3. Enable "2-Step Verification" if not already enabled
4. Find "App passwords" (only available with 2-Step enabled)
5. Select Mail and Windows Computer
6. Copy the 16-character password

### Step 2: Update Email Configuration
Edit: `app/src/main/java/com/exemple/bookingmonitor/EmailSender.java`

Replace these lines:
```java
private static final String SENDER_EMAIL = "your-email@gmail.com";
private static final String SENDER_PASSWORD = "your-app-password";
```

With your actual email and app password:
```java
private static final String SENDER_EMAIL = "myemail@gmail.com";
private static final String SENDER_PASSWORD = "abcd efgh ijkl mnop";
```

### Step 3: Build APK
Open terminal in project folder and run:
```bash
./gradlew assembleDebug
```

Wait for build to complete. APK will be at:
```
app/build/outputs/apk/debug/app-debug.apk
```

### Step 4: Install on Phone
```bash
adb install app/build/outputs/apk/debug/app-debug.apk
```

### Step 5: Configure in App
1. Open "Booking Monitor" app
2. URL is pre-filled with the booking page
3. Enter your email: `kesav.jrp@gmail.com`
4. Click "Save Settings"

### Step 6: Test
1. Click "Test Monitoring" button
2. Check your email for test message
3. Look for push notification on phone

### Step 7: Enable Monitoring
1. Toggle "Enable Monitoring" ON
2. App will check page every 15 minutes
3. You'll get alerts when dates become available

## Troubleshooting Quick Fixes

**"Failed to send email"**
- ✓ Check email/password in EmailSender.java
- ✓ Ensure 2-Step verification is enabled
- ✓ Verify app has internet permission

**"No notifications appearing"**
- ✓ Enable notifications in phone Settings
- ✓ Add app to battery optimization whitelist
- ✓ Check if device is in silent/Do Not Disturb mode

**"App keeps stopping"**
- ✓ Do NOT force close the app
- ✓ Add to battery optimization exceptions
- ✓ Grant "Modify system settings" permission if needed

## File Locations After Build

```
BookingMonitorApp/
├── app/build/outputs/apk/debug/app-debug.apk  ← Install this
├── app/src/main/java/.../EmailSender.java     ← Edit this
└── README.md                                    ← Full documentation
```

## Default Configuration

- **URL**: https://annamalaiyar.hrce.tn.gov.in/ticketing/service_collection.php?tid=20343&scode=21&sscode=1&target_type=&fees_slno=63716&group_id=4&action=P
- **Check Interval**: 15 minutes
- **Alert Type**: Push notification + Email + Sound + Vibration
- **Recipient Email**: kesav.jrp@gmail.com (change in app)

## Next Steps

1. Once monitoring is ON, the app will check the page on schedule
2. When booking dates appear, you'll immediately get:
   - Push notification
   - Email alert
   - Sound alarm
   - Phone vibration
3. Click the notification to view details

---

That's it! Your booking monitor is now running. 🎉
