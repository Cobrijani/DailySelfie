package com.example.dailyselfie;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

public class SelfieBroadcast extends BroadcastReceiver {

	private static final int SELFIE_NOTIFICATION_ID = 1;

	private final CharSequence contentTitle = "Daily Selfie";
	private final CharSequence contentText = "Time for another selfie";

	private Intent mNotificationIntent;
	private PendingIntent mContentIntent;

	private final Uri soundURI = Uri
			.parse("android.resource://course.examples.Alarms.AlarmCreate/" + R.raw.alarm_rooster);
	private final long[] mVibratePattern = { 0, 200, 200, 300 };

	@Override
	public void onReceive(Context context, Intent intent) {
		mNotificationIntent = new Intent(context, MainActivity.class);

		mContentIntent = PendingIntent.getActivity(context, 0, mNotificationIntent, Intent.FLAG_ACTIVITY_NEW_TASK);

		Notification.Builder notificationBuilder = new Notification.Builder(context)
				.setSmallIcon(R.drawable.ic_white_camera).setAutoCancel(true).setContentTitle(contentTitle)
				.setContentText(contentText).setContentIntent(mContentIntent).setSound(soundURI)
				.setVibrate(mVibratePattern);

		NotificationManager mNotificationManager = (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);

		mNotificationManager.notify(SELFIE_NOTIFICATION_ID, notificationBuilder.build());

	}
}
