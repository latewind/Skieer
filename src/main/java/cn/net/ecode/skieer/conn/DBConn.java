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

    public static void main(String [] args){

      Connection conn=  DBConn.getConnection();
      DBConn.closeAll(conn,null,null);
    }

        public static Connection getConnection() {
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

