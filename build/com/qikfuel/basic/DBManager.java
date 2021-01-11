/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.qikfuel.basic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

/**
 *
 * @author saint
 */
public class DBManager {

    public DBManager() {

    }

    public static int GetInt(String outputColumn, String TableName, String Condition) throws ClassNotFoundException, SQLException {
        Connection con = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        int result = 0;
        String sql = "Select * from " + TableName + " " + Condition;

        try {
            con = new JDBCConnector().getConnection();
            stmt = con.prepareStatement(sql);
            rs = stmt.executeQuery();
            if (rs.last()) {
                result = rs.getInt(outputColumn);
            } else {
                result = 0;
            }
        } catch (SQLException e) {
            String error = e.getMessage();
            System.out.print(error);
            return result;
        } finally {
            if (rs != null) {
                rs.close();
            }
            if (stmt != null) {
                stmt.close();
            }
            if (con != null) {
                con.close();
            }
        }
        return result;
    }

    public static int GetFirstInt(String outputColumn, String TableName, String Condition) throws ClassNotFoundException, SQLException {
        Connection con = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        int result = 0;
        String sql = "Select * from " + TableName + " " + Condition;

        try {
            con = new JDBCConnector().getConnection();
            stmt = con.prepareStatement(sql);
            rs = stmt.executeQuery();
            if (rs.first()) {
                result = rs.getInt(outputColumn);
            } else {
                result = 0;
            }
        } catch (SQLException e) {
            String error = e.getMessage();
            System.out.print(error);
            return result;
        } finally {
            if (rs != null) {
                rs.close();
            }
            if (stmt != null) {
                stmt.close();
            }
            if (con != null) {
                con.close();
            }
        }
        return result;
    }

    public static String GetString(String outputColumn, String TableName, String Condition) throws ClassNotFoundException, SQLException {
        Connection con = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        String result = "none";
        String sql = "Select * from " + TableName + " " + Condition;

        try {
            con = new JDBCConnector().getConnection();
            stmt = con.prepareStatement(sql);
            rs = stmt.executeQuery();
            rs.first();
            if (rs.first()) {
                result = rs.getString(outputColumn);
            } else {
                result = "none";
            }
        } catch (SQLException e) {
            String error = e.getMessage();
            System.out.print(error);
            return result;
        } finally {
            if (rs != null) {
                rs.close();
            }
            if (stmt != null) {
                stmt.close();
            }
            if (con != null) {
                con.close();
            }
        }
        return result;
    }

    public static ArrayList<String> GetStringArrayList(String outputColumn, String TableName, String Condition) throws ClassNotFoundException, SQLException {
        Connection con = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        ArrayList<String> result = new ArrayList<>();
        String sql = "Select * from " + TableName + " " + Condition;

        try {
            con = new JDBCConnector().getConnection();
            stmt = con.prepareStatement(sql);
            rs = stmt.executeQuery();
            rs.first();
            if (rs.last()) {
                result.add(rs.getString(outputColumn));
                while (rs.previous()) {
                    result.add(rs.getString(outputColumn));
                }
            }
        } catch (SQLException e) {
            String error = e.getMessage();
            System.out.print(error);
            return result;
        } finally {
            if (rs != null) {
                rs.close();
            }
            if (stmt != null) {
                stmt.close();
            }
            if (con != null) {
                con.close();
            }
        }
        return result;
    }

    public static ArrayList<Integer> GetIntArrayList(String outputColumn, String TableName, String Condition) throws ClassNotFoundException, SQLException {
        Connection con = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        ArrayList<Integer> result = new ArrayList<>();
        String sql = "Select * from " + TableName + " " + Condition;

        try {
            con = new JDBCConnector().getConnection();
            stmt = con.prepareStatement(sql);
            rs = stmt.executeQuery();
            if (rs.last()) {
                result.add(rs.getInt(outputColumn));
                while (rs.previous()) {
                    result.add(rs.getInt(outputColumn));
                }
            }
        } catch (SQLException e) {
            String error = e.getMessage();
            System.out.print(error);
            return result;
        } finally {
            if (rs != null) {
                rs.close();
            }
            if (stmt != null) {
                stmt.close();
            }
            if (con != null) {
                con.close();
            }
        }
        return result;
    }

    public static ArrayList<Integer> GetIntArrayListDescending(String outputColumn, String TableName, String Condition) throws ClassNotFoundException, SQLException {
        Connection con = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        ArrayList<Integer> result = new ArrayList<>();
        String sql = "Select * from " + TableName + " " + Condition;

        try {
            con = new JDBCConnector().getConnection();
            stmt = con.prepareStatement(sql);
            rs = stmt.executeQuery();
            if (rs.first()) {
                result.add(rs.getInt(outputColumn));
                while (rs.next()) {
                    result.add(rs.getInt(outputColumn));
                }
            }
        } catch (SQLException e) {
            String error = e.getMessage();
            System.out.print(error);
            return result;
        } finally {
            if (rs != null) {
                rs.close();
            }
            if (stmt != null) {
                stmt.close();
            }
            if (con != null) {
                con.close();
            }
        }
        return result;
    }

    public static HashMap<Integer, String> GetIntStringHashMap(String outputColumn1, String outputColumn2, String TableName, String Condition) throws ClassNotFoundException, SQLException {
        Connection con = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        HashMap<Integer, String> result = new HashMap<>();
        String sql = "Select * from " + TableName + " " + Condition;

        try {
            con = new JDBCConnector().getConnection();
            stmt = con.prepareStatement(sql);
            rs = stmt.executeQuery();
            if (rs.last()) {
                result.put(rs.getInt(outputColumn1), rs.getString(outputColumn2));
                while (rs.previous()) {
                    result.put(rs.getInt(outputColumn1), rs.getString(outputColumn2));
                }
            }
        } catch (SQLException e) {
            String error = e.getMessage();
            System.out.print(error);
            return result;
        } finally {
            if (rs != null) {
                rs.close();
            }
            if (stmt != null) {
                stmt.close();
            }
            if (con != null) {
                con.close();
            }
        }
        return result;
    }

    public static HashMap<String, String> GetTableData(String TableName, String Condition) throws ClassNotFoundException, SQLException {
        Connection con = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        HashMap<String, String> result = new HashMap<>();
        String sql = "Select * from " + TableName + " " + Condition;

        try {
            con = new JDBCConnector().getConnection();
            stmt = con.prepareStatement(sql);
            rs = stmt.executeQuery();
            if (rs.last()) {
                ResultSetMetaData rsmd = rs.getMetaData();
                for (int i = 1; i <= rsmd.getColumnCount(); i++) {
                    int type = rsmd.getColumnType(i);
                    String ColumnName = rsmd.getColumnName(i);
                    if (type == Types.VARCHAR || type == Types.CHAR || type == Types.LONGVARCHAR) {
                        result.put(ColumnName, rs.getString(i));
                    } else if (type == Types.INTEGER) {
                        result.put(ColumnName, "" + rs.getInt(i));
                    } else if (type == Types.DATE) {
                        result.put(ColumnName, "" + rs.getDate(i));
                    } else if (type == Types.TIME) {
                        result.put(ColumnName, "" + rs.getTime(i));
                    } else {
                        result.put(ColumnName, "" + rs.getLong(i));
                    }
                }
                while (rs.previous()) {
                    for (int i = 1; i <= rsmd.getColumnCount(); i++) {
                        int type = rsmd.getColumnType(i);
                        String ColumnName = rsmd.getColumnName(i);
                        if (type == Types.VARCHAR || type == Types.CHAR || type == Types.LONGVARCHAR) {
                            result.put(ColumnName, rs.getString(i));
                        } else if (type == Types.INTEGER) {
                            result.put(ColumnName, "" + rs.getInt(i));
                        } else if (type == Types.DATE) {
                            result.put(ColumnName, "" + rs.getDate(i));
                        } else if (type == Types.TIME) {
                            result.put(ColumnName, "" + rs.getTime(i));
                        } else {
                            result.put(ColumnName, "" + rs.getLong(i));
                        }
                    }
                }
            }
        } catch (SQLException e) {
            String error = e.getMessage();
            System.out.print(error);
            return result;
        } finally {
            if (rs != null) {
                rs.close();
            }
            if (stmt != null) {
                stmt.close();
            }
            if (con != null) {
                con.close();
            }
        }
        return result;
    }

    public static HashMap<String, String> GetStringStringHashMap(String outputColumn1, String outputColumn2, String TableName, String Condition) throws ClassNotFoundException, SQLException {
        Connection con = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        HashMap<String, String> result = new HashMap<>();
        String sql = "Select * from " + TableName + " " + Condition;

        try {
            con = new JDBCConnector().getConnection();
            stmt = con.prepareStatement(sql);
            rs = stmt.executeQuery();
            if (rs.last()) {
                result.put(rs.getString(outputColumn1), rs.getString(outputColumn2));
                while (rs.previous()) {
                    result.put(rs.getString(outputColumn1), rs.getString(outputColumn2));
                }
            }
        } catch (SQLException e) {
            String error = e.getMessage();
            System.out.print(error);
            return result;
        } finally {
            if (rs != null) {
                rs.close();
            }
            if (stmt != null) {
                stmt.close();
            }
            if (con != null) {
                con.close();
            }
        }
        return result;
    }

    public static String DeleteObject(String TableName, String Condition) throws ClassNotFoundException, SQLException {
        Connection con = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        String result = "";
        String sql = "Delete from " + TableName + " " + Condition;

        try {
            con = new JDBCConnector().getConnection();
            stmt = con.prepareStatement(sql);
            stmt.executeUpdate();
            result = "successful";
        } catch (SQLException e) {
            String error = e.getMessage();
            System.out.print(error);
            result = "failed";
        } finally {
            if (rs != null) {
                rs.close();
            }
            if (stmt != null) {
                stmt.close();
            }
            if (con != null) {
                con.close();
            }
        }
        return result;
    }

    public static String UpdateCurrentDate(String TableName, String Column, String Condition) throws ClassNotFoundException, SQLException {
        Connection con = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        String result = "";
        String sql = "UPDATE " + TableName + " SET " + Column + " = CURRENT_DATE " + Condition;

        try {
            con = new JDBCConnector().getConnection();
            stmt = con.prepareStatement(sql);
            stmt.executeUpdate();
            result = "successful";
        } catch (SQLException e) {
            String error = e.getMessage();
            System.out.print(error);
            result = "failed";
        } finally {
            if (rs != null) {
                rs.close();
            }
            if (stmt != null) {
                stmt.close();
            }
            if (con != null) {
                con.close();
            }
        }
        return result;
    }

    public static String UpdateCurrentTime(String TableName, String Column, String Condition) throws ClassNotFoundException, SQLException {
        Connection con = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        String result = "";
        String sql = "UPDATE " + TableName + " SET " + Column + " = CURRENT_TIME " + Condition;

        try {
            con = new JDBCConnector().getConnection();
            stmt = con.prepareStatement(sql);
            stmt.executeUpdate();
            result = "successful";
        } catch (SQLException e) {
            String error = e.getMessage();
            System.out.print(error);
            result = "failed";
        } finally {
            if (rs != null) {
                rs.close();
            }
            if (stmt != null) {
                stmt.close();
            }
            if (con != null) {
                con.close();
            }
        }
        return result;
    }

    public static String UpdateCurrentTimeStamp(String TableName, String Column, String Condition) throws ClassNotFoundException, SQLException {
        Connection con = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        String result = "";
        String sql = "UPDATE " + TableName + " SET " + Column + " = CURRENT_TIMESTAMP " + Condition;

        try {
            con = new JDBCConnector().getConnection();
            stmt = con.prepareStatement(sql);
            stmt.executeUpdate();
            result = "successful";
        } catch (SQLException e) {
            String error = e.getMessage();
            System.out.print(error);
            result = "failed";
        } finally {
            if (rs != null) {
                rs.close();
            }
            if (stmt != null) {
                stmt.close();
            }
            if (con != null) {
                con.close();
            }
        }
        return result;
    }

    public static String InsertStringData(String TableName, String inputColumn, String Data, String Condition) throws ClassNotFoundException, SQLException {
        Connection con = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        String result = "failed";
        String sql = "INSERT INTO " + TableName + " (" + inputColumn + ") VALUES (?) " + Condition;

        try {
            con = new JDBCConnector().getConnection();
            stmt = con.prepareStatement(sql);
            stmt.setString(1, Data);
            stmt.executeUpdate();
            result = "success";
        } catch (SQLException e) {
            String error = e.getMessage();
            System.out.print(error);
            return result;
        } finally {
            if (rs != null) {
                rs.close();
            }
            if (stmt != null) {
                stmt.close();
            }
            if (con != null) {
                con.close();
            }
        }
        return result;
    }

    public static String InsertIntData(String inputColumn, int Data, String TableName, String Condition) throws ClassNotFoundException, SQLException {
        Connection con = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        String result = "failed";
        String sql = "INSERT INTO " + TableName + " (" + inputColumn + ") VALUES (" + Data + ") " + Condition;

        try {
            con = new JDBCConnector().getConnection();
            stmt = con.prepareStatement(sql);
            stmt.executeUpdate();
            result = "success";
        } catch (SQLException e) {
            String error = e.getMessage();
            System.out.print(error);
            return result;
        } finally {
            if (rs != null) {
                rs.close();
            }
            if (stmt != null) {
                stmt.close();
            }
            if (con != null) {
                con.close();
            }
        }
        return result;
    }

    public static String UpdateStringData(String TableName, String inputColumn, String Data, String Condition) throws ClassNotFoundException, SQLException {
        Connection con = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        String result = "failed";
        String sql = "Update " + TableName + " SET " + inputColumn + " = ? " + Condition;

        try {
            con = new JDBCConnector().getConnection();
            stmt = con.prepareStatement(sql);
            stmt.setString(1, Data);
            stmt.executeUpdate();
            result = "success";
        } catch (SQLException e) {
            String error = e.getMessage();
            System.out.print(error);
            return result;
        } finally {
            if (rs != null) {
                rs.close();
            }
            if (stmt != null) {
                stmt.close();
            }
            if (con != null) {
                con.close();
            }
        }
        return result;
    }

    public static String UpdateIntData(String inputColumn, int Data, String TableName, String Condition) throws ClassNotFoundException, SQLException {
        Connection con = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        String result = "failed";
        String sql = "Update " + TableName + " SET " + inputColumn + " = ? " + Condition;

        try {
            con = new JDBCConnector().getConnection();
            stmt = con.prepareStatement(sql);
            stmt.setInt(1, Data);
            stmt.executeUpdate();
            result = "success";
        } catch (SQLException e) {
            String error = e.getMessage();
            System.out.print(error);
            return result;
        } finally {
            if (rs != null) {
                rs.close();
            }
            if (stmt != null) {
                stmt.close();
            }
            if (con != null) {
                con.close();
            }
        }
        return result;
    }

    public static String ExchangeforString(String outputColumn, String TableName, String Condition) throws ClassNotFoundException, SQLException {
        Connection con = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        String result = "";
        String sql = "Select * from " + TableName + " " + Condition;

        try {
            con = new JDBCConnector().getConnection();
            stmt = con.prepareStatement(sql);
            rs = stmt.executeQuery();
            rs.first();
            if (rs.first()) {
                result = rs.getString(outputColumn);
            } else {
                result = "none";
            }
        } catch (SQLException e) {
            String error = e.getMessage();
            System.out.print(error);
        } finally {
            if (rs != null) {
                rs.close();
            }
            if (stmt != null) {
                stmt.close();
            }
            if (con != null) {
                con.close();
            }
        }
        return result;
    }

    public static int ExchangeforInt(String outputColumn, String TableName, String Condition) throws ClassNotFoundException, SQLException {
        Connection con = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        int result = 0;
        String sql = "Select * from " + TableName + " " + Condition;

        try {
            con = new JDBCConnector().getConnection();
            stmt = con.prepareStatement(sql);
            rs = stmt.executeQuery();
            if (rs.last()) {
                result = rs.getInt(outputColumn);
            } else {
                result = 0;
            }
        } catch (SQLException e) {
            String error = e.getMessage();
            System.out.print(error);
        } finally {
            if (rs != null) {
                rs.close();
            }
            if (stmt != null) {
                stmt.close();
            }
            if (con != null) {
                con.close();
            }
        }
        return result;
    }

    public static ArrayList<String> ExchangeforStringArrayList(String outputColumn, String TableName, String Condition) throws ClassNotFoundException, SQLException {
        Connection con = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        ArrayList<String> result = new ArrayList<>();
        String sql = "Select * from " + TableName + " " + Condition;

        try {
            con = new JDBCConnector().getConnection();
            stmt = con.prepareStatement(sql);
            rs = stmt.executeQuery();
            rs.first();
            if (rs.first()) {
                result.add(rs.getString(outputColumn));
                while (rs.next()) {
                    result.add(rs.getString(outputColumn));
                }
            }
        } catch (SQLException e) {
            String error = e.getMessage();
            System.out.print(error);
        } finally {
            if (rs != null) {
                rs.close();
            }
            if (stmt != null) {
                stmt.close();
            }
            if (con != null) {
                con.close();
            }
        }
        return result;
    }

    public static ArrayList<Integer> ExchangeforIntArrayList(String outputColumn, String TableName, String Condition) throws ClassNotFoundException, SQLException {
        Connection con = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        ArrayList<Integer> result = new ArrayList<>();
        String sql = "Select * from " + TableName + " " + Condition;

        try {
            con = new JDBCConnector().getConnection();
            stmt = con.prepareStatement(sql);
            rs = stmt.executeQuery();
            rs.first();
            if (rs.first()) {
                result.add(rs.getInt(outputColumn));
                while (rs.next()) {
                    result.add(rs.getInt(outputColumn));
                }
            }
        } catch (SQLException e) {
            String error = e.getMessage();
            System.out.print(error);
        } finally {
            if (rs != null) {
                rs.close();
            }
            if (stmt != null) {
                stmt.close();
            }
            if (con != null) {
                con.close();
            }
        }
        return result;
    }

    public static String GetDate(String outputColumn, String TableName, String Condition) throws ClassNotFoundException, SQLException {
        Connection con = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        String result = "none";
        String sql = "Select * from " + TableName + " " + Condition;

        try {
            con = new JDBCConnector().getConnection();
            stmt = con.prepareStatement(sql);
            rs = stmt.executeQuery();
            rs.first();
            if (rs.first()) {
                result = "" + rs.getDate(outputColumn);
            } else {
                result = "none";
            }
        } catch (SQLException e) {
            String error = e.getMessage();
            System.out.print(error);
            return result;
        } finally {
            if (rs != null) {
                rs.close();
            }
            if (stmt != null) {
                stmt.close();
            }
            if (con != null) {
                con.close();
            }
        }
        return result;
    }

    public static String GetTime(String outputColumn, String TableName, String Condition) throws ClassNotFoundException, SQLException {
        Connection con = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        String result = "none";
        String sql = "Select * from " + TableName + " " + Condition;

        try {
            con = new JDBCConnector().getConnection();
            stmt = con.prepareStatement(sql);
            rs = stmt.executeQuery();
            rs.first();
            if (rs.first()) {
                result = "" + rs.getTime(outputColumn);
            } else {
                result = "none";
            }
        } catch (SQLException e) {
            String error = e.getMessage();
            System.out.print(error);
            return result;
        } finally {
            if (rs != null) {
                rs.close();
            }
            if (stmt != null) {
                stmt.close();
            }
            if (con != null) {
                con.close();
            }
        }
        return result;
    }

    public static String insertTableData(String Tablename, HashMap<String, Object> Data, String Condition) throws ClassNotFoundException, SQLException {
        String result = "failed";
        String dataString = "";
        String Columns = "";
        Set<String> keys = Data.keySet();
        Iterator<String> iterator = keys.iterator();
        while (iterator.hasNext()) {
            String col = iterator.next();
            Columns += "`" + col + "`,";
            Object data = Data.get(col);
            dataString += data + ",";
        }

        dataString = dataString.replaceAll(",$", "");
        Columns = Columns.replaceAll(",$", "");
        Connection con = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        String sql = "INSERT INTO " + Tablename + " (" + Columns + ") VALUES (" + dataString + ") " + Condition;

        try {
            con = new JDBCConnector().getConnection();
            stmt = con.prepareStatement(sql);
            stmt.executeUpdate();
            result = "success";
        } catch (SQLException e) {
            String error = e.getMessage();
            System.out.print(error);
            return error;
        } finally {
            if (rs != null) {
                rs.close();
            }
            if (stmt != null) {
                stmt.close();
            }
            if (con != null) {
                con.close();
            }
        }
        return result;
    }
}
