package com.iispl.cts.data;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class CTSStaticData {

    // =====================================================
    // DATABASE CONFIGURATION
    // =====================================================

    private static final String DRIVER =
            "org.postgresql.Driver";

    private static final String DB_URL =
            "jdbc:postgresql://aws-0-ap-southeast-1.pooler.supabase.com:5432/postgres"
            + "?sslmode=require";

    private static final String DB_USER =
            "postgres.bijnscklhxftxritxdrc";

    private static final String DB_PASSWORD ="Sushmabandari@123";
           

    // =====================================================
    // LOAD POSTGRESQL DRIVER
    // =====================================================

    static {
        try {

            Class.forName(DRIVER);

            System.out.println(
                    "PostgreSQL JDBC Driver loaded successfully."
            );

        } catch (ClassNotFoundException e) {

            throw new ExceptionInInitializerError(e);
        }
    }


    // =====================================================
    // DATABASE CONNECTION
    // =====================================================

    public static Connection getConnection()
            throws SQLException {

        if (DB_PASSWORD == null
                || DB_PASSWORD.trim().isEmpty()) {

            throw new SQLException(
                    "CTS_DB_PASSWORD environment variable is not set."
            );
        }

        return DriverManager.getConnection(
                DB_URL,
                DB_USER,
                DB_PASSWORD
        );
    }
}