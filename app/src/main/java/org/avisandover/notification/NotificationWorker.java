package org.avisandover.notification;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import org.avisandover.MainActivity;
import org.avisandover.R;
import org.avisandover.event.MyEventActivity;

import java.util.Map;

public class NotificationWorker extends Worker {
    private String eventTime;
    private Context mContext;
    private static final String TAG = "NotificationWorker";

    public NotificationWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        Map<String, Object> values = workerParams.getInputData().getKeyValueMap();
        eventTime = (String) values.get("eventTime");
        mContext = context;
    }

    @Override
    public Result doWork() {
        String message = "Hi, you have an upcoming event tomorrow at " + eventTime + ".";
        Intent intent = new Intent(mContext, MyEventActivity.class);
        intent.putExtra("event_type", 1);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(mContext, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(mContext, MainActivity.CHANNEL_ID)
                .setSmallIcon(R.drawable.sc_connect_circular)
                .setContentTitle("AVIS Notification")
                .setContentText(message)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(mContext);

        // notificationId is a unique int for each notification that you must define
        notificationManager.notify(1583645, builder.build());
        return Result.success();
    }
}
