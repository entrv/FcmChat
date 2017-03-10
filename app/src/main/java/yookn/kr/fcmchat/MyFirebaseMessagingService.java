package yookn.kr.fcmchat;

/**
 * Created by entrv on 2017-03-09.
 */

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.firebase.messaging.RemoteMessage;


public class MyFirebaseMessagingService extends com.google.firebase.messaging.FirebaseMessagingService {
    private static final String TAG = "FirebaseMsgService";
    SharedPreferences prefs;
    NotificationCompat.Builder notification;
    NotificationManager manager;
    // [START receive_message]
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {


        //String messageType = gcm.getMessageType(intent);
        prefs = getSharedPreferences("Chat", 0);
        if(!prefs.getString("CURRENT_ACTIVE","").equals(remoteMessage.getData().get("fromu"))) {
            sendNotification(remoteMessage.getData().get("msg"), remoteMessage.getData().get("fromu"),
                    remoteMessage.getData().get("name"));
        }
        Log.i("entrv", "Received: " + remoteMessage.getNotification().getBody() + ">> current_active : " + prefs.getString("CURRENT_ACTIVE",""));
        //추가한것
        //sendNotification(remoteMessage.getData().get("message"));

        Intent msgrcv = new Intent("Msg");
        msgrcv.putExtra("msg", remoteMessage.getData().get("msg"));
        msgrcv.putExtra("fromu", remoteMessage.getData().get("fromu"));
        msgrcv.putExtra("fromname", remoteMessage.getData().get("name"));


        LocalBroadcastManager.getInstance(this).sendBroadcast(msgrcv);

    }

    private void sendNotification(String msg,String mobno,String name) {

        Bundle args = new Bundle();
        args.putString("mobno", mobno);
        args.putString("name", name);
        args.putString("msg", msg);
        Intent chat = new Intent(this, ChatActivity.class);
        chat.putExtra("INFO", args);
        notification = new NotificationCompat.Builder(this);
        notification.setContentTitle(name);
        notification.setContentText(msg);
        notification.setTicker("New Message !");
        notification.setSmallIcon(R.mipmap.ic_launcher);

        PendingIntent contentIntent = PendingIntent.getActivity(this, 1000,
                chat, PendingIntent.FLAG_CANCEL_CURRENT);
        notification.setContentIntent(contentIntent);
        notification.setAutoCancel(true);
        manager =(NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(0, notification.build());
    }

    private void sendNotification(String messageBody) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("FCM Push Test")
                .setContentText(messageBody)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
    }

}

