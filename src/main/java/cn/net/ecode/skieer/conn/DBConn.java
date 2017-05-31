package cn.net.ecode.skieer.conn;

import cn.net.ecode.skieer.config.JSONConfig;

import java.sql.*;

/**
 * 数据库JDBC
 * Created by Li Shang Qing on 2017/5/26.
 */
public class DBConn {
        private final static String USERNAME = JSONConfig.getInstance().getDbUserName();// 用户名
        private final static String PASSWORD = JSONConfig.getInstance().getDbPassWord();// 密码
        private final static String DRIVER_NAME = JSONConfig.getInstance().getDbDriverName();//驱动名
        private final static String URL = JSONConfig.getInstance().getDbUrl();// url
        private static ThreadLocal<Connection> connHolder= new ThreadLocal<Connection>(){
            @Override
            protected Connection initialValue() {
                Connection conn = null;
                try {
                    Class.forName(DRIVER_NAME);
                    conn = DriverManager.getConnection(URL, USERNAME, PASSWORD);
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                return conn;
            }
        };

    public static Connection getConnection(){

        return  connHolder.get();
    }

    public static void close(){
        try {
            connHolder.get().close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public static void main(String [] args){

      Connection conn=  DBConn.getConnection();
      DBConn.closeAll(conn,null,null);
    }

        // 释放资源，注意关闭的顺序
        public static void closeAll(Connection conn, Statement stmt, ResultSet rs) {

            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            //
            if (stmt != null) {
                try {
                    stmt.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            //
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

