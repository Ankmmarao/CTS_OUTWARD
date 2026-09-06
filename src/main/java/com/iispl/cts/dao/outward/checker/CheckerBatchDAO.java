package com.iispl.cts.dao.outward.checker;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import com.iispl.cts.data.CTSStaticData;
import com.iispl.cts.model.outward.OutwardBatch;
import com.iispl.cts.model.outward.OutwardCheque;

public class CheckerBatchDAO {

    // =========================================================
    // GET CHEQUES BY BATCH NUMBER
    // =========================================================

    public List<OutwardCheque> getChequesByBatchId(String batchId) {

        List<OutwardCheque> cheques =
                new ArrayList<>();

        String sql =
                "SELECT "
                + "batch_number, "
                + "cheque_number, "
                + "drawer_account_number, "
                + "drawer_name, "
                + "payee_account_number, "
                + "payee_name, "
                + "amount, "
                + "amount_in_words, "
                + "cheque_date, "
                + "front_image_path, "
                + "back_image_path, "
                + "cheque_status, "
                + "bank_code, "
                + "branch_code, "
                + "city_code "
                + "FROM public.outward_cheque "
                + "WHERE batch_number = ? "
                + "ORDER BY cheque_number";

        try (
                Connection con =
                        CTSStaticData.getConnection();

                PreparedStatement ps =
                        con.prepareStatement(sql)
        ) {

            ps.setString(
                    1,
                    batchId.trim()
            );

            try (ResultSet rs =
                    ps.executeQuery()) {

                while (rs.next()) {

                    OutwardCheque cheque =
                            new OutwardCheque();

                    // =================================================
                    // BATCH NUMBER
                    // =================================================

                    cheque.setBatchNumber(
                            rs.getString(
                                    "batch_number"
                            )
                    );

                    // =================================================
                    // CHEQUE NUMBER
                    // =================================================

                    cheque.setChequeNumber(
                            rs.getString(
                                    "cheque_number"
                            )
                    );

                    // =================================================
                    // CITY CODE
                    // =================================================

                    cheque.setCityCode(
                            rs.getString(
                                    "city_code"
                            )
                    );

                    // =================================================
                    // BANK CODE
                    // =================================================

                    cheque.setBankCode(
                            rs.getString(
                                    "bank_code"
                            )
                    );

                    // =================================================
                    // BRANCH CODE
                    // =================================================

                    cheque.setBranchCode(
                            rs.getString(
                                    "branch_code"
                            )
                    );

                    // =================================================
                    // DRAWER ACCOUNT NUMBER
                    // =================================================

                    cheque.setDrawerAccountNumber(
                            rs.getString(
                                    "drawer_account_number"
                            )
                    );

                    // =================================================
                    // DRAWER NAME
                    // =================================================

                    cheque.setDrawerName(
                            rs.getString(
                                    "drawer_name"
                            )
                    );

                    // =================================================
                    // DEPOSITOR ACCOUNT NUMBER
                    //
                    // Java model field:
                    // depositorAccountNumber
                    //
                    // Database column:
                    // payee_account_number
                    // =================================================

                    cheque.setDepositorAccountNumber(
                            rs.getString(
                                    "payee_account_number"
                            )
                    );

                    // =================================================
                    // PAYEE NAME
                    // =================================================

                    cheque.setPayeeName(
                            rs.getString(
                                    "payee_name"
                            )
                    );

                    // =================================================
                    // AMOUNT
                    // =================================================

                    cheque.setAmount(
                            rs.getBigDecimal(
                                    "amount"
                            )
                    );

                    // =================================================
                    // AMOUNT IN WORDS
                    // =================================================

                    cheque.setAmountInWords(
                            rs.getString(
                                    "amount_in_words"
                            )
                    );

                    // =================================================
                    // CHEQUE DATE
                    // =================================================

                    if (rs.getDate(
                            "cheque_date"
                    ) != null) {

                        cheque.setChequeDate(
                                rs.getDate(
                                        "cheque_date"
                                ).toLocalDate()
                        );
                    }

                    // =================================================
                    // FRONT IMAGE
                    // =================================================

                    cheque.setFrontImagePath(
                            rs.getString(
                                    "front_image_path"
                            )
                    );

                    // =================================================
                    // BACK IMAGE
                    // =================================================

                    cheque.setBackImagePath(
                            rs.getString(
                                    "back_image_path"
                            )
                    );

                    // =================================================
                    // CHEQUE STATUS
                    // =================================================

                    cheque.setChequeStatus(
                            rs.getString(
                                    "cheque_status"
                            )
                    );

                    cheques.add(cheque);
                }
            }

        } catch (Exception e) {

            throw new RuntimeException(
                    "Unable to load cheques for batch: "
                    + batchId
                    + "\nDatabase Error: "
                    + e.getMessage(),
                    e
            );
        }

        return cheques;
    }

    // =========================================================
    // GET ALL BATCHES FOR CHECKER QUEUE
    // =========================================================

    public List<OutwardBatch> getCheckerBatches() {

        List<OutwardBatch> batches =
                new ArrayList<>();

        System.out.println();
        System.out.println(
                "=============================================="
        );
        System.out.println(
                "CHECKER DAO: getCheckerBatches() STARTED"
        );
        System.out.println(
                "=============================================="
        );

        /*
         * FOR NOW:
         *
         * Load ALL batches.
         *
         * No status filtering is applied.
         *
         * This means the Checker Queue can display:
         *
         * CAPTURED
         * ASSIGNED
         * IN_PROGRESS
         * COMPLETED
         * REJECTED
         * and any other existing status.
         *
         * Assignment and status workflow
         * will be implemented later.
         */

        String sql =
                "SELECT "
                + "batch_number, "
                + "branch_code, "
                + "cheque_count, "
                + "batch_folder_path, "
                + "created_by, "
                + "created_at, "
                + "batch_status "
                + "FROM public.outward_batch "
                + "ORDER BY created_at DESC";

        System.out.println(
                "CHECKER DAO: Loading ALL batches..."
        );

        try (
                Connection con =
                        CTSStaticData.getConnection();

                PreparedStatement ps =
                        con.prepareStatement(sql);

                ResultSet rs =
                        ps.executeQuery()
        ) {

            System.out.println(
                    "CHECKER DAO: Query executed successfully"
            );

            while (rs.next()) {

                OutwardBatch batch =
                        new OutwardBatch();

                // =================================================
                // BATCH NUMBER
                // =================================================

                batch.setBatchNumber(
                        rs.getString(
                                "batch_number"
                        )
                );

                // =================================================
                // BRANCH CODE
                // =================================================

                batch.setBranchCode(
                        rs.getString(
                                "branch_code"
                        )
                );

                // =================================================
                // NUMBER OF CHEQUES
                // =================================================

                batch.setNumberOfCheques(
                        rs.getInt(
                                "cheque_count"
                        )
                );

                // =================================================
                // BATCH FOLDER PATH
                // =================================================

                batch.setBatchFolderPath(
                        rs.getString(
                                "batch_folder_path"
                        )
                );

                // =================================================
                // CREATED BY
                // =================================================

                batch.setCreatedBy(
                        String.valueOf(
                                rs.getInt(
                                        "created_by"
                                )
                        )
                );

                // =================================================
                // CREATED AT
                // =================================================

                if (rs.getTimestamp(
                        "created_at"
                ) != null) {

                    batch.setCreatedAt(
                            rs.getTimestamp(
                                    "created_at"
                            ).toLocalDateTime()
                    );
                }

                // =================================================
                // BATCH STATUS
                // =================================================

                batch.setBatchStatus(
                        rs.getString(
                                "batch_status"
                        )
                );

                /*
                 * Assignment/locking is NOT being processed yet.
                 *
                 * We will implement this later.
                 */

                batch.setLockedBy(null);
                batch.setLockStatus(null);

                batches.add(batch);

                System.out.println(
                        "CHECKER DAO BATCH:"
                        + " Batch="
                        + batch.getBatchNumber()
                        + " | Branch="
                        + batch.getBranchCode()
                        + " | Cheques="
                        + batch.getNumberOfCheques()
                        + " | Status="
                        + batch.getBatchStatus()
                );
            }

            System.out.println();
            System.out.println(
                    "CHECKER DAO: TOTAL BATCHES FOUND = "
                    + batches.size()
            );

        } catch (Exception e) {

            System.out.println(
                    "CHECKER DAO: DATABASE ERROR"
            );

            e.printStackTrace();

            throw new RuntimeException(
                    "Unable to load checker queue batches."
                    + "\nDatabase Error: "
                    + e.getMessage(),
                    e
            );
        }

        System.out.println(
                "CHECKER DAO: getCheckerBatches() FINISHED"
        );

        System.out.println(
                "=============================================="
        );

        return batches;
    }
}