package com.iispl.cts.service.outward;

import com.iispl.cts.dao.outward.OutwardMakerDashboardDAO;
import com.iispl.cts.model.outward.OutwardBatch;
import com.iispl.cts.model.outward.OutwardCheque;
import com.iispl.cts.model.outward.OutwardValidationResult;

import java.sql.SQLException;
import java.util.List;

public class OutwardMakerDashboardService {

    private final OutwardMakerDashboardDAO dao;

    // ============================================================
    // CONSTRUCTOR
    // ============================================================

    public OutwardMakerDashboardService() {

        this.dao = new OutwardMakerDashboardDAO();
    }

    // ============================================================
    // GET BATCHES
    // ============================================================

    public List<OutwardBatch> getBatches()
            throws SQLException {

        return dao.getBatches();
    }

    // ============================================================
    // FIND BATCH
    // ============================================================

    public OutwardBatch findBatch(
            String batchNumber)
            throws SQLException {

        if (batchNumber == null
                || batchNumber.trim().isEmpty()) {

            return null;
        }

        List<OutwardBatch> batches =
                dao.getBatches();

        if (batches == null) {
            return null;
        }

        for (OutwardBatch batch : batches) {

            if (batch != null
                    && batchNumber.trim()
                            .equalsIgnoreCase(
                                    batch.getBatchNumber()
                            )) {

                return batch;
            }
        }

        return null;
    }

    // ============================================================
    // ASSIGN BATCH
    // ============================================================

    public boolean assignBatch(
            String batchNumber,
            String userId)
            throws SQLException {

        if (batchNumber == null
                || batchNumber.trim().isEmpty()) {

            return false;
        }

        if (userId == null
                || userId.trim().isEmpty()) {

            return false;
        }

        return dao.assignBatch(
                batchNumber.trim(),
                userId.trim()
        );
    }

    // ============================================================
    // GET CHEQUES
    // ============================================================

    public List<OutwardCheque> getCheques(
            String batchNumber)
            throws SQLException {

        if (batchNumber == null
                || batchNumber.trim().isEmpty()) {

            return null;
        }

        return dao.getCheques(
                batchNumber.trim()
        );
    }

    // ============================================================
    // VALIDATE BATCH
    // ============================================================

    public boolean isBatchValid(
            String batchNumber)
            throws SQLException {

        if (batchNumber == null
                || batchNumber.trim().isEmpty()) {

            return false;
        }

        return dao.isBatchValid(
                batchNumber.trim()
        );
    }

    // ============================================================
    // ASSIGN + VALIDATE
    // ============================================================
    //
    // IMPORTANT:
    //
    // This method now returns OutwardValidationResult
    // because the Controller uses:
    //
    //     result.getDataEntryErrors()
    //     result.getMicrErrors()
    //     result.getAmountAccountErrors()
    //
    // ============================================================

    public OutwardValidationResult assignAndValidate(
            String batchNumber,
            String userId)
            throws SQLException {

        // --------------------------------------------------------
        // BASIC VALIDATION
        // --------------------------------------------------------

        if (batchNumber == null
                || batchNumber.trim().isEmpty()) {

            return null;
        }

        if (userId == null
                || userId.trim().isEmpty()) {

            return null;
        }

        String cleanBatchNumber =
                batchNumber.trim();

        String cleanUserId =
                userId.trim();

        // --------------------------------------------------------
        // ASSIGN BATCH
        // --------------------------------------------------------

        boolean assigned =
                dao.assignBatch(
                        cleanBatchNumber,
                        cleanUserId
                );

        // --------------------------------------------------------
        // ASSIGNMENT FAILED
        // --------------------------------------------------------

        if (!assigned) {

            return null;
        }

        // --------------------------------------------------------
        // GET CHEQUES
        // --------------------------------------------------------

        List<OutwardCheque> cheques =
                dao.getCheques(
                        cleanBatchNumber
                );

        // --------------------------------------------------------
        // NO CHEQUES
        // --------------------------------------------------------

        if (cheques == null
                || cheques.isEmpty()) {

            /*
             * Assignment succeeded but the batch
             * contains no cheque records.
             *
             * Return a validation result with zero
             * cheque count rather than returning
             * boolean.
             */

            return createEmptyValidationResult();
        }

        // --------------------------------------------------------
        // CREATE VALIDATION RESULT
        // --------------------------------------------------------

        OutwardValidationResult result =
                new OutwardValidationResult();

        result.setTotalCheques(
                cheques.size()
        );

        int dataEntryErrors = 0;
        int micrErrors = 0;
        int amountAccountErrors = 0;

        // --------------------------------------------------------
        // VALIDATE EACH CHEQUE
        // --------------------------------------------------------

        for (OutwardCheque cheque : cheques) {

            if (cheque == null) {
                continue;
            }

            // ----------------------------------------------------
            // DATA ENTRY VALIDATION
            // ----------------------------------------------------

            if (isDataEntryError(cheque)) {

                dataEntryErrors++;
            }

            // ----------------------------------------------------
            // MICR VALIDATION
            // ----------------------------------------------------

            if (isMicrError(cheque)) {

                micrErrors++;
            }

            // ----------------------------------------------------
            // AMOUNT / ACCOUNT VALIDATION
            // ----------------------------------------------------

            if (isAmountAccountError(cheque)) {

                amountAccountErrors++;
            }
        }

        // --------------------------------------------------------
        // SET VALIDATION COUNTS
        // --------------------------------------------------------

        result.setDataEntryErrors(
                dataEntryErrors
        );

        result.setMicrErrors(
                micrErrors
        );

        result.setAmountAccountErrors(
                amountAccountErrors
        );

        return result;
    }

    // ============================================================
    // DATA ENTRY VALIDATION
    // ============================================================

    private boolean isDataEntryError(
            OutwardCheque cheque) {

        /*
         * Required cheque data:
         *
         * cheque number
         * drawer account
         * drawer name
         * amount
         *
         * These fields exist in the current
         * OutwardCheque model.
         */

        if (isEmpty(
                cheque.getChequeNumber()
        )) {

            return true;
        }

        if (isEmpty(
                cheque.getDrawerAccountNumber()
        )) {

            return true;
        }

        if (isEmpty(
                cheque.getDrawerName()
        )) {

            return true;
        }

        if (cheque.getAmount() == null) {

            return true;
        }

        return false;
    }

    // ============================================================
    // MICR VALIDATION
    // ============================================================

    private boolean isMicrError(
            OutwardCheque cheque) {

        /*
         * Your current OutwardCheque model does not contain
         * a micrCode property.
         *
         * Therefore MICR validation cannot be performed
         * directly here without changing the model.
         *
         * The DAO can handle MICR validation if your
         * database contains micr_code.
         */

        return false;
    }

    // ============================================================
    // AMOUNT / ACCOUNT VALIDATION
    // ============================================================

    private boolean isAmountAccountError(
            OutwardCheque cheque) {

        // --------------------------------------------------------
        // AMOUNT
        // --------------------------------------------------------

        if (cheque.getAmount() == null) {

            return true;
        }

        if (cheque.getAmount()
                .signum() <= 0) {

            return true;
        }

        // --------------------------------------------------------
        // DRAWER ACCOUNT
        // --------------------------------------------------------

        if (isEmpty(
                cheque.getDrawerAccountNumber()
        )) {

            return true;
        }

        return false;
    }

    // ============================================================
    // EMPTY VALIDATION RESULT
    // ============================================================

    private OutwardValidationResult
    createEmptyValidationResult() {

        OutwardValidationResult result =
                new OutwardValidationResult();

        result.setTotalCheques(0);

        result.setDataEntryErrors(0);

        result.setMicrErrors(0);

        result.setAmountAccountErrors(0);

        return result;
    }

    // ============================================================
    // STRING CHECK
    // ============================================================

    private boolean isEmpty(
            String value) {

        return value == null
                || value.trim().isEmpty();
    }

    // ============================================================
    // COMPLETE BATCH
    // ============================================================

    public void completeBatch(
            String batchNumber)
            throws SQLException {

        if (batchNumber == null
                || batchNumber.trim().isEmpty()) {

            return;
        }

        dao.updateBatchIfCompleted(
                batchNumber.trim()
        );
    }
}