package com.iispl.cts.dao.outward.checker;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.iispl.cts.data.CTSStaticData;
import com.iispl.cts.model.outward.ChequeProcessing;
import com.iispl.cts.model.outward.OutwardCheque;
import com.iispl.cts.model.outward.ReturnReason;

public class CheckerChequeDAO {

    /*
     * Get a particular cheque.
     */
    public OutwardCheque getCheque(
            String batchNumber,
            String chequeNumber) {

        String sql =
                "SELECT batch_number, " +
                "       cheque_number, " +
                "       drawer_account_number, " +
                "       drawer_name, " +
                "       payee_account_number, " +
                "       payee_name, " +
                "       amount, " +
                "       amount_in_words, " +
                "       cheque_date, " +
                "       front_image_path, " +
                "       back_image_path, " +
                "       cheque_status, " +
                "       bank_code, " +
                "       branch_code, " +
                "       city_code, " +
                "       return_reason_id, " +
                "       checker_remarks " +
                "FROM outward_cheque " +
                "WHERE batch_number = ? " +
                "AND cheque_number = ?";

        try (Connection connection = CTSStaticData.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setString(1, batchNumber);
            statement.setString(2, chequeNumber);

            try (ResultSet rs = statement.executeQuery()) {

                if (rs.next()) {

                    OutwardCheque cheque = new OutwardCheque();

                    cheque.setBatchNumber(
                            rs.getString("batch_number"));

                    cheque.setChequeNumber(
                            rs.getString("cheque_number"));

                    cheque.setDrawerAccountNumber(
                            rs.getString("drawer_account_number"));

                    cheque.setDrawerName(
                            rs.getString("drawer_name"));

                    cheque.setPayeeAccountNumber(
                            rs.getString("payee_account_number"));

                    cheque.setPayeeName(
                            rs.getString("payee_name"));

                    cheque.setAmount(
                            rs.getBigDecimal("amount"));

                    cheque.setAmountInWords(
                            rs.getString("amount_in_words"));

                    Date chequeDate =
                            rs.getDate("cheque_date");

                    if (chequeDate != null) {
                        cheque.setChequeDate(
                                chequeDate.toLocalDate());
                    }

                    cheque.setFrontImagePath(
                            rs.getString("front_image_path"));

                    cheque.setBackImagePath(
                            rs.getString("back_image_path"));

                    cheque.setChequeStatus(
                            rs.getString("cheque_status"));

                    cheque.setBankCode(
                            rs.getString("bank_code"));

                    cheque.setBranchCode(
                            rs.getString("branch_code"));

                    cheque.setCityCode(
                            rs.getString("city_code"));

                    Integer returnReasonId =
                            (Integer) rs.getObject("return_reason_id");

                    cheque.setReturnReasonId(returnReasonId);

                    cheque.setCheckerRemarks(
                            rs.getString("checker_remarks"));

                    return cheque;
                }
            }

        } catch (Exception e) {
            e.printStackTrace();

            throw new RuntimeException(
                    "Error while fetching cheque details",
                    e);
        }

        return null;
    }


    /*
     * Get Maker and Checker processing information
     * for a particular cheque.
     */
    public ChequeProcessing getChequeProcessing(
            String batchNumber,
            String chequeNumber) {

        String sql =
                "SELECT batch_number, " +
                "       cheque_number, " +
                "       maker_id, " +
                "       maker_action, " +
                "       maker_reason_id, " +
                "       checker_id, " +
                "       checker_action, " +
                "       checker_reason_id " +
                "FROM cheque_processing " +
                "WHERE batch_number = ? " +
                "AND cheque_number = ?";

        try (Connection connection = CTSStaticData.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setString(1, batchNumber);
            statement.setString(2, chequeNumber);

            try (ResultSet rs = statement.executeQuery()) {

                if (rs.next()) {

                    ChequeProcessing processing =
                            new ChequeProcessing();

                    processing.setBatchNumber(
                            rs.getString("batch_number"));

                    processing.setChequeNumber(
                            rs.getString("cheque_number"));

                    processing.setMakerId(
                            (Integer) rs.getObject("maker_id"));

                    processing.setMakerAction(
                            rs.getString("maker_action"));

                    processing.setMakerReasonId(
                            (Integer) rs.getObject("maker_reason_id"));

                    processing.setCheckerId(
                            (Integer) rs.getObject("checker_id"));

                    processing.setCheckerAction(
                            rs.getString("checker_action"));

                    processing.setCheckerReasonId(
                            (Integer) rs.getObject("checker_reason_id"));

                    return processing;
                }
            }

        } catch (Exception e) {
            e.printStackTrace();

            throw new RuntimeException(
                    "Error while fetching cheque processing details",
                    e);
        }

        return null;
    }


    /*
     * Get all active return/rejection reasons.
     */
    public List<ReturnReason> getReturnReasons() {

        List<ReturnReason> reasons =
                new ArrayList<>();

        String sql =
                "SELECT id, " +
                "       reason_code, " +
                "       reason_name, " +
                "       active " +
                "FROM return_reason_master " +
                "WHERE active = true " +
                "ORDER BY reason_name";

        try (Connection connection = CTSStaticData.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql);
             ResultSet rs = statement.executeQuery()) {

            while (rs.next()) {

                ReturnReason reason =
                        new ReturnReason();

                reason.setId(
                        rs.getInt("id"));

                reason.setReasonCode(
                        rs.getString("reason_code"));

                reason.setReasonName(
                        rs.getString("reason_name"));

                reason.setActive(
                        rs.getBoolean("active"));

                reasons.add(reason);
            }

        } catch (Exception e) {
            e.printStackTrace();

            throw new RuntimeException(
                    "Error while fetching return reasons",
                    e);
        }

        return reasons;
    }


    /*
     * Save Checker decision.
     *
     * ACCEPT:
     *     reason is not required
     *
     * REJECT:
     *     reason is mandatory
     *
     * SEND_BACK:
     *     reason is mandatory
     */
    public boolean saveCheckerDecision(
            String batchNumber,
            String chequeNumber,
            int checkerId,
            String checkerAction,
            Integer checkerReasonId) {

        String updateProcessingSql =
                "UPDATE cheque_processing " +
                "SET checker_id = ?, " +
                "    checker_action = ?, " +
                "    checker_reason_id = ? " +
                "WHERE batch_number = ? " +
                "AND cheque_number = ?";

        String updateChequeSql =
                "UPDATE outward_cheque " +
                "SET cheque_status = ?, " +
                "    return_reason_id = ?, " +
                "    checker_remarks = NULL, " +
                "    updated_by = ?, " +
                "    updated_at = CURRENT_TIMESTAMP " +
                "WHERE batch_number = ? " +
                "AND cheque_number = ?";

        String chequeStatus;

        if ("ACCEPT".equalsIgnoreCase(checkerAction)) {

            chequeStatus = "CHECKER_ACCEPTED";

            checkerReasonId = null;

        } else if ("REJECT".equalsIgnoreCase(checkerAction)) {

            if (checkerReasonId == null) {
                throw new IllegalArgumentException(
                        "Reject reason is mandatory");
            }

            chequeStatus = "CHECKER_REJECTED";

        } else if ("SEND_BACK".equalsIgnoreCase(checkerAction)) {

            if (checkerReasonId == null) {
                throw new IllegalArgumentException(
                        "Send Back reason is mandatory");
            }

            chequeStatus = "SENT_BACK_TO_MAKER";

        } else {

            throw new IllegalArgumentException(
                    "Invalid Checker action: "
                            + checkerAction);
        }


        try (Connection connection =
                     CTSStaticData.getConnection()) {

            connection.setAutoCommit(false);

            try {

                /*
                 * First save Checker processing information.
                 */
                int processingRows;

                try (PreparedStatement statement =
                             connection.prepareStatement(
                                     updateProcessingSql)) {

                    statement.setInt(1, checkerId);

                    statement.setString(
                            2,
                            checkerAction.toUpperCase());

                    if (checkerReasonId == null) {

                        statement.setNull(
                                3,
                                java.sql.Types.INTEGER);

                    } else {

                        statement.setInt(
                                3,
                                checkerReasonId);
                    }

                    statement.setString(
                            4,
                            batchNumber);

                    statement.setString(
                            5,
                            chequeNumber);

                    processingRows =
                            statement.executeUpdate();
                }

                if (processingRows != 1) {

                    connection.rollback();

                    throw new RuntimeException(
                            "Cheque processing record not found");
                }


                /*
                 * Then update the current cheque status.
                 */
                int chequeRows;

                try (PreparedStatement statement =
                             connection.prepareStatement(
                                     updateChequeSql)) {

                    statement.setString(
                            1,
                            chequeStatus);

                    if (checkerReasonId == null) {

                        statement.setNull(
                                2,
                                java.sql.Types.INTEGER);

                    } else {

                        statement.setInt(
                                2,
                                checkerReasonId);
                    }

                    statement.setString(
                            3,
                            String.valueOf(checkerId));

                    statement.setString(
                            4,
                            batchNumber);

                    statement.setString(
                            5,
                            chequeNumber);

                    chequeRows =
                            statement.executeUpdate();
                }

                if (chequeRows != 1) {

                    connection.rollback();

                    throw new RuntimeException(
                            "Cheque record not found");
                }

                connection.commit();

                return true;

            } catch (Exception e) {

                connection.rollback();

                throw e;
            }

        } catch (Exception e) {

            e.printStackTrace();

            throw new RuntimeException(
                    "Error while saving Checker decision",
                    e);
        }
    }


    /*
     * Get a particular return reason by ID.
     */
    public ReturnReason getReturnReasonById(
            int reasonId) {

        String sql =
                "SELECT id, " +
                "       reason_code, " +
                "       reason_name, " +
                "       active " +
                "FROM return_reason_master " +
                "WHERE id = ?";

        try (Connection connection =
                     CTSStaticData.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setInt(1, reasonId);

            try (ResultSet rs =
                         statement.executeQuery()) {

                if (rs.next()) {

                    ReturnReason reason =
                            new ReturnReason();

                    reason.setId(
                            rs.getInt("id"));

                    reason.setReasonCode(
                            rs.getString("reason_code"));

                    reason.setReasonName(
                            rs.getString("reason_name"));

                    reason.setActive(
                            rs.getBoolean("active"));

                    return reason;
                }
            }

        } catch (Exception e) {

            e.printStackTrace();

            throw new RuntimeException(
                    "Error while fetching return reason: "
                            + reasonId,
                    e);
        }

        return null;
    }


    /*
     * Get CBS account details for validation.
     *
     * Returns account details when the account exists.
     * Returns null when the account does not exist.
     */
    public Map<String, String> getCbsAccount(
            String accountNumber) {

        String sql =
                "SELECT account_number, " +
                "       account_holder_name, " +
                "       account_status " +
                "FROM account_master " +
                "WHERE account_number = ?";

        Map<String, String> account = null;

        try (Connection con =
                     CTSStaticData.getConnection();
             PreparedStatement ps =
                     con.prepareStatement(sql)) {

            ps.setString(1, accountNumber);

            try (ResultSet rs =
                         ps.executeQuery()) {

                if (rs.next()) {

                    account = new HashMap<>();

                    account.put(
                            "accountNumber",
                            rs.getString(
                                    "account_number"));

                    account.put(
                            "accountHolderName",
                            rs.getString(
                                    "account_holder_name"));

                    account.put(
                            "accountStatus",
                            rs.getString(
                                    "account_status"));
                }
            }

        } catch (Exception e) {

            e.printStackTrace();
        }

        return account;
    }
}