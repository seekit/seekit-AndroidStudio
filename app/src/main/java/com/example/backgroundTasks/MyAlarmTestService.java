package com.example.backgroundTasks;



import com.example.seekit.MainActivity;
import com.example.seekit.R;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

public class MyAlarmTestService extends IntentService {
    public MyAlarmTestService() {
       super("MyTestService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
       // Do the task here
       Log.i("MyTestService", "Service running");
       Log.i("MyTestService", intent.getStringExtra("foo"));
       
       

    ////////////////////////////////////////////////////////////////////////////////
    Intent notificationIntent = new Intent(this, MainActivity.class);
    notificationIntent.putExtra("json", intent.getStringExtra("json"));
    notificationIntent.putExtra("PARENT_NAME", "tester");
    PendingIntent contentIntent = PendingIntent.getActivity(this,
            0, notificationIntent,
            PendingIntent.FLAG_CANCEL_CURRENT);

    NotificationManager nm = (NotificationManager) this
            .getSystemService(Context.NOTIFICATION_SERVICE);

    Resources res = this.getResources();
    Notification.Builder builder = new Notification.Builder(this);

    builder.setContentIntent(contentIntent)
                .setSmallIcon(R.drawable.ic_action_new)
                .setTicker(res.getString(R.string.accept))
                .setWhen(System.currentTimeMillis())
                .setAutoCancel(true)
                .setContentTitle("sadasd")
                .setContentText("asdasdasdasdasd");
    Notification n = builder.build();

    nm.notify(3, n);
    
    }


	
}

/*
 *        //lo que vamos a querer es que en cierto momento salte el mensaje de que se perdio el cel.       
       NotificationCompat.Builder mBuilder =
    		    new NotificationCompat.Builder(this)
    		    .setSmallIcon(R.drawable.ic_action_new)
    		    .setContentTitle("My notification")
    		    .setContentText("Hello World!");
      
       
       Intent resultIntent = new Intent(this, MainActivity.class);
    // Because clicking the notification opens a new ("special") activity, there's
    // no need to create an artificial back stack.
    PendingIntent resultPendingIntent =
        PendingIntent.getActivity(
        this,
        0,
        resultIntent,
        PendingIntent.FLAG_UPDATE_CURRENT
    );
    
    mBuilder.setContentIntent(resultPendingIntent);
   	
 // Sets an ID for the notification
    int mNotificationId = 001;
    // Gets an instance of the NotificationManager service
    NotificationManager mNotifyMgr = 
            (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
    // Builds the notification and issues it.
    mNotifyMgr.notify(mNotificationId, mBuilder.build());
    
*/
