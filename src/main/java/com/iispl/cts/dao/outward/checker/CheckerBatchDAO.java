package com.iispl.cts.dao.outward.checker;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.iispl.cts.data.CTSStaticData;
import com.iispl.cts.model.outward.OutwardBatch;
import com.iispl.cts.model.outward.OutwardCheque;

public class CheckerBatchDAO {

    /*
     * Get batches currently assigned to a particular Checker.
     */
    public List<OutwardBatch> getCheckerBatches(String checkerUserId) {

        List<OutwardBatch> batches = new ArrayList<>();

        String sql =
                "SELECT ob.batch_number, " +
                "       ob.branch_code, " +
                "       ob.cheque_count, " +
                "       ob.batch_folder_path, " +
                "       ob.created_by, " +
                "       ob.created_at, " +
                "       ob.batch_status, " +
                "       oba.user_id, " +
                "       oba.assignment_status, " +
                "       oba.assigned_at, " +
                "       oba.started_at, " +
                "       oba.completed_at " +
                "FROM outward_batch ob " +
                "JOIN outward_batch_assignment oba " +
                "  ON ob.batch_number = oba.batch_number " +
                "WHERE oba.user_id = ? " +
                "  AND UPPER(oba.assignment_role) = 'CHECKER' " +
                "  AND UPPER(oba.assignment_status) IN ('ASSIGNED', 'IN_PROGRESS') " +
                "ORDER BY oba.assigned_at DESC";

        try (Connection connection = CTSStaticData.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, Integer.parseInt(checkerUserId));

            try (ResultSet rs = statement.executeQuery()) {

                while (rs.next()) {

                    OutwardBatch batch = new OutwardBatch();

                    batch.setBatchNumber(rs.getString("batch_number"));
                    batch.setBranchCode(rs.getString("branch_code"));
                    batch.setNumberOfCheques(rs.getInt("cheque_count"));
                    batch.setBatchFolderPath(rs.getString("batch_folder_path"));
                    batch.setCreatedBy(String.valueOf(rs.getInt("created_by")));
                    batch.setCreatedAt(
                            rs.getTimestamp("created_at") != null
                                    ? rs.getTimestamp("created_at").toLocalDateTime()
                                    : null
                    );
                    batch.setBatchStatus(rs.getString("batch_status"));

                    /*
                     * Existing OutwardBatch fields used by Checker UI.
                     */
                    batch.setCheckerUserNumber(checkerUserId);

                    if (rs.getTimestamp("started_at") != null) {
                        batch.setCheckerStartedAt(
                                rs.getTimestamp("started_at").toLocalDateTime()
                        );
                    }

                    if (rs.getTimestamp("completed_at") != null) {
                        batch.setCheckerCompletedAt(
                                rs.getTimestamp("completed_at").toLocalDateTime()
                        );
                    }

                    batch.setLockStatus(rs.getString("assignment_status"));

                    batches.add(batch);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error while fetching Checker batches", e);
        }

        return batches;
    }


    /*
     * Get all batches which have been submitted to Checker.
     * These batches are visible in the common Checker queue.
     */
    public List<OutwardBatch> getSubmittedBatches() {

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
                "WHERE UPPER(ob.batch_status) = 'SUBMITTED_TO_CHECKER' " +
                "ORDER BY ob.created_at ASC";

        try (Connection connection = CTSStaticData.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet rs = statement.executeQuery()) {

            while (rs.next()) {

                OutwardBatch batch = new OutwardBatch();

                batch.setBatchNumber(rs.getString("batch_number"));
                batch.setBranchCode(rs.getString("branch_code"));
                batch.setNumberOfCheques(rs.getInt("cheque_count"));
                batch.setBatchFolderPath(rs.getString("batch_folder_path"));
                batch.setCreatedBy(String.valueOf(rs.getInt("created_by")));

                if (rs.getTimestamp("created_at") != null) {
                    batch.setCreatedAt(
                            rs.getTimestamp("created_at").toLocalDateTime()
                    );
                }

                batch.setBatchStatus(rs.getString("batch_status"));

                batches.add(batch);
            }

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error while fetching submitted Checker batches", e);
        }

        return batches;
    }


    /*
     * Get a single batch by batch number.
     */
    public OutwardBatch getBatchByNumber(String batchNumber) {

        String sql =
                "SELECT batch_number, " +
                "       branch_code, " +
                "       cheque_count, " +
                "       batch_folder_path, " +
                "       created_by, " +
                "       created_at, " +
                "       batch_status " +
                "FROM outward_batch " +
                "WHERE batch_number = ?";

        try (Connection connection = CTSStaticData.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, batchNumber);

            try (ResultSet rs = statement.executeQuery()) {

                if (rs.next()) {

                    OutwardBatch batch = new OutwardBatch();

                    batch.setBatchNumber(rs.getString("batch_number"));
                    batch.setBranchCode(rs.getString("branch_code"));
                    batch.setNumberOfCheques(rs.getInt("cheque_count"));
                    batch.setBatchFolderPath(rs.getString("batch_folder_path"));
                    batch.setCreatedBy(String.valueOf(rs.getInt("created_by")));
                    batch.setBatchStatus(rs.getString("batch_status"));

                    if (rs.getTimestamp("created_at") != null) {
                        batch.setCreatedAt(
                                rs.getTimestamp("created_at").toLocalDateTime()
                        );
                    }

                    return batch;
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error while fetching batch: " + batchNumber, e);
        }

        return null;
    }


    /*
     * Get all cheques belonging to a batch.
     */
    public List<OutwardCheque> getChequesByBatchNumber(String batchNumber) {

        List<OutwardCheque> cheques = new ArrayList<>();

        String sql =
                "SELECT batch_number, " +
                "       cheque_number, " +
                "       city_code, " +
                "       bank_code, " +
                "       branch_code, " +
                "       drawer_account_number, " +
                "       drawer_name, " +
                "       depositor_account_number, " +
                "       depositor_name, " +
                "       payee_account_number, " +
                "       payee_name, " +
                "       amount, " +
                "       amount_in_words, " +
                "       cheque_date, " +
                "       front_image_path, " +
                "       back_image_path, " +
                "       cheque_status, " +
                "       return_reason_id, " +
                "       checker_remarks, " +
                "       created_by, " +
                "       created_at, " +
                "       updated_by, " +
                "       updated_at " +
                "FROM outward_cheque " +
                "WHERE batch_number = ? " +
                "ORDER BY cheque_number";

        try (Connection connection = CTSStaticData.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, batchNumber);

            try (ResultSet rs = statement.executeQuery()) {

                while (rs.next()) {

                    OutwardCheque cheque = new OutwardCheque();

                    cheque.setBatchNumber(rs.getString("batch_number"));
                    cheque.setChequeNumber(rs.getString("cheque_number"));

                    cheque.setCityCode(rs.getString("city_code"));
                    cheque.setBankCode(rs.getString("bank_code"));
                    cheque.setBranchCode(rs.getString("branch_code"));

                    cheque.setDrawerAccountNumber(
                            rs.getString("drawer_account_number")
                    );

                    cheque.setDrawerName(
                            rs.getString("drawer_name")
                    );

                    cheque.setDepositorAccountNumber(
                            rs.getString("depositor_account_number")
                    );

                    cheque.setDepositorName(
                            rs.getString("depositor_name")
                    );

                    cheque.setPayeeAccountNumber(
                            rs.getString("payee_account_number")
                    );

                    cheque.setPayeeName(
                            rs.getString("payee_name")
                    );

                    BigDecimal amount = rs.getBigDecimal("amount");
                    cheque.setAmount(amount);

                    cheque.setAmountInWords(
                            rs.getString("amount_in_words")
                    );

                    if (rs.getDate("cheque_date") != null) {
                        cheque.setChequeDate(
                                rs.getDate("cheque_date").toLocalDate()
                        );
                    }

                    cheque.setFrontImagePath(
                            rs.getString("front_image_path")
                    );

                    cheque.setBackImagePath(
                            rs.getString("back_image_path")
                    );

                    cheque.setChequeStatus(
                            rs.getString("cheque_status")
                    );

                    Integer reasonId = (Integer) rs.getObject("return_reason_id");
                    cheque.setReturnReasonId(reasonId);

                    cheque.setCheckerRemarks(
                            rs.getString("checker_remarks")
                    );

                    cheque.setCreatedBy(
                            rs.getString("created_by")
                    );

                    if (rs.getTimestamp("created_at") != null) {
                        cheque.setCreatedAt(
                                rs.getTimestamp("created_at").toLocalDateTime()
                        );
                    }

                    cheque.setUpdatedBy(
                            rs.getString("updated_by")
                    );

                    if (rs.getTimestamp("updated_at") != null) {
                        cheque.setUpdatedAt(
                                rs.getTimestamp("updated_at").toLocalDateTime()
                        );
                    }

                    cheques.add(cheque);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(
                    "Error while fetching cheques for batch: " + batchNumber,
                    e
            );
        }

        return cheques;
    }


    /*
     * Existing compatibility method.
     */
    public List<OutwardCheque> getChequesByBatchId(String batchId) {
        return getChequesByBatchNumber(batchId);
    }


    /*
     * Check whether an account exists in account_master.
     */
    public boolean accountExists(String accountNumber) {

        String sql =
                "SELECT 1 " +
                "FROM account_master " +
                "WHERE account_number = ?";

        try (Connection connection = CTSStaticData.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, accountNumber);

            try (ResultSet rs = statement.executeQuery()) {
                return rs.next();
            }

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(
                    "Error while checking account: " + accountNumber,
                    e
            );
        }
    }
}