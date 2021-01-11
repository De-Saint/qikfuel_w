/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.qikfuel.basic;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.UniformInterfaceException;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.filter.HTTPBasicAuthFilter;
import com.sun.jersey.core.util.MultivaluedMapImpl;
import javax.ws.rs.core.MediaType;

/**
 *
 * @author Saint
 */
public class SendEmail {

    public static ClientResponse SendSimpleMessage(String recipientEmail, String Subject, String Message) {
        ClientResponse resp = null;
        Client client = Client.create();
        String toEmail = "<" + recipientEmail + ">";
        client.addFilter(new HTTPBasicAuthFilter("api", "key-8e7d298196ba8b34812d2a2fbae0b864"));
        WebResource webResource = client.resource("https://api.mailgun.net/v3/qikfuel.com/messages");
        MultivaluedMapImpl formData = new MultivaluedMapImpl();
        formData.add("from", "Qikfuel <qikfuel1@gmail.com>");
        formData.add("to", toEmail);
        formData.add("subject", Subject);
        formData.add("html", Message);
        try {
            resp = webResource.type(MediaType.APPLICATION_FORM_URLENCODED).post(ClientResponse.class, formData);
        } catch (UniformInterfaceException | ClientHandlerException e) {
            String error = e.getMessage();
            System.out.print(error);
            return resp;
        }
        return resp;
    }
    public static ClientResponse SendContactMessage(String guestEmail, String Subject, String Message) {
        ClientResponse resp = null;
        Client client = Client.create();
        String frmEmail = "<" + guestEmail + ">";
        client.addFilter(new HTTPBasicAuthFilter("api", "key-8e7d298196ba8b34812d2a2fbae0b864"));
        WebResource webResource = client.resource("https://api.mailgun.net/v3/qikfuel.com/messages");
        MultivaluedMapImpl formData = new MultivaluedMapImpl();
        formData.add("from", frmEmail);
        formData.add("to", "Qikfuel <qikfuel1@gmail.com>");
        formData.add("subject", Subject);
        formData.add("text", Message);
        try {
            resp = webResource.type(MediaType.APPLICATION_FORM_URLENCODED).post(ClientResponse.class, formData);
        } catch (UniformInterfaceException | ClientHandlerException e) {
            String error = e.getMessage();
            System.out.print(error);
            return resp;
        }
        return resp;
    }
}
