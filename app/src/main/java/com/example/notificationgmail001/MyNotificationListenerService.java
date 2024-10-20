package com.example.notificationgmail001;

import android.app.Notification;
import android.content.Intent;
import android.os.Bundle;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.util.Log;

import java.util.HashSet;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.time.LocalDateTime;
import java.time.Duration;


public class MyNotificationListenerService extends NotificationListenerService {
    private String TAG = "MyNotificationListenerService";

    private HashSet<String> processedNotifications = new HashSet<>();
    private LocalDateTime lastExecutionTime = null; // 前回実行時の時刻を保持する変数（初期値はnull）

    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        Log.d(TAG, "onNotificationPosted()001");

        // 通知が投稿された際の処理
        CharSequence text = sbn.getNotification().tickerText;
        Bundle extras = sbn.getNotification().extras;

        if(text != null && extras != null) {
            String strBody = text.toString();

            String notificationId   = sbn.getPackageName() + ":" + sbn.getId() + ":" +                              ":" + strBody;
            String notificationWhen = sbn.getPackageName() + ":" +               ":" + sbn.getNotification().when + ":" + strBody;

            // すでに処理済みの通知かどうかをチェック
            if (processedNotifications.contains(notificationId) ||
                processedNotifications.contains(notificationWhen) ) {
                // すでに処理済みの通知なので、何もしない
            }
            else {
                processedNotifications.add(notificationId);
                processedNotifications.add(notificationWhen);
                Log.d(TAG, "add:" + notificationId);
                Log.d(TAG, "add:" + notificationWhen);

                Boolean flgSend = false;
                String strSrc = "タイトル："+ extras.getString(Notification.EXTRA_TITLE) + "\n本文：" + strBody;
                Log.d(TAG, strSrc);

                String strDsp = strSrc;
                { // 動作確認用処理
                    String strDspTmp = strSrc;
                    strDspTmp = replaceNtfctnTxt(strDspTmp, "リマインくん", "りりまま");
                    strDspTmp = replaceNtfctnTxt(strDspTmp, "テスト", "ててすすとと");
                    if(strDspTmp != null) {
                        flgSend = true;
                        strDsp = strDspTmp;
                    }
                    else{}
                }

                { // hugnote
                    String strDspTmp = strSrc;
                    strDspTmp = replaceNtfctnTxt(strDspTmp,"hugnote", "hugnote");
                    strDspTmp = replaceNtfctnTxt(strDspTmp,"おのでら", "");
                    if(strDsp != null) {
                        flgSend = true;
                        strDsp = strDspTmp;
                    }
                    else{}
                }

                { // コドモン
                    String strDspTmp = strSrc;
                    strDspTmp = replaceNtfctnTxt(strDspTmp,"【アワーキッズ鎌倉分園】", "【保育園】");
                    strDspTmp = replaceNtfctnTxt(strDspTmp,"小野寺", "");
                    strDspTmp = replaceNtfctnTxt(strDspTmp,"智香", "ともか");
                    if(strDsp != null) {
                        flgSend = true;
                        strDsp = strDspTmp;
                    }
                    else{}
                }

                if(flgSend == true) {
                    // アクティビティが起動しているかどうかに関係なくブロードキャストも送信
                    Intent broadcastIntent = new Intent("com.example.NOTIFICATION_LISTENER");
                    broadcastIntent.putExtra("notification_type", "normal");
                    broadcastIntent.putExtra("notification_title", strDsp);
                    broadcastIntent.putExtra("notification_text", strDsp);
                    sendBroadcast(broadcastIntent);
                }
                else{}

                // ログ表示用に送る(これはメール送信されない)
                Intent broadcastIntent = new Intent("com.example.NOTIFICATION_LISTENER");
                broadcastIntent.putExtra("notification_type", "log");
                broadcastIntent.putExtra("notification_title", strSrc);
                broadcastIntent.putExtra("notification_text", strSrc);
                sendBroadcast(broadcastIntent);
            }
        }
        else{}

        // アクティブな通知をすべて取得する
///        StatusBarNotification[] array = getActiveNotifications();
    }

    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {
        // 通知が削除されたときの処理
        Log.d(TAG, "onNotificationRemoved()001");

        // 通知が投稿された際の処理
        CharSequence text = sbn.getNotification().tickerText;

        if(text != null) {
            String strBody = text.toString();

            String notificationId   = sbn.getPackageName() + ":" + sbn.getId() + ":" +                              ":" + strBody;
            String notificationWhen = sbn.getPackageName() + ":" +               ":" + sbn.getNotification().when + ":" + strBody;

            Log.d(TAG, "remove:" + notificationId);
            Log.d(TAG, "remove:" + notificationWhen);

            // 通知が削除されたらセットから削除する
            processedNotifications.remove(notificationId);
            processedNotifications.remove(notificationWhen);
        }
    }

    private String replaceNtfctnTxt(String srcTxt,String ptnTxt, String rplcTxt) {
        String result = null;

        if(srcTxt != null && ptnTxt != null && rplcTxt != null) {
            Pattern pattern = Pattern.compile(ptnTxt);  // 正規表現パターンをコンパイル
            Matcher matcher = pattern.matcher(srcTxt);  // マッチャーを使用して検索

            // マッチしたかどうかを判定
            if (matcher.find()) {
                result = matcher.replaceAll(rplcTxt);
            }
            else {
                result = null;
            }
        }
        else{}

        return result;
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
