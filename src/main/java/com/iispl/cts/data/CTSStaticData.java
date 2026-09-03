
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
    // DATABASE CONNECTION
    // =========================================================
	 public static final String DRIVER =
	            "org.postgresql.Driver";

	    public static final String   DB_URL =
	            "jdbc:postgresql://db.bijnscklhxftxritxdrc.supabase.co:5432/postgres";

	    public static final String DB_USER =
	            "postgres";

	    public static final String  DB_PASSWORD=
	            "Sushmabandari@123";

    static {
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(
                    "PostgreSQL JDBC Driver not found", e);
        }
    }

    private CTSStaticData() {
    }

    private static Connection getConnection()
            throws SQLException {

        return DriverManager.getConnection(
                DB_URL,
                DB_USER,
                DB_PASSWORD
        );
    }


    // =========================================================
    // MAKER DASHBOARD
    // =========================================================

    public static List<OutwardBatch> getMakerDashboardBatches() {

        List<OutwardBatch> result =
                new ArrayList<>();

        String sql =
                "SELECT " +
                "ob.batch_id, " +
                "ob.total_cheques, " +
                "ob.status, " +

                "COUNT(CASE " +
                "WHEN ve.error_type = 'DATA_ENTRY_ERROR' " +
                "AND ve.error_status = 'OPEN' " +
                "THEN 1 END) AS data_entry_errors, " +

                "COUNT(CASE " +
                "WHEN ve.error_type = 'MICR_ERROR' " +
                "AND ve.error_status = 'OPEN' " +
                "THEN 1 END) AS micr_errors, " +

                "COUNT(CASE " +
                "WHEN ve.error_type = 'AMOUNT_ACCOUNT_ERROR' " +
                "AND ve.error_status = 'OPEN' " +
                "THEN 1 END) AS amount_account_errors " +

                "FROM outward_batch ob " +

                "LEFT JOIN outward_cheque oc " +
                "ON ob.batch_id = oc.batch_id " +

                "LEFT JOIN cheque_validation_error ve " +
                "ON oc.cheque_id = ve.cheque_id " +

                "GROUP BY " +
                "ob.batch_id, " +
                "ob.total_cheques, " +
                "ob.status " +

                "ORDER BY ob.batch_id";

        try (Connection con = getConnection();
             PreparedStatement ps =
                     con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {

                OutwardBatch batch =
                        new OutwardBatch();

                batch.setBatchId(
                        rs.getString("batch_id"));

                batch.setTotalCheques(
                        rs.getInt("total_cheques"));

                batch.setStatus(
                        rs.getString("status"));

                batch.setDataEntryErrorCount(
                        rs.getInt("data_entry_errors"));

                batch.setMicrErrorCount(
                        rs.getInt("micr_errors"));

                batch.setAmountAccountErrorCount(
                        rs.getInt("amount_account_errors"));

                batch.refreshTotalErrorCount();

                result.add(batch);
            }

        } catch (SQLException e) {

            throw new RuntimeException(
                    "Unable to load Maker dashboard batches",
                    e);
        }

        return result;
    }


    // =========================================================
    // MAKER DASHBOARD CHEQUES
    // =========================================================

    public static List<OutwardCheque>
    getMakerDashboardCheques(String batchId) {

        List<OutwardCheque> result =
                new ArrayList<>();

        String sql =
                "SELECT " +
                "oc.cheque_id, " +
                "oc.batch_id, " +
                "oc.cheque_number, " +
                "oc.account_number, " +
                "oc.cheque_date, " +
                "oc.amount, " +
                "oc.front_image, " +
                "oc.back_image, " +
                "oc.scanned_micr, " +
                "oc.corrected_micr, " +
                "oc.corrected_account_number, " +
                "oc.corrected_cheque_date, " +
                "oc.corrected_amount " +

                "FROM outward_cheque oc " +

                "WHERE oc.batch_id = ? " +

                "ORDER BY oc.cheque_id";

        try (Connection con = getConnection();
             PreparedStatement ps =
                     con.prepareStatement(sql)) {

            ps.setString(1, batchId);

            try (ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {

                    result.add(
                            mapCheque(rs)
                    );
                }
            }

        } catch (SQLException e) {

            throw new RuntimeException(
                    "Unable to load cheques for batch "
                            + batchId,
                    e);
        }

        return result;
    }


    // =========================================================
    // DATA ENTRY BATCHES
    // =========================================================

    public static List<OutwardBatch>
    getDataEntryBatches() {

        return getBatchesByErrorType(
                "DATA_ENTRY_ERROR");
    }


    // =========================================================
    // MICR REPAIR BATCHES
    // =========================================================

    public static List<OutwardBatch>
    getMicrRepairBatches() {

        return getBatchesByErrorType(
                "MICR_ERROR");
    }


    // =========================================================
    // AMOUNT & ACCOUNT BATCHES
    // =========================================================

    public static List<OutwardBatch>
    getAmountAccountBatches() {

        return getBatchesByErrorType(
                "AMOUNT_ACCOUNT_ERROR");
    }


    // =========================================================
    // COMMON ERROR BATCH QUERY
    // =========================================================

    private static List<OutwardBatch>
    getBatchesByErrorType(String errorType) {

        List<OutwardBatch> result =
                new ArrayList<>();

        String sql =
                "SELECT " +
                "ob.batch_id, " +
                "ob.total_cheques, " +
                "ob.status, " +

                "COUNT(*) AS error_count " +

                "FROM outward_batch ob " +

                "JOIN outward_cheque oc " +
                "ON ob.batch_id = oc.batch_id " +

                "JOIN cheque_validation_error ve " +
                "ON oc.cheque_id = ve.cheque_id " +

                "WHERE ve.error_type = ? " +
                "AND ve.error_status = 'OPEN' " +

                "GROUP BY " +
                "ob.batch_id, " +
                "ob.total_cheques, " +
                "ob.status " +

                "ORDER BY ob.batch_id";

        try (Connection con = getConnection();
             PreparedStatement ps =
                     con.prepareStatement(sql)) {

            ps.setString(1, errorType);

            try (ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {

                    OutwardBatch batch =
                            new OutwardBatch();

                    batch.setBatchId(
                            rs.getString("batch_id"));

                    batch.setTotalCheques(
                            rs.getInt("total_cheques"));

                    batch.setStatus(
                            rs.getString("status"));

                    int count =
                            rs.getInt("error_count");

                    if ("DATA_ENTRY_ERROR"
                            .equals(errorType)) {

                        batch.setDataEntryErrorCount(
                                count);

                    } else if ("MICR_ERROR"
                            .equals(errorType)) {

                        batch.setMicrErrorCount(
                                count);

                    } else if ("AMOUNT_ACCOUNT_ERROR"
                            .equals(errorType)) {

                        batch.setAmountAccountErrorCount(
                                count);
                    }

                    batch.refreshTotalErrorCount();

                    result.add(batch);
                }
            }

        } catch (SQLException e) {

            throw new RuntimeException(
                    "Unable to load " + errorType
                            + " batches",
                    e);
        }

        return result;
    }


    // =========================================================
    // CHECKER BATCHES
    // =========================================================

    public static List<OutwardBatch>
    getCheckerBatches() {

        List<OutwardBatch> result =
                new ArrayList<>();

        String sql =
                "SELECT " +
                "ob.batch_id, " +
                "ob.total_cheques, " +
                "ob.status " +

                "FROM outward_batch ob " +

                "WHERE NOT EXISTS ( " +

                "SELECT 1 " +

                "FROM outward_cheque oc " +

                "JOIN cheque_validation_error ve " +
                "ON oc.cheque_id = ve.cheque_id " +

                "WHERE oc.batch_id = ob.batch_id " +
                "AND ve.error_status = 'OPEN' " +

                ") " +

                "ORDER BY ob.batch_id";

        try (Connection con = getConnection();
             PreparedStatement ps =
                     con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {

                OutwardBatch batch =
                        new OutwardBatch();

                batch.setBatchId(
                        rs.getString("batch_id"));

                batch.setTotalCheques(
                        rs.getInt("total_cheques"));

                batch.setStatus(
                        rs.getString("status"));

                batch.setDataEntryErrorCount(0);
                batch.setMicrErrorCount(0);
                batch.setAmountAccountErrorCount(0);

                batch.refreshTotalErrorCount();

                result.add(batch);
            }

        } catch (SQLException e) {

            throw new RuntimeException(
                    "Unable to load checker batches",
                    e);
        }

        return result;
    }


    // =========================================================
    // DATA ENTRY CHEQUES
    // =========================================================

    public static List<OutwardCheque>
    getDataEntryCheques(String batchId) {

        return getChequesByErrorType(
                batchId,
                "DATA_ENTRY_ERROR");
    }


    // =========================================================
    // MICR CHEQUES
    // =========================================================

    public static List<OutwardCheque>
    getMicrCheques(String batchId) {

        return getChequesByErrorType(
                batchId,
                "MICR_ERROR");
    }


    // =========================================================
    // AMOUNT & ACCOUNT CHEQUES
    // =========================================================

    public static List<OutwardCheque>
    getAmountAccountCheques(String batchId) {

        return getChequesByErrorType(
                batchId,
                "AMOUNT_ACCOUNT_ERROR");
    }


    // =========================================================
    // GET CHEQUES BY ERROR TYPE
    // =========================================================

    private static List<OutwardCheque>
    getChequesByErrorType(
            String batchId,
            String errorType) {

        List<OutwardCheque> result =
                new ArrayList<>();

        String sql =
                "SELECT " +

                "oc.cheque_id, " +
                "oc.batch_id, " +
                "oc.cheque_number, " +
                "oc.account_number, " +
                "oc.cheque_date, " +
                "oc.amount, " +
                "oc.front_image, " +
                "oc.back_image, " +
                "oc.scanned_micr, " +
                "oc.corrected_micr, " +
                "oc.corrected_account_number, " +
                "oc.corrected_cheque_date, " +
                "oc.corrected_amount, " +

                "ve.error_type, " +
                "ve.error_field, " +
                "ve.error_message " +

                "FROM outward_cheque oc " +

                "JOIN cheque_validation_error ve " +
                "ON oc.cheque_id = ve.cheque_id " +

                "WHERE oc.batch_id = ? " +
                "AND ve.error_type = ? " +
                "AND ve.error_status = 'OPEN' " +

                "ORDER BY oc.cheque_id";

        try (Connection con = getConnection();
             PreparedStatement ps =
                     con.prepareStatement(sql)) {

            ps.setString(1, batchId);
            ps.setString(2, errorType);

            try (ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {

                    result.add(
                            mapCheque(rs)
                    );
                }
            }

        } catch (SQLException e) {

            throw new RuntimeException(
                    "Unable to load "
                            + errorType
                            + " cheques",
                    e);
        }

        return result;
    }


    // =========================================================
    // ASSIGN BATCH
    // =========================================================

    public static boolean assignBatch(
            String batchId,
            String userId) {

        String checkSql =
                "SELECT status " +
                "FROM outward_batch " +
                "WHERE batch_id = ? " +
                "FOR UPDATE";

        String insertSql =
                "INSERT INTO batch_assignment " +
                "(batch_id, user_id, assignment_type, status) " +
                "VALUES (?, ?, 'MAKER', 'ACTIVE')";

        String updateSql =
                "UPDATE outward_batch " +
                "SET status = 'ASSIGNED', " +
                "updated_at = CURRENT_TIMESTAMP " +
                "WHERE batch_id = ?";

        try (Connection con = getConnection()) {

            con.setAutoCommit(false);

            try {

                String status;

                try (PreparedStatement ps =
                             con.prepareStatement(checkSql)) {

                    ps.setString(1, batchId);

                    try (ResultSet rs =
                                 ps.executeQuery()) {

                        if (!rs.next()) {
                            con.rollback();
                            return false;
                        }

                        status =
                                rs.getString("status");
                    }
                }

                // Another maker already has it
                if (!"AVAILABLE".equals(status)) {

                    con.rollback();
                    return false;
                }

                try (PreparedStatement ps =
                             con.prepareStatement(insertSql)) {

                    ps.setString(1, batchId);
                    ps.setString(2, userId);

                    ps.executeUpdate();
                }

                try (PreparedStatement ps =
                             con.prepareStatement(updateSql)) {

                    ps.setString(1, batchId);

                    ps.executeUpdate();
                }

                con.commit();

                return true;

            } catch (Exception e) {

                con.rollback();
                throw e;
            }

        } catch (Exception e) {

            throw new RuntimeException(
                    "Unable to assign batch "
                            + batchId,
                    e);
        }
    }


    // =========================================================
    // COMPLETE DATA ENTRY CHEQUE
    // =========================================================

    public static void completeDataEntryCheque(
            String batchId,
            String chequeId) {

        resolveChequeError(
                batchId,
                chequeId,
                "DATA_ENTRY_ERROR",
                null,
                null
        );
    }


    // =========================================================
    // COMPLETE MICR CHEQUE
    // =========================================================

    public static void completeMicrCheque(
            String batchId,
            String chequeId) {

        resolveChequeError(
                batchId,
                chequeId,
                "MICR_ERROR",
                null,
                null
        );
    }


    // =========================================================
    // COMPLETE AMOUNT & ACCOUNT CHEQUE
    // =========================================================

    public static void completeAmountAccountCheque(
            String batchId,
            String chequeId) {

        resolveChequeError(
                batchId,
                chequeId,
                "AMOUNT_ACCOUNT_ERROR",
                null,
                null
        );
    }


    // =========================================================
    // RESOLVE ERROR
    // =========================================================

    private static void resolveChequeError(
            String batchId,
            String chequeId,
            String errorType,
            String userId,
            String remarks) {

        String sql =
                "UPDATE cheque_validation_error " +
                "SET error_status = 'RESOLVED', " +
                "resolved_at = CURRENT_TIMESTAMP, " +
                "resolved_by = ?, " +
                "remarks = ? " +
                "WHERE cheque_id = ? " +
                "AND error_type = ? " +
                "AND error_status = 'OPEN'";

        try (Connection con = getConnection();
             PreparedStatement ps =
                     con.prepareStatement(sql)) {

            ps.setString(1, userId);
            ps.setString(2, remarks);
            ps.setLong(
                    3,
                    Long.parseLong(chequeId));
            ps.setString(4, errorType);

            ps.executeUpdate();

            updateBatchIfCompleted(
                    batchId);

        } catch (SQLException e) {

            throw new RuntimeException(
                    "Unable to resolve cheque error",
                    e);
        }
    }


    // =========================================================
    // REJECT DATA ENTRY
    // =========================================================

    public static void rejectDataEntryCheque(
            String batchId,
            String chequeId,
            String reason) {

        rejectCheque(
                batchId,
                chequeId,
                "DATA_ENTRY_ERROR",
                reason
        );
    }


    // =========================================================
    // REJECT MICR
    // =========================================================

    public static void rejectMicrCheque(
            String batchId,
            String chequeId,
            String reason) {

        rejectCheque(
                batchId,
                chequeId,
                "MICR_ERROR",
                reason
        );
    }


    // =========================================================
    // REJECT AMOUNT & ACCOUNT
    // =========================================================

    public static void rejectAmountAccountCheque(
            String batchId,
            String chequeId,
            String reason) {

        rejectCheque(
                batchId,
                chequeId,
                "AMOUNT_ACCOUNT_ERROR",
                reason
        );
    }


    // =========================================================
    // REJECT CHEQUE
    // =========================================================

    private static void rejectCheque(
            String batchId,
            String chequeId,
            String errorType,
            String reason) {

        String sql =
                "UPDATE cheque_validation_error " +
                "SET error_status = 'REJECTED', " +
                "resolved_at = CURRENT_TIMESTAMP, " +
                "remarks = ? " +
                "WHERE cheque_id = ? " +
                "AND error_type = ? " +
                "AND error_status = 'OPEN'";

        try (Connection con = getConnection();
             PreparedStatement ps =
                     con.prepareStatement(sql)) {

            ps.setString(1, reason);

            ps.setLong(
                    2,
                    Long.parseLong(chequeId));

            ps.setString(
                    3,
                    errorType);

            ps.executeUpdate();

            updateBatchIfCompleted(
                    batchId);

        } catch (SQLException e) {

            throw new RuntimeException(
                    "Unable to reject cheque",
                    e);
        }
    }


    // =========================================================
    // UPDATE BATCH STATUS
    // =========================================================

    private static void updateBatchIfCompleted(
            String batchId) {

        String sql =
                "UPDATE outward_batch " +
                "SET status = 'COMPLETED', " +
                "updated_at = CURRENT_TIMESTAMP " +
                "WHERE batch_id = ? " +
                "AND NOT EXISTS ( " +

                "SELECT 1 " +
                "FROM outward_cheque oc " +

                "JOIN cheque_validation_error ve " +
                "ON oc.cheque_id = ve.cheque_id " +

                "WHERE oc.batch_id = ? " +
                "AND ve.error_status = 'OPEN' " +

                ")";

        try (Connection con = getConnection();
             PreparedStatement ps =
                     con.prepareStatement(sql)) {

            ps.setString(1, batchId);
            ps.setString(2, batchId);

            ps.executeUpdate();

        } catch (SQLException e) {

            throw new RuntimeException(
                    "Unable to update batch status",
                    e);
        }
    }


    // =========================================================
    // FIND CHEQUE
    // =========================================================

    public static OutwardCheque findCheque(
            String batchId,
            String chequeId) {

        String sql =
                "SELECT " +
                "oc.cheque_id, " +
                "oc.batch_id, " +
                "oc.cheque_number, " +
                "oc.account_number, " +
                "oc.cheque_date, " +
                "oc.amount, " +
                "oc.front_image, " +
                "oc.back_image, " +
                "oc.scanned_micr, " +
                "oc.corrected_micr, " +
                "oc.corrected_account_number, " +
                "oc.corrected_cheque_date, " +
                "oc.corrected_amount " +

                "FROM outward_cheque oc " +

                "WHERE oc.batch_id = ? " +
                "AND oc.cheque_id = ?";

        try (Connection con = getConnection();
             PreparedStatement ps =
                     con.prepareStatement(sql)) {

            ps.setString(1, batchId);

            ps.setLong(
                    2,
                    Long.parseLong(chequeId));

            try (ResultSet rs =
                         ps.executeQuery()) {

                if (rs.next()) {

                    return mapCheque(rs);
                }
            }

        } catch (SQLException e) {

            throw new RuntimeException(
                    "Unable to find cheque",
                    e);
        }

        return null;
    }


    // =========================================================
    // MAP RESULTSET → OUTWARD CHEQUE
    // =========================================================

    private static OutwardCheque mapCheque(
            ResultSet rs)
            throws SQLException {

        OutwardCheque cheque =
                new OutwardCheque();

        cheque.setChequeId(
                String.valueOf(
                        rs.getString("cheque_id")));

        cheque.setBatchId(
                rs.getString("batch_id"));

        cheque.setChequeNumber(
                rs.getString("cheque_number"));

        cheque.setAccountNumber(
                rs.getString("account_number"));

        Date chequeDate =
                rs.getDate("cheque_date");

        if (chequeDate != null) {

            cheque.setChequeDate(
                    chequeDate.toString());
        }

        BigDecimal amount =
                rs.getBigDecimal("amount");

        if (amount != null) {

            cheque.setAmount(
                    amount.toPlainString());
        }

        cheque.setFrontImage(
                rs.getString("front_image"));

        cheque.setBackImage(
                rs.getString("back_image"));

        return cheque;
    }


    // =========================================================
    // FRONT IMAGE
    // =========================================================

    public static String getFrontImage() {

        return "https://placehold.co/900x400?text=Cheque+Front";
    }


    // =========================================================
    // BACK IMAGE
    // =========================================================

    public static String getBackImage() {

        return "https://placehold.co/900x400?text=Cheque+Back";
    }
}
