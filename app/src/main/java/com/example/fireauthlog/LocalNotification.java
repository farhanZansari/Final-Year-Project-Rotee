package com.example.fireauthlog;
import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;
// Notification Class
public class LocalNotification extends Application {

    public static final String Notification1 = "null";

    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationchannel();
    }

    private void createNotificationchannel() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel channel = new NotificationChannel(
                    Notification1,null, NotificationManager.IMPORTANCE_HIGH
            );
            channel.setDescription("Plates are purchased");
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }
    }
}
