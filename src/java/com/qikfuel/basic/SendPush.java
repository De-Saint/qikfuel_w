/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.qikfuel.basic;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 *
 * @author stdee
 */
public class SendPush {

    public SendPush() {

    }

    public static HttpURLConnection sendPushNotification(String title, String message, int userid) throws Exception {
        HttpURLConnection conn = null;
        String SERVER_KEY = "AAAAeQ12sTM:APA91bFZVUlsOkDWKtCxYAiOHmI8iNupTYsYqO0IiK31_l9s6teWvZxWRjyyFYBcv0jadv1eobbmcuZZ8TcLPB7tsQTOwVbr6Krb_Jb3KViLt4JGACzK1Ln_N93RBwfP1A2OXtKQbujv";
         String DEVICE_TOKEN = DBManager.GetString("devicetoken", "users", "where userid = " + userid);
        String pushMessage = "{\"data\":{\"title\":\""
                + title
                + "\",\"message\":\""
                + message
                + "\"},\"to\":\""
                + DEVICE_TOKEN
                + "\"}";
        try {
            URL url = new URL("https://fcm.googleapis.com/fcm/send");
            conn = (HttpURLConnection) url.openConnection();

            conn.setRequestProperty(
                    "Authorization", "key=" + SERVER_KEY);
            conn.setRequestProperty(
                    "Content-Type", "application/json");
            conn.setRequestMethod(
                    "POST");
            conn.setDoOutput(
                    true);

            // Send FCM message content.
            OutputStream outputStream = conn.getOutputStream();

            outputStream.write(pushMessage.getBytes());

            System.out.println(conn.getResponseCode());
            System.out.println(conn.getResponseMessage());
            int code = conn.getResponseCode();
            String result = conn.getResponseMessage();
            String me = result;
        } catch (Exception e) {
            String error = e.getMessage();
            System.out.print(error);
            return conn;
        }
        // Create connection to send FCM Message request.

        return conn;

    }

}
