package com.example.notificationgmail001;


import android.util.Log;

import com.google.api.client.googleapis.json.GoogleJsonError;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.GmailScopes;
import com.google.api.services.gmail.model.Message;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.GoogleCredentials;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.Properties;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import org.apache.commons.codec.binary.Base64;

import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import java.util.Date;
import java.util.List;
import com.google.api.services.gmail.model.ListMessagesResponse;
import com.google.api.services.gmail.model.Message;
import com.google.api.services.gmail.model.MessagePart;
import com.google.api.services.gmail.model.MessagePartBody;
import com.google.api.services.gmail.model.MessagePartHeader;

/* Class to demonstrate the use of Gmail Send Message API */
public class SendMessage {
    private static String TAG = "テスト1";

    /**
     * Send an email from the user's mailbox to its recipient.
     *
     * @param fromEmailAddress - Email address to appear in the from: header
     * @param toEmailAddress   - Email address of the recipient
     * @return the sent message, {@code null} otherwise.
     * @throws MessagingException - if a wrongly formatted address is encountered.
     * @throws IOException        - if service account credentials file not found.
     */
    public static Message sendEmail(String fromEmailAddress,
                                    String toEmailAddress,
                                    String sendText,
                                    GoogleAccountCredential argCrdntial)
            throws MessagingException, IOException {
        /* Load pre-authorized user credentials from the environment.
           TODO(developer) - See https://developers.google.com/identity for
            guides on implementing OAuth2 for your application.*/
//        GoogleCredentials credentials = GoogleCredentials.getApplicationDefault()
//                .createScoped(GmailScopes.GMAIL_SEND);
//        HttpRequestInitializer requestInitializer = new HttpCredentialsAdapter(credentials);
        Log.d(TAG, "sendEmail() 001");

        // Create the gmail API client
        Gmail service = new Gmail.Builder(new NetHttpTransport(),
                GsonFactory.getDefaultInstance(),
                argCrdntial)
                .setApplicationName("Hello008")
                .build();

//        // gmailからメールを取得する試しの処理。この部分は動いた！
//        ListMessagesResponse messagesResponse = service.users().messages().list("me").execute();
//        List<Message> messageList = messagesResponse.getMessages();
//
//        // 対象のメールを見つけるためにメールの全データを探索する
//        Message targetMessage = null;
//        for (Message message : messageList) {
//            // メールの全データを取得
//            Message fullMessage = service.users().messages().get("me", message.getId()).execute();
//            // ヘッダーを見る
//            for (MessagePartHeader header : fullMessage.getPayload().getHeaders()) {
//                // ヘッダーの中でNameがSubject(件名)でValueが「テストメール」のメッセージのメールIDを取得
//                if (header.getName().equals("Subject") && header.getValue().equals("テストメール")) {
//                    targetMessage = fullMessage;
//                }
//            }
//        }

        // フォーマットを指定
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd HH:mm", Locale.getDefault());

        // Create the email content
        String messageSubject = "Test message";
        String bodyText = formatter.format(new Date()) + "\n" + sendText;

        // Encode as MIME message
        Properties props = new Properties();
        Session session = Session.getDefaultInstance(props, null);
        MimeMessage email = new MimeMessage(session);
        email.setFrom(new InternetAddress(fromEmailAddress));
        email.addRecipient(javax.mail.Message.RecipientType.TO,
                new InternetAddress(toEmailAddress));
        email.setSubject(messageSubject);
        email.setText(bodyText);

        // Encode and wrap the MIME message into a gmail message
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        email.writeTo(buffer);
        byte[] rawMessageBytes = buffer.toByteArray();
        String encodedEmail = Base64.encodeBase64URLSafeString(rawMessageBytes);
        Message message = new Message();
        message.setRaw(encodedEmail);


/*
        String messageSubject = "Test message";
        String bodyText = "test";
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();

        // Encode as MIME message
        Properties props = new Properties();
        Session session = Session.getDefaultInstance(props, null);
        MimeMessage msg = new MimeMessage(session);
        msg.setFrom(new InternetAddress(fromEmailAddress));
        InternetAddress[] address = InternetAddress.parse(toEmailAddress);
        msg.setRecipients(javax.mail.Message.RecipientType.TO, address);
//        msg.setSubject(messageSubject, Constants.CHARA_SET);
        msg.setSubject(messageSubject);
        msg.setSentDate(new Date());
        // 作成したメッセージをGmail メッセージ形式に変換
        msg.writeTo(buffer);
        byte[] rawMessageBytes = buffer.toByteArray();
        String encodedEmail = new String(Base64.encodeBase64(rawMessageBytes));
        com.google.api.services.gmail.model.Message message = new com.google.api.services.gmail.model.Message();
        message.setRaw(encodedEmail);
*/
        try {
            // Create send message
            message = service.users().messages().send("me", message).execute();
            System.out.println("Message id: " + message.getId());
            System.out.println(message.toPrettyString());
            return message;
        } catch (GoogleJsonResponseException e) {
            // TODO(developer) - handle error appropriately
            GoogleJsonError error = e.getDetails();
            if (error.getCode() == 403) {
                System.err.println("Unable to send message: " + e.getDetails());
            } else {
                throw e;
            }
        }

        return null;
    }

    public static Message fff(String fromEmailAddress,
                                    String toEmailAddress,
                                    GoogleAccountCredential argCrdntial)
            throws MessagingException, IOException {

        String messageSubject = "Test message";
        String bodyText = "test";
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();


        // Encode as MIME message
        Properties props = new Properties();
        Session session = Session.getDefaultInstance(props, null);
        MimeMessage msg = new MimeMessage(session);
        msg.setFrom(new InternetAddress(fromEmailAddress));
        InternetAddress[] address = InternetAddress.parse(toEmailAddress);
        msg.setRecipients(javax.mail.Message.RecipientType.TO, address);
//        msg.setSubject(messageSubject, Constants.CHARA_SET);
        msg.setSubject(messageSubject);
        msg.setSentDate(new Date());
        // 作成したメッセージをGmail メッセージ形式に変換
        msg.writeTo(buffer);
        byte[] rawMessageBytes = buffer.toByteArray();
        String encodedEmail = new String(Base64.encodeBase64(rawMessageBytes));
        com.google.api.services.gmail.model.Message message = new com.google.api.services.gmail.model.Message();
        message.setRaw(encodedEmail);

        return null;
    }
}