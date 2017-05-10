package ge.bondx.fcgagramessanger;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.apache.http.NameValuePair;

import java.io.Serializable;
import java.util.ArrayList;

import ge.bondx.fcgagramessanger.models.Contact;
import ge.bondx.fcgagramessanger.service.JSONHttpClient;
import ge.bondx.fcgagramessanger.service.ServiceUrl;

/**
 * Created by Admin on 4/23/2017.
 */

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "MyFirebaseMsgService";
    public static final String INTENT_FILTER = "INTENT_FILTER";
    public static final String PREFS_NAME = "perf_general";
    private static final String PREF_USERNAME = "username";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        String message = null;
        String title = null;
        String link = "";
        String from = "";
        int notification_id = 0;

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            message = remoteMessage.getNotification().getBody();
            title = remoteMessage.getNotification().getTitle();

            if(remoteMessage.getData().size() > 0){
                link = remoteMessage.getData().get("link");
                notification_id = Integer.parseInt(remoteMessage.getData().get("message_id"));
                from = remoteMessage.getData().get("from_email");
            }
            sendNotification(message,title,link , notification_id, from);
        }
        else if (remoteMessage.getData().size() > 0) {
            message = remoteMessage.getData().get("message");
            title = remoteMessage.getData().get("title");
            link = remoteMessage.getData().get("link");
            from = remoteMessage.getData().get("from_email");
            notification_id = Integer.parseInt(remoteMessage.getData().get("message_id"));

            sendNotification(message, title, link, notification_id, from);
        }

        Intent intent = new Intent(INTENT_FILTER);
        intent.putExtra("from",from);
        intent.putExtra("message",message);
        intent.putExtra("message_id",notification_id);
        sendBroadcast(intent);
    }

    //This method is only generating push notification
    //It is same as we did in earlier posts
    private void sendNotification(String messageBody, String title, String link, int notification_id, String from) {

        SharedPreferences pref = getSharedPreferences(PREFS_NAME,MODE_PRIVATE);
        String username = pref.getString(PREF_USERNAME, null);

        Intent intent = new Intent(this, MessagingActivity.class);
        JSONHttpClient jsonHttpClient = new JSONHttpClient();
        Contact c = jsonHttpClient.Get(ServiceUrl.CONTACTS + "/Get?email="+from, new ArrayList<NameValuePair>(), Contact.class);
        Contact me = jsonHttpClient.Get(ServiceUrl.CONTACTS + "/Get?email="+username, new ArrayList<NameValuePair>(), Contact.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("Contact", (Serializable) c);
        intent.putExtra("me", (Serializable)me);
        //intent.putExtra("NOTIFY_ID", notification_id);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,2, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(title)
                .setContentText(messageBody)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(notification_id, notificationBuilder.build());

        //DatabaseHandler db = new DatabaseHandler(this);
        //db.addNotification(new GasfNotify(notification_id,title,messageBody,link,false));

        //Integer badgeCount = db.getUnSeenNotifications().size();
        //ShortcutBadger.applyCount(this, badgeCount);
        //updateUi(intent);
    }

}
