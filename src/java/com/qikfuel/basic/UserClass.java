/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.qikfuel.basic;

import java.sql.Date;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Random;

/**
 *
 * @author saint
 */
public class UserClass {

    public UserClass() {

    }

    public static java.sql.Date CurrentDate() throws ParseException {
        Calendar currentdate = Calendar.getInstance();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MMM-dd");
        String Placeholder = formatter.format(currentdate.getTime());
        java.util.Date datenow = formatter.parse(Placeholder);
        java.sql.Date CurrentDate = new Date(datenow.getTime());
        return CurrentDate;
    }

    public static boolean checkEmailAddressOrPhoneNumberExist(String EmailAddress) throws ClassNotFoundException, SQLException {
        boolean result = false;
        int usid = DBManager.GetInt("userid", "users", "where email = '" + EmailAddress + "' or phone = '" + EmailAddress + "'");
        if (usid != 0) {
            result = true;
        }
        return result;
    }

    public static int checkPasswordEmailMatch(String Password, String Email_PhoneNum) throws ClassNotFoundException, SQLException {
        int result = 0;
        String memPassword = "";
        String email = Email_PhoneNum;
        memPassword = DBManager.GetString("password", "users", "where email = '" + Email_PhoneNum + "'");
        if (memPassword.equals("none")) {
            memPassword = DBManager.GetString("password", "users", "where phone = '" + Email_PhoneNum + "'");
            email = DBManager.GetString("email", "users", "where phone = '" + Email_PhoneNum + "'");
        }
        if (memPassword.equals(Password)) {
            result = DBManager.GetInt("userid", "users", "where email = '" + email + "'");
        }
        return result;
    }

    public static String getUserType(int UserID) throws ClassNotFoundException, SQLException {
        String result = DBManager.GetString("type", "users", "where userid =" + UserID);
        return result;
    }

    public static int CreateUser(String FirstName, String LastName, String EmailAddress, String PhoneNumber, String password, Date DateCreated, String Subclass) throws ClassNotFoundException, SQLException {
        HashMap<String, Object> tableData = new HashMap<>();
        tableData.put("firstname", "'" + FirstName + "'");
        tableData.put("lastname", "'" + LastName + "'");
        tableData.put("email", "'" + EmailAddress + "'");
        tableData.put("phone", "'" + PhoneNumber + "'");
        tableData.put("password", "'" + password + "'");
        tableData.put("date_created", "'" + DateCreated + "'");
        tableData.put("type", "'" + Subclass + "'");
        String result = DBManager.insertTableData("users", tableData, "");
        int userId = 0;
        if (result.equals("success")) {
            userId = DBManager.GetInt("userid", "users", "where email = '" + EmailAddress + "'");
        }
        return userId;
    }

    public static String CreateRecovery(int userID, String question, String answer) throws ClassNotFoundException, SQLException {
        HashMap<String, Object> data = new HashMap<>();
        data.put("userid", userID);
        data.put("question", "'" + question + "'");
        data.put("answer", "'" + answer + "'");
        String result = DBManager.insertTableData("recovery", data, "");
        return result;
    }

    public static String CreateCustomer(String Address, int userId, String Status) throws ClassNotFoundException, SQLException {
        HashMap<String, Object> tableData = new HashMap<>();
        tableData.put("address", "'" + Address + "'");
        tableData.put("userid", "'" + userId + "'");
        tableData.put("status", "'" + Status + "'");
        String result = DBManager.insertTableData("customers", tableData, "");
        return result;
    }

    public static String CreateAdmin(String Address, int userId, String Permissions) throws ClassNotFoundException, SQLException {
        HashMap<String, Object> tableData = new HashMap<>();
        tableData.put("address", "'" + Address + "'");
        tableData.put("userid", "'" + userId + "'");
        tableData.put("permissionids", "'" + Permissions + "'");
        String result = DBManager.insertTableData("customers", tableData, "");
        return result;
    }

    public static int CreateSupplier(int userId, String Address, String DPRNumber, String CompanyName, String SupplierCode, String SupStatus, String PayCharges, String Stateid, String Lgaid, int Townid) throws ClassNotFoundException, SQLException {
        int bookings = 10;
        int currentbookings = 0;
        HashMap<String, Object> tableData = new HashMap<>();
        tableData.put("userid", "'" + userId + "'");
        tableData.put("address", "'" + Address + "'");
        tableData.put("dprnumber", "'" + DPRNumber + "'");
        tableData.put("company_name", "'" + CompanyName + "'");
        tableData.put("suppliercode", "'" + SupplierCode + "'");
        tableData.put("status", "'" + SupStatus + "'");
        tableData.put("paycharges", "'" + PayCharges + "'");
        tableData.put("bookings", "'" + bookings + "'");
        tableData.put("currentbookings", "'" + currentbookings + "'");
        tableData.put("stateid", "'" + Stateid + "'");
        tableData.put("lgaid", "'" + Lgaid + "'");
        tableData.put("townid", "'" + Townid + "'");
        String result = DBManager.insertTableData("suppliers", tableData, "");
        int supplierid = 0;
        if (result.equals("success")) {
            supplierid = DBManager.GetInt("supplierid", "suppliers", "where userid = '" + userId + "'");
        }
        return supplierid;
    }

    public static String CreateSupplierCode(int LengthOfCode) {
        String SupplierCode = "";
        char[] chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890".toCharArray();
        StringBuilder sb = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < LengthOfCode; i++) {
            char c = chars[random.nextInt(chars.length)];
            sb.append(c);
        }
        SupplierCode = sb.toString();
        return SupplierCode;
    }

    public static String CreateSupplierProduct(int SupplierID, int ProductID) throws ClassNotFoundException, SQLException {
        HashMap<String, Object> tableData = new HashMap<>();
        tableData.put("ItemOneType", "'Supplier'");
        tableData.put("ItemTwoType", "'Product'");
        tableData.put("ItemOneID", "'" + SupplierID + "'");
        tableData.put("ItemTwoID", "'" + ProductID + "'");
        String result = DBManager.insertTableData("products_join", tableData, "");
        return result;
    }

    public static int CreateProduct(int Quantity, int Price, String Type, int MinQuantity) throws ClassNotFoundException, SQLException {
        HashMap<String, Object> tableData = new HashMap<>();
        tableData.put("quantity", "'" + Quantity + "'");
        tableData.put("price", "'" + Price + "'");
        tableData.put("type", "'" + Type + "'");
        tableData.put("minquantity", "'" + MinQuantity + "'");
        String result = DBManager.insertTableData("products", tableData, "");
        int supplierid = 0;
        if (result.equals("success")) {
            supplierid = DBManager.GetInt("productid", "products", "where type = '" + Type + "'");
        }
        return supplierid;
    }

    public static int CreateTransporter(int userId, String Address, String PlateNumber, int TankCapacity, String EngineNuber, String ActivationStatus) throws ClassNotFoundException, SQLException {
        HashMap<String, Object> tableData = new HashMap<>();
        tableData.put("userid", "'" + userId + "'");
        tableData.put("address", "'" + Address + "'");
        tableData.put("activationstatus", "'" + ActivationStatus + "'");
        tableData.put("platenumber", "'" + PlateNumber + "'");
        tableData.put("enginenumber", "'" + EngineNuber + "'");
        tableData.put("tankcapacity", "'" + TankCapacity + "'");
        String result = DBManager.insertTableData("transporters", tableData, "");
        int transporterid = 0;
        if (result.equals("success")) {
            transporterid = DBManager.GetInt("transporterid", "transporters", "where userid = '" + userId + "'");
        }
        return transporterid;
    }

    public static String CreateTransporterSupplier(int SupplierID, int TransporterID) throws ClassNotFoundException, SQLException {
        HashMap<String, Object> tableData = new HashMap<>();
        tableData.put("ItemOneType", "'Supplier'");
        tableData.put("ItemTwoType", "'Transporter'");
        tableData.put("ItemOneID", "'" + SupplierID + "'");
        tableData.put("ItemTwoID", "'" + TransporterID + "'");
        String result = DBManager.insertTableData("users_join", tableData, "");
        return result;
    }

    public static void sendMemberMessage(int ToMemberId, String bdy, String sub, int FromMemberId) throws ClassNotFoundException, SQLException, ParseException {
        String Time = CurrentTime();
        HashMap<String, Object> data = new HashMap<>();
        data.put("date", "CURRENT_DATE");
        data.put("time", "'" + Time + "'");
        data.put("subject", "'" + sub + "'");
        data.put("is_read", 1);
        data.put("from_member_id", FromMemberId);
        data.put("to_member_id", ToMemberId);
        data.put("body", "'" + bdy + "'");
        DBManager.insertTableData("messages", data, "");

    }

    public static String CurrentTime() throws ParseException {
        Calendar currentdate = Calendar.getInstance();
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");
        String CurrentTime = formatter.format(currentdate.getTime());
        return CurrentTime;
    }
}
