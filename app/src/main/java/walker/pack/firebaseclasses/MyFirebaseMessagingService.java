package walker.pack.firebaseclasses;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import walker.pack.HomeActivity;
import walker.pack.R;

/**
 * Created by s214108503 on 2017/09/18.
 */

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        Intent intent = new Intent(this, HomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("starting", "notification");
        PendingIntent pendingIntent = PendingIntent.getActivities(this, 0, new Intent[]{intent}, PendingIntent.FLAG_ONE_SHOT);
        NotificationCompat.Builder notification_builder = new NotificationCompat.Builder(this);
        notification_builder.setContentTitle("FCM NOTIFICATION");
        notification_builder.setContentText(remoteMessage.getNotification().getBody());
        notification_builder.setAutoCancel(true);
        notification_builder.setSmallIcon(R.drawable.studentwalking);
        notification_builder.setContentIntent(pendingIntent);
        NotificationManager notificationManager = (NotificationManager) getSystemService((Context.NOTIFICATION_SERVICE));
        notificationManager.notify(0, notification_builder.build());
    }
}
