/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.qikfuel.basic;

import com.plivo.helper.api.client.RestAPI;
import com.plivo.helper.api.response.message.MessageResponse;
import com.plivo.helper.exception.PlivoException;
import java.util.LinkedHashMap;

/**
 *
 * @author stdee
 */
public class SendSMS {

    public static void main(String[] args) {

    }

    public static MessageResponse SendSimpleSMS(String PhoneTo, String Msg) {
        if (PhoneTo.length() == 11) {
            PhoneTo = PhoneTo.substring(1);//08059330008
            PhoneTo = "+234" + PhoneTo;//+2348059330008
        } else if (PhoneTo.length() == 13) {
            PhoneTo = "+" + PhoneTo;//+2348059330008
        }
        MessageResponse msgResponse = null;
        String authId = "MANZM1MGY5YTK2NWRMZM";
        String authToken = "Njc2ODM0MjM3YTIzN2Q2MWM5ODNlNjZjMTA0N2Yw";
        RestAPI api = new RestAPI(authId, authToken, "v1");

        LinkedHashMap<String, String> parameters = new LinkedHashMap<String, String>();
        parameters.put("src", "+2348059330008"); // Sender's phone number with country code
        parameters.put("dst", PhoneTo); // Receiver's phone number with country code
        parameters.put("text", Msg); // Your SMS text message
        parameters.put("method", "GET"); // The method used to call the url

        try {
            // Send the message
            msgResponse = api.sendMessage(parameters);

            // Print the response
            System.out.println(msgResponse);
            // Print the Api ID
            System.out.println("Api ID : " + msgResponse.apiId);
            // Print the Response Message
            System.out.println("Message : " + msgResponse.message);

            if (msgResponse.serverCode == 202) {
                // Print the Message UUID
                System.out.println("Message UUID : " + msgResponse.messageUuids.get(0).toString());
            } else {
                System.out.println(msgResponse.error);
            }
        } catch (PlivoException e) {
            System.out.println(e.getLocalizedMessage());
        }
        return msgResponse;
    }
}
