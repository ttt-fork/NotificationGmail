package com.example.notificationgmail001;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.net.Uri;
import android.app.Activity;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.Scope;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.json.gson.GsonFactory;

import java.util.Collections;

public class MainActivity extends AppCompatActivity {
    private String TAG = "MainActivity";
    private Button mBtnNotificationListenerSettings = null;
    private Button mBtnAuthGoogle = null;
    private Button mBtnMailSend = null;
    private EditText mETxtLog = null;
    private NotificationReceiver notificationReceiver = null;
    private String mNotificationLog = "";

    private static final int REQUEST_CODE_SIGN_IN = 1;
    private static final int REQUEST_CODE_OPEN_DOCUMENT = 2;

    private SendMessage mSndMsg = null;
    GoogleAccountCredential mCrdntial = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Log.d(TAG, "onCreate()001");

        // ログ表示表のエディットテキスト
        mETxtLog = findViewById(R.id.editText);

        // ブロードキャストレシーバーのインスタンスを作成
        notificationReceiver = new NotificationReceiver();
        IntentFilter filter = new IntentFilter("com.example.NOTIFICATION_LISTENER");
        registerReceiver(notificationReceiver, filter);

        // 通知リスナ設定ボタンイベント
        mBtnNotificationListenerSettings = (Button) findViewById(R.id.btnNotificationListenerSettings);
        mBtnNotificationListenerSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent_1=new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS");
                startActivity(intent_1);
            }
        });

        // 通知リスナ設定ボタンイベント
        mBtnAuthGoogle = (Button) findViewById(R.id.btnAuthGoogle);
        mBtnAuthGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestSignIn();
            }
        });

        // メール送信ボタンイベント
        mBtnMailSend = (Button) findViewById(R.id.btnMailSend);
        mBtnMailSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Runnable runnable = new Runnable(){
                    @Override
                    public void run(){
                        try{
                            doSend("メール送信ボタンイベント");
                        }catch(Exception e){
                            Log.e(TAG, "Exception:" + e.toString());
                        }
                    }
                };

                Thread thread = new Thread(runnable);
                thread.start();
            }
        });
    }

    private void requestSignIn() {
        Log.d(TAG, "Requesting sign-in");

        GoogleSignInOptions signInOptions =
                new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestEmail()
                        .requestScopes(new Scope("https://mail.google.com/"))
                        .build();
        GoogleSignInClient client = GoogleSignIn.getClient(this, signInOptions);

        // The result of the sign-in Intent is handled in onActivityResult.
        startActivityForResult(client.getSignInIntent(), REQUEST_CODE_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent resultData) {
        switch (requestCode) {
            case REQUEST_CODE_SIGN_IN:
                if (resultCode == Activity.RESULT_OK && resultData != null) {
                    Log.d(TAG, "Requesting sign-in OK");

                    handleSignInResult(resultData);
                }
                else
                {
                    Log.d(TAG, "Requesting sign-in NG");
                }
                break;

            case REQUEST_CODE_OPEN_DOCUMENT:
                if (resultCode == Activity.RESULT_OK && resultData != null) {
                    Uri uri = resultData.getData();
                    if (uri != null) {
///                        openFileFromFilePicker(uri);
                    }
                }
                break;
        }

        super.onActivityResult(requestCode, resultCode, resultData);
    }

    private void handleSignInResult(Intent result) {
        GoogleSignIn.getSignedInAccountFromIntent(result)
                .addOnSuccessListener(googleAccount -> {
                    Log.d(TAG, "Signed in as " + googleAccount.getEmail());

                    // Use the authenticated account to sign in to the Drive service.
                    GoogleAccountCredential credential =
                            GoogleAccountCredential.usingOAuth2(
                                    this, Collections.singleton("https://mail.google.com/"));
                    credential.setSelectedAccount(googleAccount.getAccount());

                    mCrdntial = credential;
                })
                .addOnFailureListener(exception -> Log.e(TAG, "Unable to sign in.", exception));
    }


    void doSend(String sendText) throws Exception{
        Log.d(TAG, "doSend() start");

        mSndMsg.sendEmail("onod0601@gmail.com","onod0601@gmail.com", sendText, mCrdntial);

        Log.d(TAG, "doSend() end");
    }

    // ブロードキャストを受け取るためのレシーバークラス
    private class NotificationReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            // ブロードキャストからデータを取得
            String type = intent.getStringExtra("notification_type");
            String title = intent.getStringExtra("notification_title");
            String text = intent.getStringExtra("notification_text");

            // 取得したデータをTextViewに設定
            if (text != null) {
                if( type.equals("normal") ) {
                    // メール送信
                    Runnable runnable = new Runnable(){
                        @Override
                        public void run(){
                            try{
                                doSend(text);
                            }catch(Exception e){
                                Log.e(TAG, "Exception:" + e.toString());
                            }
                        }
                    };

                    Thread thread = new Thread(runnable);
                    thread.start();
                }
                else {
                    // ログの記録
                    mNotificationLog = mNotificationLog + "\n" + text.replaceAll("[\n\r]", "");
                    mETxtLog.setText(mNotificationLog);
                }
            }
        }
    }

}