package database;

import java.sql.Connection;
import java.sql.DriverManager;

public class DBConnection {

    public static Connection connect() {

        try {

            Connection conn =
                    DriverManager.getConnection(
                            "jdbc:mysql://localhost:3306/hospital_sma",
                            "root",
                            "1234"
                    );

            System.out.println(
                    "Database Connected"
            );

            return conn;

        } catch (Exception e) {

            e.printStackTrace();
        }

        return null;
    }
}