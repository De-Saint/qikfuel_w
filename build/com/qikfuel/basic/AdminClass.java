/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.qikfuel.basic;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.io.IOException;
import java.text.ParseException;
import javax.naming.NamingException;

/**
 *
 * @author saint
 */
public class AdminClass {

    public AdminClass() {

    }

    public static ArrayList<Integer> getUserIds(int start, int limit) throws ClassNotFoundException, SQLException {
        ArrayList<Integer> IDS = DBManager.GetIntArrayList("userid", "users", "Where type = 'User' ORDER by lastname ASC LIMIT " + start + ", " + limit);
        return IDS;
    }
    public static ArrayList<Integer> getUserIds() throws ClassNotFoundException, SQLException {
        ArrayList<Integer> IDS = DBManager.GetIntArrayList("userid", "users", "Where type = 'User'");
        return IDS;
    }

    public static ArrayList<Integer> getSupplierIds(int start, int limit) throws ClassNotFoundException, SQLException {
        ArrayList<Integer> IDS = DBManager.GetIntArrayList("userid", "users", "Where type = 'Supplier' ORDER by lastname ASC LIMIT " + start + ", " + limit);
        return IDS;
    }
    public static ArrayList<Integer> getSupplierIds() throws ClassNotFoundException, SQLException {
        ArrayList<Integer> IDS = DBManager.GetIntArrayList("userid", "users", "Where type = 'Supplier'");
        return IDS;
    }

    public static ArrayList<Integer> getActivatedSupplierIds() throws ClassNotFoundException, SQLException {
        ArrayList<Integer> IDS = DBManager.GetIntArrayList("supplierid", "suppliers", "Where status = 'Activated'");
        return IDS;
    }

    public static ArrayList<Integer> getNonActivatedSupplierIds() throws ClassNotFoundException, SQLException {
        ArrayList<Integer> IDS = DBManager.GetIntArrayList("supplierid", "suppliers", "Where status = 'Not Activated'");
        return IDS;
    }

    public static ArrayList<Integer> getTransporterIds() throws ClassNotFoundException, SQLException {
        ArrayList<Integer> IDS = DBManager.GetIntArrayList("userid", "users", "Where type = 'Transporter'");
        return IDS;
    }
    public static ArrayList<Integer> getTransporterIds(int start, int limit) throws ClassNotFoundException, SQLException {
        ArrayList<Integer> IDS = DBManager.GetIntArrayList("userid", "users", "Where type = 'Transporter' ORDER by lastname ASC LIMIT " + start + ", " + limit);
        return IDS;
    }

    public static ArrayList<Integer> getActivatedTransporterIds() throws ClassNotFoundException, SQLException {
        ArrayList<Integer> IDS = DBManager.GetIntArrayList("transporterid", "transporters", "Where activationstatus = 'Activated'");
        return IDS;
    }

    public static ArrayList<Integer> getNonActivatedTransporterIds() throws ClassNotFoundException, SQLException {
        ArrayList<Integer> IDS = DBManager.GetIntArrayList("transporterid", "transporters", "Where activationstatus = 'Not Activated'");
        return IDS;
    }

    public static ArrayList<Integer> getSupplierProductIds(int SupplierID) throws ClassNotFoundException, SQLException {
        ArrayList<Integer> IDS = DBManager.GetIntArrayList("ItemTwoID", "products_join", "Where ItemOneType = 'Supplier' AND ItemTwoType = 'Product' AND ItemOneID = " + SupplierID);
        return IDS;
    }

    public static String getSupplierStatus(int SupplierID) throws ClassNotFoundException, SQLException {
        String result = DBManager.GetString("status", "suppliers", "where supplierid =" + SupplierID);
        return result;
    }

    public static String ActivateSupplier(int SupplierID) throws ClassNotFoundException, SQLException {
        String Status = "Activated";
        String result = DBManager.UpdateStringData("suppliers", "status", Status, "where supplierid =" + SupplierID);
        return result;
    }

    public static String DeActivateSupplier(int SupplierID) throws ClassNotFoundException, SQLException {
        String Status = "Not Activated";
        String result = DBManager.UpdateStringData("suppliers", "status", Status, "where supplierid =" + SupplierID);
        return result;
    }

    public static ArrayList<Integer> getTransporterSupplierIds(int TransporterID) throws ClassNotFoundException, SQLException {
        ArrayList<Integer> SupplierIDS = DBManager.GetIntArrayList("ItemOneID", "users_join", "Where ItemOneType = 'Supplier' AND ItemTwoType = 'Transporter' AND ItemTwoID = " + TransporterID);
        return SupplierIDS;
    }

    public static HashMap<String, String> getDetails(int id, String UserType) throws ClassNotFoundException, SQLException {
        HashMap<String, String> getDetails = new HashMap<>();
        HashMap<String, String> Details = new HashMap<>();
        HashMap<String, String> TableDetails = new HashMap<>();
        int UserID = 0;
        if (id != 0) {
            switch (UserType) {
                case "User":
                    Details = DBManager.GetTableData("users", "where userid =" + id);
                    UserID = Integer.parseInt(Details.get("userid"));
                    TableDetails = DBManager.GetTableData("customers", "where userid =" + UserID);
                    getDetails.putAll(Details);
                    getDetails.putAll(TableDetails);
                    break;
                case "Supplier":
                    Details = DBManager.GetTableData("users", "where userid =" + id);
                    UserID = Integer.parseInt(Details.get("userid"));
                    TableDetails = DBManager.GetTableData("suppliers", "where userid =" + UserID);
                    getDetails.putAll(Details);
                    getDetails.putAll(TableDetails);
                    break;
                case "Transporter":
                    Details = DBManager.GetTableData("users", "where userid =" + id);
                    UserID = Integer.parseInt(Details.get("userid"));
                    TableDetails = DBManager.GetTableData("transporters", "where userid =" + UserID);
                    getDetails.putAll(Details);
                    getDetails.putAll(TableDetails);
                    break;
                case "Admin":
                    Details = DBManager.GetTableData("users", "where userid =" + id);
                    UserID = Integer.parseInt(Details.get("userid"));
                    TableDetails = DBManager.GetTableData("admins", "where userid =" + UserID);
                    getDetails.putAll(Details);
                    getDetails.putAll(TableDetails);
                    break;
            }
        }
        return getDetails;
    }

    public static HashMap<String, String> SearchDetails(int id, String UserType) throws ClassNotFoundException, SQLException {
        HashMap<String, String> getDetails = new HashMap<>();
        HashMap<String, String> Details = new HashMap<>();
        HashMap<String, String> TableDetails = new HashMap<>();
        int UserID = 0;
        if (id != 0) {
            switch (UserType) {
                case "User":
                    Details = DBManager.GetTableData("users", "where type = 'User' and userid =" + id);
                    if (!Details.isEmpty()) {
                        UserID = Integer.parseInt(Details.get("userid"));
                        TableDetails = DBManager.GetTableData("customers", "where userid =" + UserID);
                        getDetails.putAll(Details);
                        getDetails.putAll(TableDetails);
                    }
                    break;
                case "Transporter":
                    Details = DBManager.GetTableData("users", "where type = 'Transporter' and userid =" + id);
                    if (!Details.isEmpty()) {
                        UserID = Integer.parseInt(Details.get("userid"));
                        TableDetails = DBManager.GetTableData("transporters", "where userid =" + UserID);
                        getDetails.putAll(Details);
                        getDetails.putAll(TableDetails);
                    }
                    break;
            }
        }
        return getDetails;
    }

    public static HashMap<String, String> getUserDetails(int id, String UserType) throws ClassNotFoundException, SQLException {
        HashMap<String, String> getUserDetails = new HashMap<>();
        HashMap<String, String> TableDetails = new HashMap<>();
        HashMap<String, String> GasDetails = new HashMap<>();
        if (id != 0) {
            switch (UserType) {
                case "User":
                    TableDetails = DBManager.GetTableData("customers", "where customerid =" + id);
                    getUserDetails.putAll(TableDetails);
                    break;
                case "Supplier":
                    TableDetails = DBManager.GetTableData("suppliers", "where supplierid =" + id);
                    getUserDetails.putAll(TableDetails);
                    break;
                case "Transporter":
                    TableDetails = DBManager.GetTableData("transporters", "where transporterid =" + id);
                    getUserDetails.putAll(TableDetails);
                    break;
                case "Admin":
                    TableDetails = DBManager.GetTableData("admins", "where adminid =" + id);
                    getUserDetails.putAll(TableDetails);
                    break;
                case "Product":
                    TableDetails = DBManager.GetTableData("products", "where productid =" + id);
                    getUserDetails.putAll(TableDetails);

                    break;
            }
        }
        return getUserDetails;
    }

    public static HashMap<String, String> getUserdetails(int id, String UserType) throws ClassNotFoundException, SQLException {

        HashMap<String, String> getUserDetails = new HashMap<>();
        HashMap<String, String> TableDetails = new HashMap<>();
        HashMap<String, String> Details = new HashMap<>();
        int UserID = 0;
        if (id != 0) {
            switch (UserType) {
                case "User":
                    TableDetails = DBManager.GetTableData("customers", "where customerid =" + id);
                    UserID = Integer.parseInt(TableDetails.get("userid"));
                    Details = DBManager.GetTableData("users", "where userid =" + UserID);
                    getUserDetails.putAll(TableDetails);
                    getUserDetails.putAll(Details);
                    break;
                case "Supplier":
                    TableDetails = DBManager.GetTableData("suppliers", "where supplierid =" + id);
                    UserID = Integer.parseInt(TableDetails.get("userid"));
                    Details = DBManager.GetTableData("users", "where userid =" + UserID);
                    getUserDetails.putAll(TableDetails);
                    getUserDetails.putAll(Details);
                    break;
                case "Transporter":
                    TableDetails = DBManager.GetTableData("transporters", "where transporterid =" + id);
                    UserID = Integer.parseInt(TableDetails.get("userid"));
                    Details = DBManager.GetTableData("users", "where userid =" + UserID);
                    getUserDetails.putAll(TableDetails);
                    getUserDetails.putAll(Details);
                    break;
                case "Admin":
                    TableDetails = DBManager.GetTableData("admins", "where adminid =" + id);
                    UserID = Integer.parseInt(TableDetails.get("userid"));
                    Details = DBManager.GetTableData("users", "where userid =" + UserID);
                    getUserDetails.putAll(TableDetails);
                    getUserDetails.putAll(Details);
                    break;
                case "Product":
                    TableDetails = DBManager.GetTableData("products", "where productid =" + id);
                    getUserDetails.putAll(TableDetails);
                    break;
            }
        }
        return getUserDetails;
    }

    public static String DeleteUserdetails(int id, String UserType) throws ClassNotFoundException, SQLException, IOException, NamingException, ParseException {
        HashMap<String, String> TableDetails = new HashMap<>();
        int UserID = 0;
        int UID = 0;
        String result = "";
        String UserName = "";
        if (id != 0) {
            switch (UserType) {
                case "User":
                    UserName = DBManager.GetString("lastname", "users", "where userid =" + id) + " " + DBManager.GetString("firstname", "users", "where userid =" + id);
                    result = DBManager.DeleteObject("users", "where userid =" + id);
                    TableDetails = DBManager.GetTableData("customers", "where userid =" + id);
                    UserID = Integer.parseInt(TableDetails.get("customerid"));
                    DBManager.DeleteObject("customers", "where customerid =" + UserID);
                    DBManager.UpdateIntData("customerid", 0, "orders", "where customerid =" + UserID);
                    DBManager.UpdateIntData("member_to_id", 0, "messages", "where member_to_id =" + id);
                    DBManager.UpdateIntData("customerid", 0, "transactions", "where customerid =" + UserID);
                    UserClass.sendMemberMessage(UserID, "User Account Deleted", UserName + " account has been Deleted", 1);
                    break;
                case "Supplier":
                    TableDetails = DBManager.GetTableData("suppliers", "where supplierid =" + id);
                    UserID = Integer.parseInt(TableDetails.get("userid"));
                    UserName = DBManager.GetString("lastname", "users", "where userid =" + UserID) + " " + DBManager.GetString("firstname", "users", "where userid =" + UserID);
                    result = DBManager.DeleteObject("users", "where userid =" + UserID);
                    DBManager.DeleteObject("suppliers", "where supplierid =" + id);
                    DBManager.UpdateIntData("supplierid", 0, "orders", "where supplierid =" + id);
                    DBManager.UpdateIntData("member_to_id", 0, "messages", "where member_to_id =" + UserID);
                    DBManager.UpdateIntData("supplierid", 0, "transactions", "where supplierid =" + id);
                    UserClass.sendMemberMessage(UserID, "User Account Deleted", "User Account with ID " + UserID + " has been Deleted", 1);
                    ArrayList<Integer> IDS = SupplierClass.getSupplierTransporterIds(id);
                    if (!IDS.isEmpty()) {
                        for (int tid : IDS) {
                            if (tid != 0) {
                                DBManager.DeleteObject("users_join", "where ItemTwoID =" + tid);
                                TableDetails = DBManager.GetTableData("transporters", "where transporterid =" + tid);
                                UID = Integer.parseInt(TableDetails.get("userid"));
                                result = DBManager.DeleteObject("users", "where userid =" + UID);
                                DBManager.DeleteObject("transporters", "where transporterid =" + tid);
                                DBManager.UpdateIntData("transporterid", 0, "orders", "where transporterid =" + tid);
                                DBManager.UpdateIntData("member_to_id", 0, "messages", "where member_to_id =" + UID);
                                DBManager.UpdateIntData("transporterid", 0, "transactions", "where transporterid =" + tid);
                            }
                        }
                    }

                    ArrayList<Integer> IDs = AdminClass.getSupplierProductIds(id);
                    if (!IDS.isEmpty()) {
                        for (int pid : IDs) {
                            result = DBManager.DeleteObject("products", "where productid =" + pid);
                            DBManager.DeleteObject("products_join", "Where ItemOneType = 'Supplier' AND ItemTwoType = 'Product' AND ItemTwoID = " + pid);
                        }
                    }
                    ArrayList<Integer> AccountIDS = DBManager.GetIntArrayList("accountid", "accounts", "where supplierid =" + id);
                    if (!AccountIDS.isEmpty()) {
                        for (int acctid : AccountIDS) {
                            DBManager.DeleteObject("accounts", "where accountid =" + acctid);
                        }
                    }
                    break;
                case "Transporter":
                    TableDetails = DBManager.GetTableData("transporters", "where transporterid =" + id);
                    UserID = Integer.parseInt(TableDetails.get("userid"));
                    UserName = DBManager.GetString("lastname", "users", "where userid =" + UserID) + " " + DBManager.GetString("firstname", "users", "where userid =" + UserID);
                    result = DBManager.DeleteObject("users", "where userid =" + UserID);
                    DBManager.DeleteObject("transporters", "where transporterid =" + id);
                    DBManager.UpdateIntData("ItemTwoID", 0, "users_join", "where ItemTwoID =" + id);
                    DBManager.UpdateIntData("transporterid", 0, "orders", "where transporterid =" + id);
                    DBManager.UpdateIntData("member_to_id", 0, "messages", "where member_to_id =" + UserID);
                    DBManager.UpdateIntData("transporterid", 0, "transactions", "where transporterid =" + id);
                    UserClass.sendMemberMessage(UserID, "User Account Deleted", "User Account with ID " + UserID + " has been Deleted", 1);
                    break;
            }
        }
        return result;
    }

    public static String DeleteOrder(int OrderID, String UserType) throws ClassNotFoundException, SQLException, IOException, NamingException, ParseException {
        HashMap<String, String> details = new HashMap<>();
        String result = "";
        int UserID = 0;
        details = DBManager.GetTableData("orders", "where orderid =" + OrderID);
        String OrderNumber = details.get("ordernumber");
        switch (UserType) {
            case "User":
                UserID = Integer.parseInt(details.get("customerid"));
                result = DBManager.UpdateIntData("customerid", 0, "orders", "where orderid =" + OrderID);
                UserClass.sendMemberMessage(UserID, "An Order with Order Number " + OrderNumber + " has been Deleted", "Order Deleted", 1);
                break;
            case "Supplier":
                UserID = Integer.parseInt(details.get("supplierid"));
                result = DBManager.UpdateIntData("supplierid", 0, "orders", "where orderid =" + OrderID);
                UserClass.sendMemberMessage(UserID, "An Order with Order Number " + OrderNumber + " has been Deleted", "Order Deleted", 1);
                break;
            case "Transporter":
                UserID = Integer.parseInt(details.get("transporterid"));
                result = DBManager.UpdateIntData("transporterid", 0, "orders", "where orderid =" + OrderID);
                UserClass.sendMemberMessage(UserID, "An Order with Order Number " + OrderNumber + " has been Deleted", "Order Deleted", 1);
                break;
        }
        return result;
    }

    public static String DeleteTransactions(int TransID, String UserType) throws ClassNotFoundException, SQLException, IOException, NamingException, ParseException {
        HashMap<String, String> details = new HashMap<>();
        String result = "";
        int UserID = 0;
        details = DBManager.GetTableData("transactions", "where transactionid =" + TransID);
        String Descrpition = details.get("description");
        switch (UserType) {
            case "User":
                UserID = Integer.parseInt(details.get("customerid"));
                result = DBManager.UpdateIntData("customerid", 0, "transactions", "where transactionid =" + TransID);
                UserClass.sendMemberMessage(UserID, "Transaction details: " + Descrpition + " has been Deleted", "Transaction Deleted", 1);
                break;
            case "Supplier":
                UserID = Integer.parseInt(details.get("supplierid"));
                result = DBManager.UpdateIntData("supplierid", 0, "transactions", "where transactionid =" + TransID);
                UserClass.sendMemberMessage(UserID, "Transaction details: " + Descrpition + " has been Deleted", "Transaction Deleted", 1);
                break;
            case "Transporter":
                UserID = Integer.parseInt(details.get("transporterid"));
                result = DBManager.UpdateIntData("transporterid", 0, "transactions", "where transactionid =" + TransID);
                UserClass.sendMemberMessage(UserID, "Transaction details: " + Descrpition + " has been Deleted", "Transaction Deleted", 1);
                break;
        }
        return result;
    }

}
