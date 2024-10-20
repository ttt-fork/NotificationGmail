package com.example.notificationgmail001;

import android.content.Intent;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.util.Log;

import java.time.LocalDateTime;
import java.time.Duration;


public class MyNotificationListenerService extends NotificationListenerService {
    private String TAG = "MyNotificationListenerService";

    // 前回実行時の時刻を保持する変数（初期値はnull）
    private LocalDateTime lastExecutionTime = null;

    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        // 通知が投稿された際の処理

        Log.d(TAG, "onNotificationPosted()001");

        fff();
    }

    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {
        // 通知が削除されたときの処理
    }

    // fffが呼び出された時に、前回時刻と現在時刻を比較
    private void fff() {
        // 現在時刻を取得
        LocalDateTime currentTime = LocalDateTime.now();

        // 最初の呼び出しの場合、または6時間以上経過している場合にログを表示
        if (lastExecutionTime == null) {
            lastExecutionTime = currentTime;    // 前回時刻を現在時刻で更新
        } else {
            // 経過時間を計算
            Duration duration = Duration.between(lastExecutionTime, currentTime);

            // 6時間（21600秒）以上経過していたらログを出力
            if (duration.getSeconds() >= 6 * 60 * 60) {
///            if (duration.getSeconds() >= 15) {
                Log.d(TAG, "前回の実行から6時間以上経過しました。");

                // アクティビティが起動しているかどうかに関係なくブロードキャストも送信
                Intent broadcastIntent = new Intent("com.example.NOTIFICATION_LISTENER");
                broadcastIntent.putExtra("notification_title", "MyNotificationListenerService:fff()");
                broadcastIntent.putExtra("notification_text", "MyNotificationListenerService:fff()");
                sendBroadcast(broadcastIntent);

                lastExecutionTime = currentTime;    // 前回時刻を現在時刻で更新
            } else {}
        }

    }
}
