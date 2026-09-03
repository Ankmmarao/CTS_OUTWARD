
package com.iispl.cts.data;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import com.iispl.cts.model.outward.OutwardBatch;
import com.iispl.cts.model.outward.OutwardCheque;

public class CTSStaticData {

    // =========================================================
    // DATABASE CONFIGURATION
    // =========================================================

	public static final String DRIVER = "org.postgresql.Driver";
	/* * Supabase Session Pooler PostgreSQL connection. */ 
	public static final String 
	DB_URL = "jdbc:postgresql://aws-0-ap-southeast-1.pooler.supabase.com:5432/postgres" + "?sslmode=require";
	public static final String DB_USER = "postgres.bijnscklhxftxritxdrc";

    /*
     * KEEP YOUR EXISTING DATABASE PASSWORD HERE.
     *
     * Do not use the password shown in previous messages.
     */
    public static final String DB_PASSWORD =
            "Sushmabandari@123";

    // =========================================================
    // LOAD POSTGRESQL DRIVER
    // =========================================================

    static {

        try {

            Class.forName(DRIVER);

            System.out.println(
                    "PostgreSQL JDBC Driver loaded successfully."
            );

        } catch (ClassNotFoundException e) {

            System.err.println(
                    "PostgreSQL JDBC Driver not found."
            );

            e.printStackTrace();
        }
    }

    // =========================================================
    // DATABASE CONNECTION
    // =========================================================

    private static Connection getConnection()
            throws SQLException {

        System.out.println(
                "======================================"
        );

        System.out.println(
                "DATABASE CONNECTION"
        );

        System.out.println(
                "======================================"
        );

        System.out.println(
                "Database URL  = " + DB_URL
        );

        System.out.println(
                "Database User = " + DB_USER
        );

        try {

            Connection connection =
                    DriverManager.getConnection(
                            DB_URL,
                            DB_USER,
                            DB_PASSWORD
                    );

            System.out.println(
                    "PostgreSQL connection SUCCESS."
            );

            return connection;

        } catch (SQLException e) {

            System.err.println(
                    "======================================"
            );

            System.err.println(
                    "POSTGRESQL CONNECTION FAILED"
            );

            System.err.println(
                    "======================================"
            );

            System.err.println(
                    "SQLState  = " + e.getSQLState()
            );

            System.err.println(
                    "ErrorCode = " + e.getErrorCode()
            );

            System.err.println(
                    "Message   = " + e.getMessage()
            );

            e.printStackTrace();

            throw e;
        }
    }

    // =========================================================
    // 1. MAKER DASHBOARD - GET ALL BATCHES
    // =========================================================

    public static List<OutwardBatch> getMakerDashboardBatches() {

        List<OutwardBatch> batches =
                new ArrayList<>();

        String sql =
                "SELECT "
                + "ob.batch_id, "
                + "ob.total_cheques, "
                + "ob.status, "
                + "ob.user_id, "
                + "ob.assignment "
                + "FROM public.outward_batch ob "
                + "ORDER BY ob.batch_id";

        System.out.println(
                "======================================"
        );

        System.out.println(
                "MAKER DASHBOARD - LOAD BATCHES"
        );

        System.out.println(
                "======================================"
        );

        System.out.println(
                "SQL = " + sql
        );

        try (
                Connection con = getConnection();

                PreparedStatement ps =
                        con.prepareStatement(sql);

                ResultSet rs =
                        ps.executeQuery()
        ) {

            System.out.println(
                    "SQL QUERY EXECUTED SUCCESSFULLY."
            );

            int rowCount = 0;

            while (rs.next()) {

                rowCount++;

                OutwardBatch batch =
                        new OutwardBatch();

                String batchId =
                        rs.getString("batch_id");

                batch.setBatchId(batchId);

                batch.setTotalCheques(
                        rs.getInt("total_cheques")
                );

                batch.setStatus(
                        rs.getString("status")
                );

                batch.setUserId(
                        rs.getString("user_id")
                );

                // -------------------------------------------------
                // ASSIGNMENT
                // -------------------------------------------------

                String assignment =
                        rs.getString("assignment");

                if (assignment == null
                        || assignment.trim().isEmpty()) {

                    if (batch.getUserId() == null
                            || batch.getUserId()
                                    .trim().isEmpty()) {

                        batch.setAssignment(
                                "AVAILABLE"
                        );

                    } else {

                        batch.setAssignment(
                                "ASSIGNED"
                        );
                    }

                } else {

                    batch.setAssignment(
                            assignment
                    );
                }

                // -------------------------------------------------
                // CALCULATE ACTUAL CHEQUE ERRORS
                // -------------------------------------------------

                try {

                    List<OutwardCheque> cheques =
                            getMakerDashboardCheques(
                                    batchId
                            );

                    int dataEntryErrors = 0;
                    int micrErrors = 0;
                    int amountAccountErrors = 0;

                    for (OutwardCheque cheque : cheques) {

                        if (isDataEntryError(cheque)) {

                            dataEntryErrors++;
                        }

                        if (isMicrError(cheque)) {

                            micrErrors++;
                        }

                        if (isAmountAccountError(cheque)) {

                            amountAccountErrors++;
                        }
                    }

                    batch.setDataEntryErrorCount(
                            dataEntryErrors
                    );

                    batch.setMicrErrorCount(
                            micrErrors
                    );

                    batch.setAmountAccountErrorCount(
                            amountAccountErrors
                    );

                    batch.refreshTotalErrorCount();

                    System.out.println(
                            "Batch " + batchId
                            + " -> Cheques="
                            + cheques.size()
                            + ", DataEntryErrors="
                            + dataEntryErrors
                            + ", MICRErrors="
                            + micrErrors
                            + ", AmountAccountErrors="
                            + amountAccountErrors
                    );

                } catch (Exception e) {

                    System.err.println(
                            "ERROR calculating cheque errors for batch: "
                            + batchId
                    );

                    e.printStackTrace();

                    batch.setDataEntryErrorCount(0);
                    batch.setMicrErrorCount(0);
                    batch.setAmountAccountErrorCount(0);

                    batch.refreshTotalErrorCount();
                }

                // -------------------------------------------------
                // ALWAYS ADD BATCH
                // -------------------------------------------------

                batches.add(batch);

                System.out.println(
                        "BATCH FOUND:"
                        + " ID=" + batch.getBatchId()
                        + ", TOTAL=" + batch.getTotalCheques()
                        + ", STATUS=" + batch.getStatus()
                        + ", USER=" + batch.getUserId()
                        + ", ASSIGNMENT=" + batch.getAssignment()
                );
            }

            System.out.println(
                    "TOTAL BATCHES FOUND = "
                    + rowCount
            );

        } catch (SQLException e) {

            System.err.println(
                    "======================================"
            );

            System.err.println(
                    "DATABASE ERROR - MAKER DASHBOARD"
            );

            System.err.println(
                    "======================================"
            );

            System.err.println(
                    "SQLState  = " + e.getSQLState()
            );

            System.err.println(
                    "ErrorCode = " + e.getErrorCode()
            );

            System.err.println(
                    "Message   = " + e.getMessage()
            );

            e.printStackTrace();

            throw new RuntimeException(
                    "Unable to load maker dashboard batches",
                    e
            );
        }

        System.out.println(
                "Maker Dashboard Batches Loaded = "
                + batches.size()
        );

        return batches;
    }

    // =========================================================
    // 2. GET ALL CHEQUES OF A BATCH
    // =========================================================

    public static List<OutwardCheque>
    getMakerDashboardCheques(String batchId) {

        List<OutwardCheque> cheques =
                new ArrayList<>();

        String sql =
                "SELECT "
                + "oc.cheque_id, "
                + "oc.batch_id, "
                + "oc.cheque_number, "
                + "oc.account_number, "
                + "oc.cheque_date, "
                + "oc.amount, "
                + "oc.front_image, "
                + "oc.back_image, "
                + "oc.scanned_micr, "
                + "oc.corrected_micr, "
                + "oc.corrected_account_number, "
                + "oc.corrected_cheque_date, "
                + "oc.corrected_amount "
                + "FROM public.outward_cheque oc "
                + "WHERE oc.batch_id = ? "
                + "ORDER BY oc.cheque_id";

        try (
                Connection con = getConnection();

                PreparedStatement ps =
                        con.prepareStatement(sql)
        ) {

            ps.setString(1, batchId);

            try (
                    ResultSet rs =
                            ps.executeQuery()
            ) {

                while (rs.next()) {

                    cheques.add(
                            mapCheque(rs)
                    );
                }
            }

        } catch (SQLException e) {

            System.err.println(
                    "ERROR loading cheques for batch: "
                    + batchId
            );

            System.err.println(
                    "SQLState  = " + e.getSQLState()
            );

            System.err.println(
                    "ErrorCode = " + e.getErrorCode()
            );

            System.err.println(
                    "Message   = " + e.getMessage()
            );

            e.printStackTrace();

            /*
             * Important:
             * Do not hide the database error.
             */
            throw new RuntimeException(
                    "Unable to load cheques for batch "
                    + batchId,
                    e
            );
        }

        return cheques;
    }

    // =========================================================
    // 3. DATA ENTRY ERROR BATCHES
    // =========================================================

    public static List<OutwardBatch>
    getDataEntryBatches() {

        return getBatchesByDynamicError(
                "DATA_ENTRY"
        );
    }

    // =========================================================
    // 4. MICR ERROR BATCHES
    // =========================================================

    public static List<OutwardBatch>
    getMicrRepairBatches() {

        return getBatchesByDynamicError(
                "MICR"
        );
    }

    // =========================================================
    // 5. AMOUNT / ACCOUNT ERROR BATCHES
    // =========================================================

    public static List<OutwardBatch>
    getAmountAccountBatches() {

        return getBatchesByDynamicError(
                "AMOUNT_ACCOUNT"
        );
    }

    // =========================================================
    // 6. DYNAMIC ERROR BATCH QUERY
    // =========================================================

    private static List<OutwardBatch>
    getBatchesByDynamicError(
            String errorCategory) {

        List<OutwardBatch> batches =
                new ArrayList<>();

        String sql =
                "SELECT DISTINCT "
                + "ob.batch_id, "
                + "ob.total_cheques, "
                + "ob.status, "
                + "ob.user_id, "
                + "ob.assignment "
                + "FROM public.outward_batch ob "
                + "JOIN public.outward_cheque oc "
                + "ON oc.batch_id = ob.batch_id "
                + "WHERE ob.status <> 'COMPLETED' ";

        if ("MICR".equals(errorCategory)) {

            sql +=
                    "AND ( "
                    + "oc.scanned_micr IS NULL "
                    + "OR TRIM(oc.scanned_micr) = '' "
                    + "OR oc.scanned_micr !~ '^[0-9]{9}$' "
                    + ") ";

        } else if ("DATA_ENTRY".equals(errorCategory)) {

            sql +=
                    "AND ( "
                    + "oc.cheque_number IS NULL "
                    + "OR TRIM(oc.cheque_number) = '' "
                    + "OR oc.cheque_date IS NULL "
                    + ") ";

        } else if ("AMOUNT_ACCOUNT".equals(errorCategory)) {

            sql +=
                    "AND ( "
                    + "oc.account_number IS NULL "
                    + "OR TRIM(oc.account_number) = '' "
                    + "OR oc.amount IS NULL "
                    + "OR oc.amount <= 0 "
                    + ") ";
        }

        sql +=
                "ORDER BY ob.batch_id";

        try (
                Connection con = getConnection();

                PreparedStatement ps =
                        con.prepareStatement(sql);

                ResultSet rs =
                        ps.executeQuery()
        ) {

            while (rs.next()) {

                OutwardBatch batch =
                        new OutwardBatch();

                batch.setBatchId(
                        rs.getString("batch_id")
                );

                batch.setTotalCheques(
                        rs.getInt("total_cheques")
                );

                batch.setStatus(
                        rs.getString("status")
                );

                batch.setUserId(
                        rs.getString("user_id")
                );

                String assignment =
                        rs.getString("assignment");

                if (assignment == null
                        || assignment.trim().isEmpty()) {

                    if (batch.getUserId() == null
                            || batch.getUserId()
                                    .trim().isEmpty()) {

                        batch.setAssignment(
                                "AVAILABLE"
                        );

                    } else {

                        batch.setAssignment(
                                "ASSIGNED"
                        );
                    }

                } else {

                    batch.setAssignment(
                            assignment
                    );
                }

                batches.add(batch);
            }

        } catch (SQLException e) {

            System.err.println(
                    "ERROR loading "
                    + errorCategory
                    + " batches"
            );

            e.printStackTrace();
        }

        return batches;
    }

    // =========================================================
    // 7. DATA ENTRY CHEQUES
    // =========================================================

    public static List<OutwardCheque>
    getDataEntryCheques(String batchId) {

        return getChequesByDynamicError(
                batchId,
                "DATA_ENTRY"
        );
    }

    // =========================================================
    // 8. MICR CHEQUES
    // =========================================================

    public static List<OutwardCheque>
    getMicrCheques(String batchId) {

        return getChequesByDynamicError(
                batchId,
                "MICR"
        );
    }

    // =========================================================
    // 9. AMOUNT / ACCOUNT CHEQUES
    // =========================================================

    public static List<OutwardCheque>
    getAmountAccountCheques(String batchId) {

        return getChequesByDynamicError(
                batchId,
                "AMOUNT_ACCOUNT"
        );
    }

    // =========================================================
    // 10. DYNAMIC CHEQUE QUERY
    // =========================================================

    private static List<OutwardCheque>
    getChequesByDynamicError(
            String batchId,
            String errorCategory) {

        List<OutwardCheque> cheques =
                new ArrayList<>();

        String sql =
                "SELECT "
                + "oc.cheque_id, "
                + "oc.batch_id, "
                + "oc.cheque_number, "
                + "oc.account_number, "
                + "oc.cheque_date, "
                + "oc.amount, "
                + "oc.front_image, "
                + "oc.back_image, "
                + "oc.scanned_micr, "
                + "oc.corrected_micr, "
                + "oc.corrected_account_number, "
                + "oc.corrected_cheque_date, "
                + "oc.corrected_amount "
                + "FROM public.outward_cheque oc "
                + "WHERE oc.batch_id = ? ";

        if ("MICR".equals(errorCategory)) {

            sql +=
                    "AND ( "
                    + "oc.scanned_micr IS NULL "
                    + "OR TRIM(oc.scanned_micr) = '' "
                    + "OR oc.scanned_micr !~ '^[0-9]{9}$' "
                    + ") ";

        } else if ("DATA_ENTRY".equals(errorCategory)) {

            sql +=
                    "AND ( "
                    + "oc.cheque_number IS NULL "
                    + "OR TRIM(oc.cheque_number) = '' "
                    + "OR oc.cheque_date IS NULL "
                    + ") ";

        } else if ("AMOUNT_ACCOUNT".equals(errorCategory)) {

            sql +=
                    "AND ( "
                    + "oc.account_number IS NULL "
                    + "OR TRIM(oc.account_number) = '' "
                    + "OR oc.amount IS NULL "
                    + "OR oc.amount <= 0 "
                    + ") ";
        }

        sql +=
                "ORDER BY oc.cheque_id";

        try (
                Connection con = getConnection();

                PreparedStatement ps =
                        con.prepareStatement(sql)
        ) {

            ps.setString(1, batchId);

            try (
                    ResultSet rs =
                            ps.executeQuery()
            ) {

                while (rs.next()) {

                    cheques.add(
                            mapCheque(rs)
                    );
                }
            }

        } catch (SQLException e) {

            System.err.println(
                    "ERROR loading "
                    + errorCategory
                    + " cheques for batch "
                    + batchId
            );

            e.printStackTrace();
        }

        return cheques;
    }

    // =========================================================
    // 11. CHECKER BATCHES
    // =========================================================

    public static List<OutwardBatch>
    getCheckerBatches() {

        List<OutwardBatch> batches =
                new ArrayList<>();

        String sql =
                "SELECT "
                + "ob.batch_id, "
                + "ob.total_cheques, "
                + "ob.status, "
                + "ob.user_id, "
                + "ob.assignment "
                + "FROM public.outward_batch ob "
                + "WHERE ob.status = 'READY_FOR_CHECKER' "
                + "ORDER BY ob.batch_id";

        try (
                Connection con = getConnection();

                PreparedStatement ps =
                        con.prepareStatement(sql);

                ResultSet rs =
                        ps.executeQuery()
        ) {

            while (rs.next()) {

                OutwardBatch batch =
                        new OutwardBatch();

                batch.setBatchId(
                        rs.getString("batch_id")
                );

                batch.setTotalCheques(
                        rs.getInt("total_cheques")
                );

                batch.setStatus(
                        rs.getString("status")
                );

                batch.setUserId(
                        rs.getString("user_id")
                );

                batch.setAssignment(
                        "ASSIGNED"
                );

                batches.add(batch);
            }

        } catch (SQLException e) {

            e.printStackTrace();
        }

        return batches;
    }

    // =========================================================
    // 12. ASSIGN BATCH TO MAKER
    // =========================================================

    public static boolean assignBatch(
            String batchId,
            String userId) {

        Connection con = null;

        try {

            con = getConnection();

            con.setAutoCommit(false);

            // -------------------------------------------------
            // LOCK BATCH
            // -------------------------------------------------

            String checkSql =
                    "SELECT status "
                    + "FROM public.outward_batch "
                    + "WHERE batch_id = ? "
                    + "FOR UPDATE";

            try (
                    PreparedStatement ps =
                            con.prepareStatement(checkSql)
            ) {

                ps.setString(1, batchId);

                try (
                        ResultSet rs =
                                ps.executeQuery()
                ) {

                    if (!rs.next()) {

                        con.rollback();

                        return false;
                    }

                    String status =
                            rs.getString("status");

                    if (!"AVAILABLE".equalsIgnoreCase(
                            status)) {

                        con.rollback();

                        return false;
                    }
                }
            }

            // -------------------------------------------------
            // INSERT ASSIGNMENT
            // -------------------------------------------------

            String insertSql =
                    "INSERT INTO public.batch_assignment "
                    + "(batch_id, user_id, assignment_type, status) "
                    + "VALUES (?, ?, 'MAKER', 'ACTIVE')";

            try (
                    PreparedStatement ps =
                            con.prepareStatement(insertSql)
            ) {

                ps.setString(1, batchId);
                ps.setString(2, userId);

                ps.executeUpdate();
            }

            // -------------------------------------------------
            // UPDATE BATCH
            // -------------------------------------------------

            String updateSql =
                    "UPDATE public.outward_batch "
                    + "SET status = 'ASSIGNED', "
                    + "user_id = ?, "
                    + "assignment = 'ASSIGNED', "
                    + "updated_at = CURRENT_TIMESTAMP "
                    + "WHERE batch_id = ?";

            try (
                    PreparedStatement ps =
                            con.prepareStatement(updateSql)
            ) {

                ps.setString(1, userId);
                ps.setString(2, batchId);

                ps.executeUpdate();
            }

            con.commit();

            return true;

        } catch (SQLException e) {

            e.printStackTrace();

            if (con != null) {

                try {

                    con.rollback();

                } catch (SQLException ignored) {
                }
            }

            return false;

        } finally {

            if (con != null) {

                try {

                    con.setAutoCommit(true);
                    con.close();

                } catch (SQLException ignored) {
                }
            }
        }
    }

    // =========================================================
    // 13. FIND SINGLE CHEQUE
    // =========================================================

    public static OutwardCheque findCheque(
            String batchId,
            String chequeId) {

        String sql =
                "SELECT "
                + "oc.cheque_id, "
                + "oc.batch_id, "
                + "oc.cheque_number, "
                + "oc.account_number, "
                + "oc.cheque_date, "
                + "oc.amount, "
                + "oc.front_image, "
                + "oc.back_image, "
                + "oc.scanned_micr, "
                + "oc.corrected_micr, "
                + "oc.corrected_account_number, "
                + "oc.corrected_cheque_date, "
                + "oc.corrected_amount "
                + "FROM public.outward_cheque oc "
                + "WHERE oc.batch_id = ? "
                + "AND oc.cheque_id = ?";

        try (
                Connection con = getConnection();

                PreparedStatement ps =
                        con.prepareStatement(sql)
        ) {

            ps.setString(1, batchId);
            ps.setString(2, chequeId);

            try (
                    ResultSet rs =
                            ps.executeQuery()
            ) {

                if (rs.next()) {

                    return mapCheque(rs);
                }
            }

        } catch (SQLException e) {

            e.printStackTrace();
        }

        return null;
    }

    // =========================================================
    // 14. COMPLETE DATA ENTRY CHEQUE
    // =========================================================

    public static boolean completeDataEntryCheque(
            String batchId,
            String chequeId) {

        return updateBatchIfCompleted(
                batchId
        );
    }

    // =========================================================
    // 15. COMPLETE MICR CHEQUE
    // =========================================================

    public static boolean completeMicrCheque(
            String batchId,
            String chequeId) {

        return updateBatchIfCompleted(
                batchId
        );
    }

    // =========================================================
    // 16. COMPLETE AMOUNT / ACCOUNT CHEQUE
    // =========================================================

    public static boolean completeAmountAccountCheque(
            String batchId,
            String chequeId) {

        return updateBatchIfCompleted(
                batchId
        );
    }

    // =========================================================
    // 17. CHECK WHETHER BATCH IS FULLY VALID
    // =========================================================

    public static boolean isBatchValid(
            String batchId) {

        List<OutwardCheque> cheques =
                getMakerDashboardCheques(batchId);

        if (cheques == null
                || cheques.isEmpty()) {

            return false;
        }

        for (OutwardCheque cheque : cheques) {

            if (isDataEntryError(cheque)
                    || isMicrError(cheque)
                    || isAmountAccountError(cheque)) {

                return false;
            }
        }

        return true;
    }

    // =========================================================
    // 18. UPDATE BATCH AFTER REPAIR
    // =========================================================

    public static boolean updateBatchIfCompleted(
            String batchId) {

        if (!isBatchValid(batchId)) {

            return false;
        }

        String sql =
                "UPDATE public.outward_batch "
                + "SET status = 'READY_FOR_CHECKER', "
                + "updated_at = CURRENT_TIMESTAMP "
                + "WHERE batch_id = ?";

        try (
                Connection con = getConnection();

                PreparedStatement ps =
                        con.prepareStatement(sql)
        ) {

            ps.setString(1, batchId);

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {

            e.printStackTrace();

            return false;
        }
    }

    // =========================================================
    // 19. DATA ENTRY VALIDATION
    // =========================================================

    private static boolean isDataEntryError(
            OutwardCheque cheque) {

        if (cheque == null) {

            return true;
        }

        String chequeNumber =
                cheque.getChequeNumber();

        if (isBlank(chequeNumber)) {

            return true;
        }

        String chequeDate =
                cheque.getChequeDate();

        if (isBlank(chequeDate)) {

            return true;
        }

        try {

            Date.valueOf(chequeDate);

        } catch (Exception e) {

            return true;
        }

        return false;
    }

    // =========================================================
    // 20. MICR VALIDATION
    // =========================================================

    private static boolean isMicrError(
            OutwardCheque cheque) {

        if (cheque == null) {

            return true;
        }

        String micr =
                cheque.getMicr();

        if (isBlank(micr)) {

            return true;
        }

        return !micr.matches(
                "^[0-9]{9}$"
        );
    }

    // =========================================================
    // 21. AMOUNT / ACCOUNT VALIDATION
    // =========================================================

    private static boolean isAmountAccountError(
            OutwardCheque cheque) {

        if (cheque == null) {

            return true;
        }

        // -----------------------------------------------------
        // ACCOUNT NUMBER
        // -----------------------------------------------------

        String account =
                cheque.getAccountNumber();

        if (isBlank(account)) {

            return true;
        }

        if (!account.matches(
                "^[0-9]{12}$")) {

            return true;
        }

        // -----------------------------------------------------
        // AMOUNT
        // -----------------------------------------------------

        String amount =
                cheque.getAmount();

        if (isBlank(amount)) {

            return true;
        }

        try {

            BigDecimal value =
                    new BigDecimal(amount);

            if (value.compareTo(
                    BigDecimal.ZERO) <= 0) {

                return true;
            }

        } catch (NumberFormatException e) {

            return true;
        }

        return false;
    }

    // =========================================================
    // 22. BLANK CHECK
    // =========================================================

    private static boolean isBlank(
            String value) {

        return value == null
                || value.trim().isEmpty();
    }

    // =========================================================
    // 23. MAP RESULTSET TO OUTWARD CHEQUE
    // =========================================================

    private static OutwardCheque mapCheque(
            ResultSet rs)
            throws SQLException {

        OutwardCheque cheque =
                new OutwardCheque();

        // -----------------------------------------------------
        // CHEQUE ID
        // -----------------------------------------------------

        cheque.setChequeId(
                rs.getString("cheque_id")
        );

        // -----------------------------------------------------
        // BATCH ID
        // -----------------------------------------------------

        cheque.setBatchId(
                rs.getString("batch_id")
        );

        // -----------------------------------------------------
        // CHEQUE NUMBER
        // -----------------------------------------------------

        cheque.setChequeNumber(
                rs.getString("cheque_number")
        );

        // -----------------------------------------------------
        // ACCOUNT NUMBER
        // -----------------------------------------------------

        cheque.setAccountNumber(
                rs.getString("account_number")
        );

        // -----------------------------------------------------
        // CHEQUE DATE
        // -----------------------------------------------------

        Date chequeDate =
                rs.getDate("cheque_date");

        if (chequeDate != null) {

            cheque.setChequeDate(
                    chequeDate.toString()
            );
        }

        // -----------------------------------------------------
        // AMOUNT
        // -----------------------------------------------------

        BigDecimal amount =
                rs.getBigDecimal("amount");

        if (amount != null) {

            cheque.setAmount(
                    amount.toPlainString()
            );
        }

        // -----------------------------------------------------
        // IMAGE PATHS
        // -----------------------------------------------------

        cheque.setFrontImage(
                rs.getString("front_image")
        );

        cheque.setBackImage(
                rs.getString("back_image")
        );

        // -----------------------------------------------------
        // ORIGINAL SCANNED MICR
        // -----------------------------------------------------

        cheque.setMicr(
                rs.getString("scanned_micr")
        );

        // -----------------------------------------------------
        // CORRECTED MICR
        // -----------------------------------------------------

        cheque.setCorrectMicr(
                rs.getString("corrected_micr")
        );

        // -----------------------------------------------------
        // CORRECTED ACCOUNT
        // -----------------------------------------------------

        cheque.setCorrectAccountNumber(
                rs.getString(
                        "corrected_account_number"
                )
        );

        // -----------------------------------------------------
        // CORRECTED CHEQUE DATE
        // -----------------------------------------------------

        Date correctedDate =
                rs.getDate(
                        "corrected_cheque_date"
                );

        if (correctedDate != null) {

            cheque.setCorrectChequeDate(
                    correctedDate.toString()
            );
        }

        // -----------------------------------------------------
        // CORRECTED AMOUNT
        // -----------------------------------------------------

        BigDecimal correctedAmount =
                rs.getBigDecimal(
                        "corrected_amount"
                );

        if (correctedAmount != null) {

            cheque.setCorrectAmount(
                    correctedAmount.toPlainString()
            );
        }

        return cheque;
    }
}

