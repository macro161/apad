# Slack Integration Setup Guide

This guide explains how to set up Slack notifications for the Weather Application.

## Prerequisites

1. Access to a Slack workspace where you can create apps
2. Admin permissions to add webhooks to your Slack workspace

## Step 1: Create a Slack App

1. Go to [https://api.slack.com/apps](https://api.slack.com/apps)
2. Click "Create New App"
3. Choose "From scratch"
4. Enter app name: "Weather Alert Bot"
5. Select your workspace
6. Click "Create App"

## Step 2: Enable Incoming Webhooks

1. In your app settings, go to "Incoming Webhooks"
2. Toggle "Activate Incoming Webhooks" to On
3. Click "Add New Webhook to Workspace"
4. Select the channel where you want weather alerts
5. Click "Allow"
6. Copy the webhook URL (it looks like: `https://hooks.slack.com/services/T00000000/B00000000/XXXXXXXXXXXXXXXXXXXXXXXX`)

## Step 3: Configure the Application

1. Open `src/main/resources/application.properties`
2. Update the Slack configuration:

```properties
# Slack Configuration
slack.webhook.url=https://hooks.slack.com/services/YOUR/SLACK/WEBHOOK
slack.notifications.enabled=true
```

3. Replace `YOUR/SLACK/WEBHOOK` with your actual webhook URL from Step 2

## Step 4: Restart the Application

```bash
./mvnw spring-boot:run
```

## Types of Slack Alerts

### 1. Extreme Weather Alerts (Automatic)
- **Extreme Cold**: Temperature ≤ -20°C
- **Extreme Heat**: Temperature ≥ 40°C
- **High Wind**: Wind speed ≥ 20 m/s
- **Severe Weather**: Heavy rain, storms, thunderstorms
- **Snow Alerts**: Snow conditions with specific temperature ranges

### 2. Critical Weather Alerts (Immediate)
- **Critical Cold**: Temperature ≤ -25°C (frostbite risk)
- **Critical Heat**: Temperature ≥ 45°C (heat stroke risk)
- **Severe Wind**: Wind speed ≥ 25 m/s (dangerous conditions)
- **Storm Alerts**: Thunderstorms and severe weather

### 3. Temperature Change Alerts
- Triggered when temperature changes by ≥10°C between checks
- Helps identify rapid weather changes

### 4. Scheduled Alerts
- **Daily Summary**: Sent at 8:00 AM daily for all monitored cities
- **Regular Monitoring**: Checks every 30 minutes for extreme conditions

## Monitored Cities

The application automatically monitors these cities:
- London, UK
- New York, US
- Tokyo, JP
- Paris, FR
- Sydney, AU
- Moscow, RU
- Mumbai, IN
- Berlin, DE

## API Endpoints for Manual Alerts

### Test Alert
```bash
curl -X POST http://localhost:8081/api/alerts/test/London
```

### Manual Weather Check
```bash
curl -X POST http://localhost:8081/api/monitoring/check
```

### Trigger Daily Summary
```bash
curl -X POST http://localhost:8081/api/alerts/summary
```

### Check Monitoring Status
```bash
curl http://localhost:8081/api/monitoring/status
```

## Sample Slack Messages

### Extreme Weather Alert
```
🔥 *EXTREME Weather Alert for Mumbai* 🔥
• Temperature: 42°C (108°F)
• Conditions: Hot and humid
• Wind Speed: 6.4 m/s
• Humidity: 88%
Please take appropriate precautions!
```

### Critical Weather Alert
```
🚨 *CRITICAL HEAT WARNING* 🚨
Extremely dangerous heat conditions in Mumbai: 47°C (117°F)
Risk of heat stroke. Stay indoors and hydrated!
```

### Daily Summary
```
🌤️ Daily weather summary for London: Partly cloudy, 15°C (59°F)
```

### Temperature Change Alert
```
🌡️ Significant temperature change in Moscow: -5°C → -18°C (Δ-13°C)
```

## Troubleshooting

### Notifications Not Working
1. Check that `slack.notifications.enabled=true`
2. Verify webhook URL is correct
3. Check application logs for errors
4. Test webhook URL manually with curl:

```bash
curl -X POST -H 'Content-type: application/json' \
--data '{"text":"Test message from Weather App"}' \
YOUR_WEBHOOK_URL
```

### Webhook URL Issues
- Ensure the URL starts with `https://hooks.slack.com/services/`
- Make sure there are no extra spaces or characters
- Verify the webhook is still active in your Slack app settings

### Permission Issues
- Ensure the Slack app has permission to post to the selected channel
- Check if the webhook was revoked or the app was removed

## Customization

You can customize alert thresholds by modifying:
- `WeatherMonitoringService.java` - Alert conditions and scheduling
- `SlackNotificationService.java` - Message formatting and alert types
- `application.properties` - Configuration values

## Security Notes

- Keep your webhook URL secret
- Don't commit webhook URLs to version control
- Consider using environment variables for production:

```bash
export SLACK_WEBHOOK_URL="https://hooks.slack.com/services/YOUR/SLACK/WEBHOOK"
export SLACK_NOTIFICATIONS_ENABLED=true
```

Then reference in application.properties:
```properties
slack.webhook.url=${SLACK_WEBHOOK_URL}
slack.notifications.enabled=${SLACK_NOTIFICATIONS_ENABLED}
```