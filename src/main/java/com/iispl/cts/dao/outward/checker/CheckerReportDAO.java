package com.iispl.cts.dao.outward.checker;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import com.iispl.cts.data.CTSStaticData;
import com.iispl.cts.model.outward.OutwardBatch;

public class CheckerReportDAO {

    /*
     * Get completed Checker batches.
     *
     * These batches are available in the Reports section.
     */
    public List<OutwardBatch> getCompletedBatches() {

        List<OutwardBatch> batches = new ArrayList<>();

        String sql =
                "SELECT ob.batch_number, " +
                "       ob.branch_code, " +
                "       ob.cheque_count, " +
                "       ob.batch_folder_path, " +
                "       ob.created_by, " +
                "       ob.created_at, " +
                "       ob.batch_status " +
                "FROM outward_batch ob " +
                "WHERE UPPER(ob.batch_status) = 'COMPLETED' " +
                "ORDER BY ob.created_at DESC";

        try (Connection connection = CTSStaticData.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql);
             ResultSet rs = statement.executeQuery()) {

            while (rs.next()) {

                OutwardBatch batch = new OutwardBatch();

                batch.setBatchNumber(
                        rs.getString("batch_number"));

                batch.setBranchCode(
                        rs.getString("branch_code"));

                batch.setNumberOfCheques(
                        rs.getInt("cheque_count"));

                batch.setBatchFolderPath(
                        rs.getString("batch_folder_path"));

                batch.setCreatedBy(
                        String.valueOf(
                                rs.getInt("created_by")));

                if (rs.getTimestamp("created_at") != null) {
                    batch.setCreatedAt(
                            rs.getTimestamp("created_at")
                                    .toLocalDateTime());
                }

                batch.setBatchStatus(
                        rs.getString("batch_status"));

                batches.add(batch);
            }

        } catch (Exception e) {
            e.printStackTrace();

            throw new RuntimeException(
                    "Error while fetching completed Checker batches",
                    e);
        }

        return batches;
    }


    /*
     * Get batch summary.
     *
     * Used by the Reports screen before generating
     * CXF / CIBF files.
     */
    public ResultSet getBatchSummary(String batchNumber) {

        String sql =
                "SELECT summary_id, " +
                "       batch_number, " +
                "       total_cheques, " +
                "       processed_cheques, " +
                "       rejected_cheques, " +
                "       total_amount, " +
                "       completed_date " +
                "FROM outward_batch_summary " +
                "WHERE batch_number = ?";

        try {
            Connection connection =
                    CTSStaticData.getConnection();

            PreparedStatement statement =
                    connection.prepareStatement(sql);

            statement.setString(1, batchNumber);

            return statement.executeQuery();

        } catch (Exception e) {
            e.printStackTrace();

            throw new RuntimeException(
                    "Error while fetching batch summary: "
                            + batchNumber,
                    e);
        }
    }


    /*
     * Get total number of cheques in a batch.
     */
    public int getTotalChequeCount(String batchNumber) {

        String sql =
                "SELECT COUNT(*) " +
                "FROM outward_cheque " +
                "WHERE batch_number = ?";

        try (Connection connection = CTSStaticData.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setString(1, batchNumber);

            try (ResultSet rs = statement.executeQuery()) {

                if (rs.next()) {
                    return rs.getInt(1);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();

            throw new RuntimeException(
                    "Error while getting cheque count: "
                            + batchNumber,
                    e);
        }

        return 0;
    }


    /*
     * Get number of accepted cheques.
     */
    public int getAcceptedChequeCount(String batchNumber) {

        String sql =
                "SELECT COUNT(*) " +
                "FROM outward_cheque " +
                "WHERE batch_number = ? " +
                "AND UPPER(cheque_status) = 'CHECKER_ACCEPTED'";

        return getCount(batchNumber, sql);
    }


    /*
     * Get number of rejected cheques.
     */
    public int getRejectedChequeCount(String batchNumber) {

        String sql =
                "SELECT COUNT(*) " +
                "FROM outward_cheque " +
                "WHERE batch_number = ? " +
                "AND UPPER(cheque_status) = 'CHECKER_REJECTED'";

        return getCount(batchNumber, sql);
    }


    /*
     * Get number of cheques sent back to Maker.
     */
    public int getSentBackChequeCount(String batchNumber) {

        String sql =
                "SELECT COUNT(*) " +
                "FROM outward_cheque " +
                "WHERE batch_number = ? " +
                "AND UPPER(cheque_status) = 'SENT_BACK_TO_MAKER'";

        return getCount(batchNumber, sql);
    }


    /*
     * Common count method.
     */
    private int getCount(
            String batchNumber,
            String sql) {

        try (Connection connection = CTSStaticData.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setString(1, batchNumber);

            try (ResultSet rs = statement.executeQuery()) {

                if (rs.next()) {
                    return rs.getInt(1);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();

            throw new RuntimeException(
                    "Error while getting Checker batch count",
                    e);
        }

        return 0;
    }
}