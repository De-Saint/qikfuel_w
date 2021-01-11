/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.qikfuel.basic;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author saint
 */
public class SupplierClass {

    public SupplierClass() {

    }

    public static String getTransporterActivationStatus(int TransporterID) throws ClassNotFoundException, SQLException {
        String result = DBManager.GetString("activationstatus", "transporters", "where transporterid =" + TransporterID);
        return result;
    }

    public static String ActivateTransporterStatus(int TransporterID) throws ClassNotFoundException, SQLException {
        String Status = "Activated";
        String result = DBManager.UpdateStringData("transporters", "activationstatus", Status, "where transporterid =" + TransporterID);
        return result;
    }

    public static String DectivateTransporterStatus(int TransporterID) throws ClassNotFoundException, SQLException {
        String Status = "Not Activated";
        String result = DBManager.UpdateStringData("transporters", "activationstatus", Status, "where transporterid =" + TransporterID);
        return result;
    }

    public static ArrayList<Integer> getSupplierTransporterIds(int SupplierID, int start, int limit) throws ClassNotFoundException, SQLException {
        ArrayList<Integer> TransporterIDS = DBManager.GetIntArrayList("ItemTwoID", "users_join", "Where ItemOneType = 'Supplier' AND ItemTwoType = 'Transporter' AND ItemOneID = '" + SupplierID + "'  ORDER by ItemTwoID ASC LIMIT " + start + ", " + limit);
        return TransporterIDS;
    }
    public static ArrayList<Integer> getSupplierTransporterIds(int SupplierID) throws ClassNotFoundException, SQLException {
        ArrayList<Integer> TransporterIDS = DBManager.GetIntArrayList("ItemTwoID", "users_join", "Where ItemOneType = 'Supplier' AND ItemTwoType = 'Transporter' AND ItemOneID = " + SupplierID);
        return TransporterIDS;
    }

    public static HashMap<String, String> getSupplierTransporterDetails(int TransporterId) throws ClassNotFoundException, SQLException {
        HashMap<String, String> Userdetails = new HashMap<>();
        HashMap<String, String> Details = new HashMap<>();
        HashMap<String, String> transporterdetails = new HashMap<>();
        String ActivationStatus = DBManager.GetString("activationstatus", "transporters", "where transporterid = " + TransporterId);
        if (ActivationStatus.equals("Activated")) {
            Userdetails = DBManager.GetTableData("transporters", "where transporterid = " + TransporterId);
            if (!Userdetails.isEmpty()) {
                int UserID = Integer.parseInt(Userdetails.get("userid"));
                Details = DBManager.GetTableData("users", "where userid = '" + UserID + "'");
                transporterdetails.putAll(Details);
                transporterdetails.putAll(Userdetails);
            }

        }

        return transporterdetails;
    }
}
