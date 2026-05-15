package database;

import java.sql.Connection;
import java.sql.DriverManager;

public class DBConnection {

    private static final String URL  =
            "jdbc:mysql://localhost:3306/hospital_sma" +
                    "?useSSL=false&allowPublicKeyRetrieval=true" +
                    "&serverTimezone=UTC&autoReconnect=true";
    private static final String USER = "root";
    private static final String PASS = "1234";

    public static Connection connect() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            return DriverManager.getConnection(URL, USER, PASS);
        } catch (Exception e) {
            System.out.println("✗ Database Error : " + e.getMessage());
            return null;
        }
    }

    public static void closeQuietly(Connection conn) {
        try {
            if (conn != null && !conn.isClosed())
                conn.close();
        } catch (Exception e) {
            System.out.println("✗ Close error : " + e.getMessage());
        }
    }
}