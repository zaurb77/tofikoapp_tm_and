package com.mangalhousemanager.utils;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Vibrator;
import android.util.Log;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.mangalhousemanager.R;
import com.mangalhousemanager.activity.MainActivity;
import com.mangalhousemanager.activity.TableBookingActivity;

import org.json.JSONObject;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private static final String TAG = "MyFirebaseMsgService";
    @Override
    public void onNewToken(String s) {
        super.onNewToken( s );
        new StoreUserData( this ).setString( Constants.USER_FCM, s );
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.d( TAG, "From: " + remoteMessage.getData() );
        try {
            if (remoteMessage.getData().size() > 0) {
                Log.d( TAG, "Message data payload ==> " + remoteMessage.getData().toString() );
                if (remoteMessage.getData().get( "type" ).equalsIgnoreCase( "booking_receive" )) {
                    sendNotification(
                            this,
                            remoteMessage.getData().get( "title" ),
                            remoteMessage.getData().get( "body" ),
                            remoteMessage.getData().get( "type" ),
                            remoteMessage.getData().get( "booking_date" )
                    );
                } else {
                    sendNotification(
                            this,
                            remoteMessage.getData().get( "title" ),
                            remoteMessage.getData().get( "body" ),
                            remoteMessage.getData().get( "type" ),
                            "" );
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void sendNotification(Context context, String title, String message, String type, String dy) {
        final String CHANNEL_ID = "default";
        final String CHANNEL_NAME = "Default";

        NotificationManager notificationManager = (NotificationManager) context.getSystemService( context.NOTIFICATION_SERVICE );
        Uri defaultSoundUri = RingtoneManager.getDefaultUri( RingtoneManager.TYPE_NOTIFICATION );
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel defaultChannel = new NotificationChannel( CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH );
            defaultChannel.enableVibration( true );
            AudioAttributes attributes = new AudioAttributes.Builder()
                    .setUsage( AudioAttributes.USAGE_NOTIFICATION )
                    .build();
            defaultChannel.setSound( defaultSoundUri, attributes );
            notificationManager.createNotificationChannel( defaultChannel );
        }
        Intent _intent = new Intent();

        if (type.equalsIgnoreCase( "booking_receive" )) {
            _intent = new Intent( this, TableBookingActivity.class );
            _intent.putExtra("bookingDate", dy);
        }else {
            _intent = new Intent( this, MainActivity.class );
        }

        _intent.addFlags( Intent.FLAG_ACTIVITY_CLEAR_TOP );
        PendingIntent pandingIntent = PendingIntent.getActivity( this, 0, _intent, PendingIntent.FLAG_ONE_SHOT );
        pandingIntent = PendingIntent.getActivity( this, 0, _intent, PendingIntent.FLAG_UPDATE_CURRENT );
        NotificationCompat.Builder builder = (NotificationCompat.Builder) new NotificationCompat.Builder( context, CHANNEL_ID )
                .setStyle( new NotificationCompat.BigTextStyle().bigText( title ) )
                .setStyle( new NotificationCompat.BigTextStyle().bigText( message ) )
                .setSmallIcon( R.drawable.ic_app )
                .setPriority( Notification.PRIORITY_HIGH )
                .setContentTitle( title )
                .setContentText( message )
                //.setSound( defaultSoundUri )
                .setAutoCancel( true )
                .setContentIntent( pandingIntent );
        Notification notification = builder.build();
        notification.defaults |= Notification.DEFAULT_VIBRATE;
        notificationManager.notify( 100, notification );
        MediaPlayer mp = MediaPlayer.create( context, R.raw.sound );
        mp.start();
    }
  /*  private void sendNotification(String messageBody, String cmd, String type, String bookingDate) {
        if (_intent!=null)
        _intent.addFlags( Intent.FLAG_ACTIVITY_CLEAR_TOP );

        PendingIntent pendingIntent = PendingIntent.getActivity( this, 0  Request code , _intent, PendingIntent.FLAG_ONE_SHOT );
        NotificationManager notificationManager = (NotificationManager) getSystemService( Context.NOTIFICATION_SERVICE );
        String channelId = "signals";
        Uri sound = Uri.parse( ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + this.getPackageName() + "/" + R.raw.abc );
        Log.d( "aasound",sound.toString());
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder( this, channelId )
                .setSmallIcon( getNotificationIcon() )
                .setContentTitle( getResources().getString( R.string.app_name ) )
                .setContentText( messageBody )
                .setColor( ContextCompat.getColor( this, R.color.colorPrimaryDark ) )
                .setAutoCancel( true )
                .setStyle( new NotificationCompat.BigTextStyle().bigText( messageBody ) )
                .setVibrate( new long[]{100, 200} )
                .setSound( sound )
                .setContentIntent( pendingIntent );
        notificationId++;

        Notification notification = notificationBuilder.build();
        notification.defaults |= Notification.DEFAULT_VIBRATE;
        notificationManager.notify(100, notification);
        MediaPlayer mp= MediaPlayer.create(this, R.raw.sound);
        mp.start();
    }*/
    private int getNotificationIcon() {
        boolean useWhiteIcon = (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP);
        return useWhiteIcon ? R.drawable.ic_black : R.drawable.ic_black;
    }
}



